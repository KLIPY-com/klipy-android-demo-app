package com.klipy.demoapp.data.infrastructure.interceptor

import com.klipy.demoapp.data.infrastructure.DeviceInfoProvider
import com.klipy.demoapp.data.infrastructure.Measurements
import com.klipy.demoapp.data.infrastructure.ScreenMeasurementsProvider
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Invocation

class AdsQueryParametersInterceptorTest {
    private interface Requests {
        @AdsQueryParameters fun media()
        fun categories()
    }

    @Test fun mediaRequestsKeepRenderingParametersWithoutRetiredAdvertisingFields() {
        val request = intercepted("media", "https://api.example.invalid/gifs/search?q=hello&page=2")
        assertEquals("hello", request.url.queryParameter("q"))
        assertEquals("2", request.url.queryParameter("page"))
        assertEquals("1", request.url.queryParameter("ad-iframe"))
        assertEquals("320", request.url.queryParameter("ad-max-width"))
        assertEquals("test-user-agent", request.header("User-Agent"))
        assertEquals(listOf(CUSTOMER_ID), request.url.queryParameterValues("customer_id"))
        RETIRED_FIELDS.forEach { assertNull(it, request.url.queryParameter(it)) }
    }

    @Test fun retiredFieldsAlreadyOnAMediaUrlAreRemoved() {
        val url = "https://api.example.invalid/clips/trending".toHttpUrl().newBuilder()
        RETIRED_FIELDS.forEach { url.addQueryParameter(it, "must-not-leave-device") }
        url.addQueryParameter("customer_id", "old-device-id")
        url.addQueryParameter("customer_id", "another-id")
        val request = intercepted("media", url.build().toString())
        RETIRED_FIELDS.forEach { assertNull(it, request.url.queryParameter(it)) }
        assertEquals("1", request.url.queryParameter("ad-iframe"))
        assertEquals(listOf(CUSTOMER_ID), request.url.queryParameterValues("customer_id"))
    }

    @Test fun requestsWithoutTheAnnotationAreUnchanged() {
        val url = "https://api.example.invalid/stickers/categories?locale=en"
        val request = intercepted("categories", url)
        assertEquals(url, request.url.toString())
        assertNull(request.header("User-Agent"))
    }

    private fun intercepted(method: String, url: String): Request {
        val device = object : DeviceInfoProvider {
            override fun getCustomerId() = CUSTOMER_ID
            override fun getUserAgent() = "test-user-agent"
            override fun getCarrier(): String? = null
            override fun getNetworkOperator(): String? = null
        }
        val measurements = object : ScreenMeasurementsProvider {
            override var device = Measurements(1080, 1920)
            override var mediaSelectorContainer = Measurements(320, 480)
            override fun getDensityScaleFactor() = 3f
        }
        var recorded: Request? = null
        val client = OkHttpClient.Builder()
            .addInterceptor(AdsQueryParametersInterceptor(device, measurements))
            .addInterceptor { chain ->
                recorded = chain.request()
                Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1)
                    .code(200).message("OK").body("{}".toResponseBody()).build()
            }.build()
        val request = Request.Builder().url(url)
            .tag(Invocation::class.java, Invocation.of(Requests::class.java.getMethod(method), emptyList<Any>()))
            .build()
        client.newCall(request).execute().close()
        return checkNotNull(recorded)
    }

    private companion object {
        const val CUSTOMER_ID = "b990af14-01ef-4d5c-a4ce-d655f04fd5c2"
        val RETIRED_FIELDS = listOf(
            "ad-ifa", "ad-didsha1", "ad-didmd5", "ad-dpidsha1", "ad-dpidmd5",
            "ad-macsha1", "ad-macmd5", "ad-yob", "ad-gender", "ad-app-version", "ad-position"
        )
    }
}
