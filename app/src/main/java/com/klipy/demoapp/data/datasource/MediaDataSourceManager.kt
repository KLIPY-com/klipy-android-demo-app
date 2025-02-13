package com.klipy.demoapp.data.datasource

import com.klipy.demoapp.presentation.features.conversation.model.MediaType

interface MediaDataSourceManager {
    fun getDataSource(mediaType: MediaType): MediaDataSource
}

class MediaDataSourceManagerImpl(
    private val mediaDataSourceFactory: MediaDataSourceFactory
) : MediaDataSourceManager {

    private var lastMediaType: MediaType? = null

    override fun getDataSource(mediaType: MediaType): MediaDataSource {
        val dataSource = mediaDataSourceFactory.getDataSource(mediaType)
        // If media type changed, we need to reset (paging, e.t.c) previously selected datasource
        if (mediaType != lastMediaType) {
            lastMediaType = mediaType
            dataSource.reset()
        }
        return dataSource
    }
}

interface MediaDataSourceFactory {
    fun getDataSource(mediaType: MediaType): MediaDataSource
}

class MediaDataSourceFactoryImpl(
    private val dataSources: List<MediaDataSource>
) : MediaDataSourceFactory {

    override fun getDataSource(mediaType: MediaType): MediaDataSource = when (mediaType) {
        MediaType.GIF -> dataSources.firstOrNull { it is GifsDataSource }
        MediaType.STICKER -> dataSources.firstOrNull { it is StickersDataSource }
        MediaType.CLIP -> dataSources.firstOrNull { it is ClipsDataSource }
        else -> throw IllegalArgumentException("No DataSource found for $mediaType")
    } ?: throw IllegalArgumentException("No DataSource found for $mediaType")
}

