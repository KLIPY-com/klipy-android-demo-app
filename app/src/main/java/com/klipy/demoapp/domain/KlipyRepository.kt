package com.klipy.demoapp.domain

import com.klipy.demoapp.domain.models.Category
import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.presentation.features.conversation.model.MediaType


interface KlipyRepository {
    suspend fun getAvailableMediaTypes(): List<MediaType>

    suspend fun getCategories(mediaType: MediaType): Result<List<Category>>

    suspend fun getMediaData(mediaType: MediaType, filter: String) : Result<List<MediaItem>>

    suspend fun triggerShare(mediaType: MediaType, id: String): Result<Any>

    suspend fun triggerView(mediaType: MediaType, id: String): Result<Any>

    suspend fun report(mediaType: MediaType, id: String, reason: String): Result<Any>

    suspend fun hideFromRecent(mediaType: MediaType, id: String): Result<Any>
}