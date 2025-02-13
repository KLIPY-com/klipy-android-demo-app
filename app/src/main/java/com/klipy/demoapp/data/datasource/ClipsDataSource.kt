package com.klipy.demoapp.data.datasource

import com.klipy.demoapp.data.infrastructure.ApiCallHelper
import com.klipy.demoapp.data.infrastructure.DeviceInfoProvider
import com.klipy.demoapp.data.mapper.MediaItemMapper
import com.klipy.demoapp.data.service.ClipsService

/**
 * This class is implementing the interface MediaDataSource
 * by delegating all of its public members to an instance of MediaDataSourceImpl
 */
class ClipsDataSource(
    private val apiCallHelper: ApiCallHelper,
    private val clipsService: ClipsService,
    private val mapper: MediaItemMapper,
    private val deviceInfoProvider: DeviceInfoProvider
): MediaDataSource by MediaDataSourceImpl(apiCallHelper, clipsService, mapper, deviceInfoProvider)