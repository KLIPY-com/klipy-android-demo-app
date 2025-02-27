package com.klipy.demoapp.presentation.features.conversation

import com.klipy.demoapp.domain.KlipyRepository
import com.klipy.demoapp.domain.models.Category
import com.klipy.demoapp.domain.models.MediaData
import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.presentation.algorithm.MasonryMeasurementsCalculator
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
            val category = categories.find { it.title.lowercase() == TRENDING }
            val data = category?.let {
                    klipyRepository.getMediaData(mediaType, category.title).getOrNull()
                        ?: MediaData.EMPTY
            } ?: MediaData.EMPTY
            MasonryMeasurementsCalculator.itemMinWidth = data.itemMinWidth
            MasonryMeasurementsCalculator.adMaxResizePercentage = data.adMaxResizePercentage
            onInitialDataFetched.invoke(categories, category, data.mediaItems)
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
                    klipyRepository.getMediaData(mediaType, filter).getOrNull() ?: MediaData.EMPTY
                if (data.mediaItems.isEmpty()) {
                    canLoadMore = false
                } else {
                    MasonryMeasurementsCalculator.itemMinWidth = data.itemMinWidth
                    MasonryMeasurementsCalculator.adMaxResizePercentage = data.adMaxResizePercentage
                }
                onMoreDataFetched.invoke(data.mediaItems)
            }
        }
    }

    private companion object {
        const val TRENDING = "trending"
    }
}