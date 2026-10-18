package com.klipy.demoapp.data.infrastructure

import android.content.Context
import android.telephony.TelephonyManager
import android.webkit.WebSettings
import java.util.UUID

interface DeviceInfoProvider {

    /** Returns the persisted random customer ID for this app installation, never a device ID. */
    fun getCustomerId(): String

    fun getUserAgent(): String?

    fun getCarrier(): String?

    fun getNetworkOperator(): String?
}

class DeviceInfoProviderImpl(
    private val context: Context
): DeviceInfoProvider {

    /**
     * Creates the ID once and shares it across feeds, Recents, and media actions.
     * The dedicated preferences file is excluded from backup and device transfer.
     *
     * @return A UUID that remains stable until the installation's data is removed.
     * @throws IllegalStateException if the first ID cannot be persisted.
     */
    override fun getCustomerId(): String = synchronized(customerIdLock) {
        val preferences = context.getSharedPreferences("klipy_installation", Context.MODE_PRIVATE)
        preferences.getString("customer_id", null)?.let { return@synchronized it }
        val id = UUID.randomUUID().toString()
        if (!preferences.edit().putString("customer_id", id).commit()) {
            // commit() may update memory even when disk persistence fails. Do not reuse that ID.
            preferences.edit().remove("customer_id").commit()
            throw IllegalStateException("Could not persist installation customer ID")
        }
        id
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

    private companion object {
        // The demo runs in one process. Separate provider instances must not mint competing IDs.
        val customerIdLock = Any()
    }
}
