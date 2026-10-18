package com.klipy.demoapp.data.datasource

import com.klipy.demoapp.data.dto.request.ReportRequestDto
import com.klipy.demoapp.data.dto.request.TriggerViewRequestDto
import com.klipy.demoapp.data.infrastructure.ApiCallHelper
import com.klipy.demoapp.data.infrastructure.DeviceInfoProvider
import com.klipy.demoapp.data.mapper.MediaItemMapper
import com.klipy.demoapp.data.mapper.toCategories
import com.klipy.demoapp.data.service.MediaService
import com.klipy.demoapp.domain.models.Category
import com.klipy.demoapp.domain.models.MediaData

/**
 * General implementation of datasource for each media types: GIF, Clip, Sticker
 */
class MediaDataSourceImpl(
    private val apiCallHelper: ApiCallHelper,
    private val mediaService: MediaService,
    private val mediaItemMapper: MediaItemMapper,
    deviceInfoProvider: DeviceInfoProvider
) : MediaDataSource {
    // Store categories in memory
    private var categories = emptyList<Category>()
    private val customerId = deviceInfoProvider.getCustomerId()

    private var currentPage = INITIAL_PAGE
    private var currentFilter = ""
    private var canRequestMoreData = true

    override suspend fun getCategories(): Result<List<Category>> {
        return if (categories.isEmpty()) {
            apiCallHelper.makeApiCall {
                mediaService.getCategories()
            }.mapCatching { result ->
                val mappedList = result.toCategories()
                categories = mappedList
                mappedList
            }
        } else {
            Result.success(categories)
        }
    }

    override suspend fun getMediaData(filter: String, page: Int?): Result<MediaData> {
        if (filter.isEmpty()) return Result.success(MediaData.EMPTY)

        if (filter != currentFilter) {
            // We need to reset state, if search input is changed or other category is selected
            currentPage = INITIAL_PAGE
            canRequestMoreData = true
        }
        currentFilter = filter
        currentPage++
        return if (canRequestMoreData) {
            apiCallHelper.makeApiCall {
                when (filter) {
                    RECENT -> mediaService.getRecent(
                        page = currentPage,
                        perPage = PER_PAGE,
                        customerId = customerId
                    )

                    TRENDING -> mediaService.getTrending(page = currentPage, perPage = PER_PAGE)
                    else -> mediaService.search(
                        query = filter,
                        page = currentPage,
                        perPage = PER_PAGE
                    )
                }
            }.mapCatching {
                canRequestMoreData = it.data?.hasNext == true
                MediaData(
                    mediaItems = it.data?.data?.map { mediaItem ->
                        mediaItemMapper.mapToDomain(mediaItem)
                    } ?: emptyList(),
                    itemMinWidth = it.data?.meta?.itemMinWidth ?: 0,
                    adMaxResizePercentage = (it.data?.meta?.adMaxResizePercentage ?: 0) / 100F
                )
            }.onFailure {
                canRequestMoreData = false
            }
        } else {
            Result.success(MediaData.EMPTY)
        }
    }

    override fun reset() {
        currentPage = INITIAL_PAGE
        currentFilter = ""
    }

    override suspend fun triggerShare(slug: String): Result<Any> {
        return apiCallHelper.makeApiCall {
            mediaService.triggerShare(slug, TriggerViewRequestDto(customerId))
        }
    }

    override suspend fun triggerView(slug: String): Result<Any> {
        return apiCallHelper.makeApiCall {
            mediaService.triggerView(slug, TriggerViewRequestDto(customerId))
        }
    }

    override suspend fun report(slug: String, reason: String): Result<Any> {
        return apiCallHelper.makeApiCall {
            mediaService.report(slug, ReportRequestDto(customerId, reason))
        }
    }

    override suspend fun hideFromRecent(slug: String): Result<Any> {
        return apiCallHelper.makeApiCall {
            mediaService.hideFromRecent(customerId, slug)
        }
    }

    private companion object {
        const val INITIAL_PAGE = 0
        const val PER_PAGE = 50
        const val RECENT = "recent"
        const val TRENDING = "trending"
    }
}
