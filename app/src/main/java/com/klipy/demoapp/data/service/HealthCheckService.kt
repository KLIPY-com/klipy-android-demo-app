package com.klipy.demoapp.data.service

import com.klipy.demoapp.data.dto.HealthCheckResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Service to check if media type services are up and running
 */
interface HealthCheckService {

    @GET("health-check")
    suspend fun healthCheck(@Query("customer_id") customerId: String): Response<HealthCheckResponseDto>
}