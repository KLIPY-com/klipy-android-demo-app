package com.klipy.demoapp.presentation.features.conversation

import com.klipy.demoapp.domain.KlipyRepository
import com.klipy.demoapp.domain.models.Category
import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.presentation.features.conversation.model.MediaType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MediaTypeDataInteractor(
    private val mediaType: MediaType,
    private val klipyRepository: KlipyRepository,
    private val coroutineScope: CoroutineScope,
    private var onInitialDataFetched: (List<Category>, Category?, List<MediaItem>) -> Unit,
    private var onMoreDataFetched: (List<MediaItem>) -> Unit
) {
    private var canLoadMore = true
    private var filter = ""

    fun fetchInitialData() {
        coroutineScope.launch {
            canLoadMore = true
            val categories = klipyRepository.getCategories(mediaType).getOrNull() ?: emptyList()
            if (categories.isEmpty()) {
                onInitialDataFetched.invoke(categories, null, emptyList())
                canLoadMore = false
                return@launch
            }
            var category = categories.first()
            var data =
                klipyRepository.getMediaData(mediaType, category.title).getOrNull() ?: emptyList()
            if (data.isEmpty()) {
                categories.getOrNull(1)?.let {
                    category = it
                    data = klipyRepository.getMediaData(mediaType, category.title).getOrNull()
                        ?: emptyList()
                } ?: run {
                    canLoadMore = false
                }
            }
            onInitialDataFetched.invoke(categories, category, data)
        }
    }

    fun onCategoryChosen(category: Category) {
        filter = category.title
        canLoadMore = true
        fetchMoreData()
    }

    fun onSearchInputEntered(input: String) {
        filter = input
        canLoadMore = true
        fetchMoreData()
    }

    fun fetchMoreData() {
        if (canLoadMore) {
            coroutineScope.launch {
                val data =
                    klipyRepository.getMediaData(mediaType, filter).getOrNull() ?: emptyList()
                if (data.isEmpty()) {
                    canLoadMore = false
                }
                onMoreDataFetched.invoke(data)
            }
        }
    }
}