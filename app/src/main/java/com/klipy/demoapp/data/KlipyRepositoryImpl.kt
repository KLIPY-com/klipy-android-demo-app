package com.klipy.demoapp.data

import com.klipy.demoapp.data.datasource.HealthCheckDataSource
import com.klipy.demoapp.data.datasource.MediaDataSourceSelector
import com.klipy.demoapp.domain.KlipyRepository
import com.klipy.demoapp.domain.models.Category
import com.klipy.demoapp.domain.models.MediaData
import com.klipy.demoapp.presentation.features.conversation.model.MediaType

class KlipyRepositoryImpl(
    private val healthCheckDataSource: HealthCheckDataSource,
    private val mediaDataSourceManager: MediaDataSourceSelector,
) : KlipyRepository {

    override suspend fun getAvailableMediaTypes(): List<MediaType> {
        return healthCheckDataSource.getHealthyMediaTypes()
    }

    override suspend fun getCategories(mediaType: MediaType): Result<List<Category>> {
        return mediaDataSourceManager.getDataSource(mediaType).getCategories()
    }

    override suspend fun getMediaData(mediaType: MediaType, filter: String): Result<MediaData> {
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