package com.klipy.demoapp.data.infrastructure

import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import android.webkit.WebSettings

interface DeviceInfoProvider {

    fun getDeviceId(): String

    fun getUserAgent(): String?

    fun getCarrier(): String?

    fun getNetworkOperator(): String?
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

    override fun getCarrier(): String? {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.networkOperatorName
    }

    override fun getNetworkOperator(): String? {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.networkOperator // Returns MCC+MNC
    }

}