package com.klipy.demoapp.data.datasource

import com.klipy.demoapp.data.mapper.MediaItemMapper
import com.klipy.demoapp.data.service.GifService
import com.klipy.demoapp.data.infrastructure.ApiCallHelper
import com.klipy.demoapp.data.infrastructure.DeviceInfoProvider

/**
 * This class is implementing the interface MediaDataSource
 * by delegating all of its public members to an instance of MediaDataSourceImpl
 */
class GifsDataSource(
    private val apiCallHelper: ApiCallHelper,
    private val gifService: GifService,
    private val mapper: MediaItemMapper,
    private val deviceInfoProvider: DeviceInfoProvider
): MediaDataSource by MediaDataSourceImpl(apiCallHelper, gifService, mapper, deviceInfoProvider)