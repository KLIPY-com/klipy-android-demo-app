package com.klipy.demoapp.data.datasource

import com.klipy.demoapp.data.infrastructure.ApiCallHelper
import com.klipy.demoapp.data.infrastructure.DeviceInfoProvider
import com.klipy.demoapp.data.service.HealthCheckService
import com.klipy.demoapp.presentation.features.conversation.model.MediaType

interface HealthCheckDataSource {
    suspend fun getHealthyMediaTypes(): List<MediaType>
}

class HealthCheckDataSourceImpl(
    private val apiCallHelper: ApiCallHelper,
    private val healthCheckService: HealthCheckService,
    private val deviceInfoProvider: DeviceInfoProvider
): HealthCheckDataSource {

    override suspend fun getHealthyMediaTypes(): List<MediaType> {
        return apiCallHelper.makeApiCall {
            healthCheckService.healthCheck(deviceInfoProvider.getDeviceId())
        }.mapCatching { result ->
            listOfNotNull(
                MediaType.GIF.takeIf { result.gifs?.isAlive == true },
                MediaType.CLIP.takeIf { result.stickers?.isAlive == true },
                MediaType.STICKER.takeIf { result.stickers?.isAlive == true }
            )
        }.getOrElse { emptyList() }
    }

}