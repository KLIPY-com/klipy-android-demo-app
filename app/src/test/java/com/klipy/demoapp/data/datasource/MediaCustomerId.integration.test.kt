package com.klipy.demoapp.data.datasource

import com.google.gson.Gson
import com.klipy.demoapp.data.infrastructure.ApiCallHelper
import com.klipy.demoapp.data.infrastructure.DeviceInfoProvider
import com.klipy.demoapp.data.infrastructure.Measurements
import com.klipy.demoapp.data.infrastructure.ScreenMeasurementsProvider
import com.klipy.demoapp.data.infrastructure.interceptor.AdsQueryParametersInterceptor
import com.klipy.demoapp.data.mapper.MediaItemMapperImpl
import com.klipy.demoapp.data.service.ClipsService
import com.klipy.demoapp.data.service.GifService
import com.klipy.demoapp.data.service.StickersService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Records real Retrofit requests for all media operations; no requests reach a server. */
class MediaCustomerIdIntegrationTest {
    @Test fun allMediaTypesUseOneCustomerIdAcrossFeedsRecentsAndActions() = runBlocking {
        val device = object : DeviceInfoProvider {
            override fun getCustomerId() = CUSTOMER_ID
            override fun getUserAgent(): String? = null
            override fun getCarrier(): String? = null
            override fun getNetworkOperator(): String? = null
        }
        val measurements = object : ScreenMeasurementsProvider {
            override var device = Measurements(1080, 1920)
            override var mediaSelectorContainer = Measurements(320, 480)
            override fun getDensityScaleFactor() = 3f
        }
        val requests = mutableListOf<Request>()
        val client = OkHttpClient.Builder()
            .addInterceptor(AdsQueryParametersInterceptor(device, measurements))
            .addInterceptor { chain ->
                val request = chain.request()
                requests.add(request)
                Response.Builder().request(request).protocol(Protocol.HTTP_1_1)
                    .code(200).message("OK").body(
                        (if (request.url.encodedPath.endsWith("/categories")) CATEGORIES else "{}")
                            .toResponseBody()
                    ).build()
            }.build()
        val retrofit = Retrofit.Builder().baseUrl("https://api.example.invalid/")
            .client(client).addConverterFactory(GsonConverterFactory.create()).build()
        listOf(
            "gifs" to retrofit.create(GifService::class.java),
            "clips" to retrofit.create(ClipsService::class.java),
            "stickers" to retrofit.create(StickersService::class.java)
        ).forEach { (type, service) ->
            requests.clear()
            val source = MediaDataSourceImpl(ApiCallHelper(), service, MediaItemMapperImpl(), device)
            val categories = source.getCategories().getOrThrow()
            assertEquals(listOf("recent", "trending", "Greetings"), categories.map { it.title })
            assertEquals("hello", categories.last().query)
            assertEquals("https://static.example.invalid/hello.gif", categories.last().url)
            assertEquals(categories, source.getCategories().getOrThrow())
            assertEquals(1, requests.size) // Second read uses the cached categories.
            requests.clear()
            assertTrue(source.getMediaData("trending", null).isSuccess)
            assertTrue(source.getMediaData("hello", null).isSuccess)
            assertTrue(source.getMediaData("recent", null).isSuccess)
            assertTrue(source.triggerShare("fixture").isSuccess)
            assertTrue(source.triggerView("fixture").isSuccess)
            assertTrue(source.report("fixture", "fixture reason").isSuccess)
            assertTrue(source.hideFromRecent("fixture").isSuccess)
            assertEquals(7, requests.size)
            requests.take(3).forEach {
                assertEquals(listOf(CUSTOMER_ID), it.url.queryParameterValues("customer_id"))
            }
            assertEquals("/$type/recent/$CUSTOMER_ID", requests[2].url.encodedPath)
            requests.subList(3, 6).forEach {
                val body = Buffer().also { buffer -> it.body!!.writeTo(buffer) }.readUtf8()
                assertEquals(CUSTOMER_ID, Gson().fromJson(body, Map::class.java)["customer_id"])
            }
            assertEquals("DELETE", requests.last().method)
            assertEquals("/$type/recent/$CUSTOMER_ID", requests.last().url.encodedPath)
        }
    }

    private companion object {
        const val CATEGORIES = """{"result":true,"data":{"locale":"en_US","categories":[
            {"category":"Greetings","query":"hello","preview_url":"https://static.example.invalid/hello.gif"}
        ]}}"""
        const val CUSTOMER_ID = "b990af14-01ef-4d5c-a4ce-d655f04fd5c2"
    }
}
