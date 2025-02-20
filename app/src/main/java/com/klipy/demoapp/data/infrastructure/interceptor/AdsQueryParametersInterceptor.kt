package com.klipy.demoapp.data.infrastructure.interceptor

import android.os.Build
import com.klipy.demoapp.data.infrastructure.AdvertisingInfoProvider
import com.klipy.demoapp.data.infrastructure.DeviceInfoProvider
import com.klipy.demoapp.data.infrastructure.ScreenMeasurementsProvider
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import java.util.Locale

class AdsQueryParametersInterceptor(
    private val deviceInfoProvider: DeviceInfoProvider,
    private val screenMeasurementsProvider: ScreenMeasurementsProvider,
    private val advertisingInfoProvider: AdvertisingInfoProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val invocation = originalRequest.tag(Invocation::class.java)
        // Check whether api call has @AdsQueryParameters annotation
        val hasAnnotation =
            invocation?.method()?.getAnnotation(AdsQueryParameters::class.java) != null

        return if (hasAnnotation) {
            val originalHttpUrl = originalRequest.url

            val newHttpUrl = originalHttpUrl.newBuilder()
                //  User's unique identifier in the app (e.g., GUID).
                //  It is used for analytics and displaying relevant trending and search results.
                //  For the sake of simplicity, we are using device id
                .addQueryParameter(CUSTOMER_ID, deviceInfoProvider.getDeviceId())
                .addQueryParameter(LOCALE, Locale.getDefault().language)
                .addQueryParameter(AD_MIN_WIDTH, "50")
                .addQueryParameter(
                    AD_MAX_WIDTH,
                    screenMeasurementsProvider.mediaSelectorContainer.width.toString()
                )
                .addQueryParameter(AD_MIN_HEIGHT, "50")
                .addQueryParameter(AD_MAX_HEIGHT, "200")
                .apply {
                    val ifa = advertisingInfoProvider.getAdvertisingId()
                    if (ifa != null) {
                        addQueryParameter(IFA, ifa)
                    }
                }
                .addQueryParameter(APP_VERSION, "1.0")
                .addQueryParameter(OS, "Android")
                .addQueryParameter(OS_VERSION, Build.VERSION.RELEASE)
                .addQueryParameter(MANUFACTURER, Build.MANUFACTURER)
                .addQueryParameter(MODEL, Build.MODEL)
                .addQueryParameter(
                    AD_DEVICE_WIDTH,
                    screenMeasurementsProvider.device.width.toString()
                )
                .addQueryParameter(
                    AD_DEVICE_HEIGHT,
                    screenMeasurementsProvider.device.height.toString()
                )
                .addQueryParameter(
                    AD_PXRATIO,
                    screenMeasurementsProvider.getDensityScaleFactor().toString()
                )
                .addQueryParameter(AD_LANGUAGE, Locale.getDefault().language)
                .apply {
                    val carrier = deviceInfoProvider.getCarrier()
                    if (carrier != null) {
                        addQueryParameter(AD_CARRIER, carrier)
                    }
                }
                .apply {
                    val networkOperator = deviceInfoProvider.getNetworkOperator()
                    if (networkOperator != null) {
                        addQueryParameter(AD_MCCMNC, networkOperator)
                    }
                }
                // Year of birth of user, hardcoded for sample
                .addQueryParameter(AD_YOB, "1980")
                // Gender of user, hardcoded for sample
                .addQueryParameter(AD_GENDER, "M")
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newHttpUrl)
                .apply {
                    val userAgent = deviceInfoProvider.getUserAgent()
                    if (userAgent != null) {
                        this.header(USER_AGENT, userAgent)
                    }
                }
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }

    private companion object {
        const val CUSTOMER_ID = "customer_id"
        const val AD_MIN_WIDTH = "ad-min-width"
        const val AD_MAX_WIDTH = "ad-max-width"
        const val AD_MIN_HEIGHT = "ad-min-height"
        const val AD_MAX_HEIGHT = "ad-max-height"
        const val USER_AGENT = "User-Agent"
        const val APP_VERSION = "ad-app-version"
        const val OS = "ad-os"
        const val OS_VERSION = "ad-osv"
        const val MANUFACTURER = "ad-make"
        const val MODEL = "ad-model"
        const val IFA = "ad-ifa"
        const val LOCALE = "locale"
        const val AD_DEVICE_WIDTH = "ad-device-w"
        const val AD_DEVICE_HEIGHT = "ad-device-h"
        const val AD_YOB = "ad-yob"
        const val AD_GENDER = "ad-gender"
        const val AD_PXRATIO = "ad-pxratio"
        const val AD_CARRIER = "ad-carrier"
        const val AD_MCCMNC = "ad-mccmnc"
        const val AD_LANGUAGE = "ad-language"
    }
}