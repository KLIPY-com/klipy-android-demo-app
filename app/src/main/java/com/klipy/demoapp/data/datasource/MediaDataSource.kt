package com.klipy.demoapp.data.datasource

import com.klipy.demoapp.domain.models.Category
import com.klipy.demoapp.domain.models.MediaItem

/**
 * General interface of datasource for each media types: GIF, Clip, Sticker
 */
interface MediaDataSource {
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getMediaData(filter: String, page: Int?): Result<List<MediaItem>>
    suspend fun triggerShare(slug: String): Result<Any>
    suspend fun triggerView(slug: String): Result<Any>
    suspend fun report(slug: String, reason: String): Result<Any>
    suspend fun hideFromRecent(slug: String): Result<Any>
    fun reset()
}
