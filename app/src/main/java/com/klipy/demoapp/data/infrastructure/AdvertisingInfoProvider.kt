package com.klipy.demoapp.data.infrastructure

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient

interface AdvertisingInfoProvider {

    fun getAdvertisingId(): String?
}

class AdvertisingInfoProviderImpl(private val context: Context): AdvertisingInfoProvider {

    /**
     * Returns advertising id of the device
     */
    override fun getAdvertisingId(): String? {
        return try {
            val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
            adInfo.id
        } catch (e: Exception) {
            null
        }
    }
}