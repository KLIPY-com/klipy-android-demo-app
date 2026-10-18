package com.klipy.demoapp.data.infrastructure

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.klipy.demoapp.R
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.xmlpull.v1.XmlPullParser
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Exercises real Android persistence without clearing the installed demo's identity. */
@RunWith(AndroidJUnit4::class)
class CustomerIdIntegrationTest {
    private val base = InstrumentationRegistry.getInstrumentation().targetContext
    private val testStore = "customer-id-test-${UUID.randomUUID()}"
    private val prefs = base.getSharedPreferences(testStore, Context.MODE_PRIVATE)
    private val context = object : ContextWrapper(base) {
        override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
            assertEquals("klipy_installation", name)
            return prefs
        }
    }

    @After fun cleanUp() { base.deleteSharedPreferences(testStore) }

    @Test fun identitySurvivesProviderRecreationAndResetsWithItsStorage() {
        val first = DeviceInfoProviderImpl(context).getCustomerId()
        assertEquals(4, UUID.fromString(first).version())
        assertEquals(first, prefs.getString("customer_id", null))
        assertEquals(first, DeviceInfoProviderImpl(context).getCustomerId())
        assertTrue(prefs.edit().clear().commit())
        val afterReset = DeviceInfoProviderImpl(context).getCustomerId()
        assertEquals(4, UUID.fromString(afterReset).version())
        assertNotEquals(first, afterReset)
    }

    @Test fun existingInstallationIdentityIsPreserved() {
        val saved = UUID.randomUUID().toString()
        assertTrue(prefs.edit().putString("customer_id", saved).commit())
        assertEquals(saved, DeviceInfoProviderImpl(context).getCustomerId())
    }

    @Test fun failedPersistenceDoesNotReturnATemporaryIdentity() {
        val failingContext = object : ContextWrapper(base) {
            override fun getSharedPreferences(name: String, mode: Int): SharedPreferences =
                object : SharedPreferences by prefs {
                    override fun edit(): SharedPreferences.Editor =
                        object : SharedPreferences.Editor by prefs.edit() {
                            override fun putString(key: String?, value: String?) = this
                            override fun remove(key: String?) = this
                            override fun commit() = false
                        }
                }
        }
        assertThrows(IllegalStateException::class.java) {
            DeviceInfoProviderImpl(failingContext).getCustomerId()
        }
        assertFalse(prefs.contains("customer_id"))
    }

    @Test fun concurrentFirstRequestsShareOnePersistedIdentity() {
        val executor = Executors.newFixedThreadPool(8)
        try {
            val ids = executor.invokeAll((1..32).map {
                Callable { DeviceInfoProviderImpl(context).getCustomerId() }
            }, 5, TimeUnit.SECONDS).map { it.get() }.toSet()
            assertEquals(1, ids.size)
            assertEquals(ids.single(), prefs.getString("customer_id", null))
        } finally {
            executor.shutdownNow()
        }
    }

    @Test fun installationIdentityIsExcludedFromBackupAndTransfer() {
        listOf(
            R.xml.backup_rules to setOf("full-backup-content"),
            R.xml.data_extraction_rules to setOf("cloud-backup", "device-transfer")
        ).forEach { (resource, expected) ->
            val excluded = mutableSetOf<String>()
            base.resources.getXml(resource).use { xml ->
                var parent = ""
                while (xml.eventType != XmlPullParser.END_DOCUMENT) {
                    if (xml.eventType == XmlPullParser.START_TAG) {
                        if (xml.name in expected) parent = xml.name
                        if (xml.name == "exclude" &&
                            xml.getAttributeValue(null, "domain") == "sharedpref" &&
                            xml.getAttributeValue(null, "path") == "klipy_installation.xml"
                        ) excluded.add(parent)
                    } else if (xml.eventType == XmlPullParser.END_TAG && xml.name == parent) {
                        parent = ""
                    }
                    xml.next()
                }
            }
            assertEquals(expected, excluded)
        }
    }
}
