package com.klipy.demoapp.data.infrastructure

import android.content.Context
import android.provider.Settings
import android.webkit.WebSettings

interface DeviceInfoProvider {

    fun getDeviceId(): String

    fun getUserAgent(): String?
}

class DeviceInfoProviderImpl(
    private val context: Context
): DeviceInfoProvider {

    override fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun getUserAgent(): String? {
        return WebSettings.getDefaultUserAgent(context)
    }
}