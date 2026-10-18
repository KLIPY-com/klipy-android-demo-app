package com.klipy.demoapp.presentation.features.conversation

import com.google.gson.Gson
import com.klipy.demoapp.data.dto.CategoriesResponseDto
import com.klipy.demoapp.data.mapper.toCategories
import com.klipy.demoapp.domain.KlipyRepository
import com.klipy.demoapp.domain.models.Category
import com.klipy.demoapp.domain.models.MediaData
import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.domain.models.isRecent
import com.klipy.demoapp.presentation.features.conversation.model.MediaType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.*
import org.junit.Test

/** Covers the picker's category-to-request flow with both observed API response formats. */
class MediaTypeDataInteractorIntegrationTest {
    @Test fun pickerLoadsTrendingAndUsesQueriesForCategorySelectionAndSearch() = runBlocking {
        val categories = parse("""{"result":true,"data":{"locale":"en_US","categories":[
            {"category":"Greetings","query":"hello","preview_url":"https://static.example.invalid/hello.gif"}
        ]}}""")
        val repository = RecordingRepository(Result.success(categories)).apply {
            media = MediaData(listOf(MediaItem("fixture", "Hello", null, null, null, MediaType.GIF)), 100, 0F)
        }
        val initial = CompletableDeferred<Pair<List<Category>, Category?>>()
        val interactor = MediaTypeDataInteractor(MediaType.GIF, repository, this,
            { all, chosen, items ->
                assertEquals("fixture", items.single().id)
                initial.complete(all to chosen)
            }, {})
        interactor.fetchInitialData()
        val (all, chosen) = initial.await()
        assertEquals(listOf("recent", "trending", "Greetings"), all.map { it.title })
        assertEquals("trending", chosen?.query)
        assertTrue(all.first().isRecent())
        assertEquals(listOf("trending"), repository.requests)
        interactor.fetchMoreData()
        yield()
        assertEquals(listOf("trending", "trending"), repository.requests)
        repository.media = MediaData.EMPTY
        interactor.fetchMoreData()
        yield()
        interactor.fetchMoreData()
        yield()
        // An empty page stops further pagination, without losing the active query.
        assertEquals(listOf("trending", "trending", "trending"), repository.requests)
        interactor.onCategoryChosen(all.last())
        yield()
        interactor.onCategoryChosen(all.first())
        yield()
        interactor.onSearchInputEntered("cats")
        yield()
        assertEquals(listOf("trending", "trending", "trending", "hello", "recent", "cats"), repository.requests)
    }

    @Test fun cachedDemoCategoriesKeepTheirExistingQueryAndPreviewContract() {
        val categories = parse("""{"result":true,"data":["hello","lol"]}""")
        assertEquals(listOf("recent", "trending", "hello", "lol"), categories.map { it.query })
        assertEquals("https://api.klipy.com/assets/images/category/hello.png", categories[2].url)
        assertEquals(listOf("recent", "trending"), parse("""{"result":true,"data":[]}""").map { it.query })
        assertEquals(listOf("recent", "trending"), parse("""{"result":true,"data":{"locale":"en_US","categories":[]}}""").map { it.query })
        assertTrue(parse("""{"result":true}""").isEmpty())
    }

    @Test fun categoryFailureProducesAnEmptyInitialStateWithoutLoadingMedia() = runBlocking {
        val repository = RecordingRepository(Result.failure(IllegalStateException("offline")))
        val initial = CompletableDeferred<Triple<List<Category>, Category?, List<MediaItem>>>()
        val interactor = MediaTypeDataInteractor(MediaType.GIF, repository, this,
            { all, chosen, items -> initial.complete(Triple(all, chosen, items)) }, {})
        interactor.fetchInitialData()
        val (all, chosen, items) = initial.await()
        assertTrue(all.isEmpty())
        assertNull(chosen)
        assertTrue(items.isEmpty())
        interactor.fetchMoreData()
        yield()
        assertTrue(repository.requests.isEmpty())
    }

    @Test fun malformedProductionCategoryIsNotGivenAnInventedQuery() {
        assertThrows(com.google.gson.JsonParseException::class.java) {
            parse("""{"result":true,"data":{"categories":[{"category":"Hello","preview_url":"https://example.invalid/hello.gif"}]}}""")
        }
    }

    private fun parse(json: String) = Gson().fromJson(json, CategoriesResponseDto::class.java).toCategories()

    private class RecordingRepository(private val categories: Result<List<Category>>) : KlipyRepository {
        val requests = mutableListOf<String>()
        var media = MediaData.EMPTY
        override suspend fun getCategories(mediaType: MediaType) = categories
        override suspend fun getMediaData(mediaType: MediaType, filter: String): Result<MediaData> {
            requests.add(filter)
            return Result.success(media)
        }
        override suspend fun getAvailableMediaTypes() = listOf(MediaType.GIF)
        override suspend fun triggerShare(mediaType: MediaType, id: String): Result<Any> = error("Unexpected share")
        override suspend fun triggerView(mediaType: MediaType, id: String): Result<Any> = error("Unexpected view")
        override suspend fun report(mediaType: MediaType, id: String, reason: String): Result<Any> = error("Unexpected report")
        override suspend fun hideFromRecent(mediaType: MediaType, id: String): Result<Any> = error("Unexpected hide")
    }
}
