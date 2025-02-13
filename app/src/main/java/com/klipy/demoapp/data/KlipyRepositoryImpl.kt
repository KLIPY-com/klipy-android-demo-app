package com.klipy.demoapp.data

import com.klipy.demoapp.data.datasource.HealthCheckDataSource
import com.klipy.demoapp.data.datasource.MediaDataSourceManager
import com.klipy.demoapp.domain.KlipyRepository
import com.klipy.demoapp.domain.models.Category
import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.presentation.features.conversation.model.MediaType

class KlipyRepositoryImpl(
    private val healthCheckDataSource: HealthCheckDataSource,
    private val mediaDataSourceManager: MediaDataSourceManager,
) : KlipyRepository {

    override suspend fun getAvailableMediaTypes(): List<MediaType> {
        return healthCheckDataSource.getHealthyMediaTypes()
    }

    override suspend fun getCategories(mediaType: MediaType): Result<List<Category>> {
        return mediaDataSourceManager.getDataSource(mediaType).getCategories()
    }

    override suspend fun getMediaData(
        mediaType: MediaType,
        filter: String
    ): Result<List<MediaItem>> {
        return mediaDataSourceManager.getDataSource(mediaType).getMediaData(filter, null)
    }

    override suspend fun triggerShare(mediaType: MediaType, id: String): Result<Any> {
        return mediaDataSourceManager.getDataSource(mediaType).triggerShare(id)
    }

    override suspend fun triggerView(mediaType: MediaType, id: String): Result<Any> {
        return mediaDataSourceManager.getDataSource(mediaType).triggerView(id)
    }

    override suspend fun report(mediaType: MediaType, id: String, reason: String): Result<Any> {
        return mediaDataSourceManager.getDataSource(mediaType).report(id, reason)
    }

    override suspend fun hideFromRecent(mediaType: MediaType, id: String): Result<Any> {
        return mediaDataSourceManager.getDataSource(mediaType).hideFromRecent(id)
    }
}