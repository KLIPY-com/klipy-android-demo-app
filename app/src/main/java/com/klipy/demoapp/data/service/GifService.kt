package com.klipy.demoapp.data.service

import com.klipy.demoapp.data.dto.CategoriesResponseDto
import com.klipy.demoapp.data.dto.MediaItemResponseDto
import com.klipy.demoapp.data.dto.request.ReportRequestDto
import com.klipy.demoapp.data.dto.request.TriggerViewRequestDto
import com.klipy.demoapp.data.infrastructure.interceptor.AdsQueryParameters
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GifService : MediaService {

    @GET("${PREFIX}/categories")
    override suspend fun getCategories(): Response<CategoriesResponseDto>

    @GET("${PREFIX}/recent/{customer_id}")
    @AdsQueryParameters
    override suspend fun getRecent(
        @Path("customer_id") customerId: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<MediaItemResponseDto>

    @GET("${PREFIX}/trending")
    @AdsQueryParameters
    override suspend fun getTrending(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<MediaItemResponseDto>

    @GET("${PREFIX}/search")
    @AdsQueryParameters
    override suspend fun search(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<MediaItemResponseDto>

    @POST("${PREFIX}/share/{slug}")
    override suspend fun triggerShare(
        @Path("slug") slug: String,
        @Body request: TriggerViewRequestDto
    ): Response<Any>

    @POST("${PREFIX}/view/{slug}")
    override suspend fun triggerView(
        @Path("slug") slug: String,
        @Body request: TriggerViewRequestDto
    ): Response<Any>

    @POST("${PREFIX}/report/{slug}")
    override suspend fun report(
        @Path("slug") slug: String,
        @Body request: ReportRequestDto
    ): Response<Any>

    @DELETE("${PREFIX}/recent/{customerId}")
    override suspend fun hideFromRecent(
        @Path("customerId") customerId: String,
        @Query("slug") slug: String
    ): Response<Any>

    private companion object {
        const val PREFIX = "gifs"
    }
}