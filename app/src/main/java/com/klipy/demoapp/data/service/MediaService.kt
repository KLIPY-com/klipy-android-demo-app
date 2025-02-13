package com.klipy.demoapp.data.service

import com.klipy.demoapp.data.dto.CategoriesResponseDto
import com.klipy.demoapp.data.dto.MediaItemResponseDto
import com.klipy.demoapp.data.dto.request.ReportRequestDto
import com.klipy.demoapp.data.dto.request.TriggerViewRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * General interface of services for GIFS, CLIPS and STICKERS
 * They share same functionality and structure, only endpoints vary
 */
interface MediaService {
    suspend fun getCategories(): Response<CategoriesResponseDto>

    suspend fun getRecent(
        @Path("customer_id") customerId: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<MediaItemResponseDto>

    suspend fun getTrending(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<MediaItemResponseDto>

    suspend fun search(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<MediaItemResponseDto>

    suspend fun triggerShare(
        @Path("slug") slug: String,
        @Body request: TriggerViewRequestDto
    ): Response<Any>

    suspend fun triggerView(
        @Path("slug") slug: String,
        @Body request: TriggerViewRequestDto
    ): Response<Any>

    suspend fun report(
        @Path("slug") slug: String,
        @Body request: ReportRequestDto
    ): Response<Any>

    suspend fun hideFromRecent(
        @Path("customerId") customerId: String,
        @Query("slug") slug: String
    ): Response<Any>
}