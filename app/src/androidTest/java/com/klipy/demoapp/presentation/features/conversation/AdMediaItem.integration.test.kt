package com.klipy.demoapp.presentation.features.conversation

import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Rect
import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.klipy.demoapp.presentation.features.conversation.ui.AdMediaItem
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** Exercises the demo's actual Compose-to-SDK boundary with an offline creative. */
@RunWith(AndroidJUnit4::class)
class AdMediaItemIntegrationTest {
    @Test fun adRendersOpensOnTapAndCanBeRemovedAndReopened() {
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        val shown = mutableStateOf(true)
        val attempts = CopyOnWriteArrayList<Intent>()
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent {
                    val base = LocalContext.current
                    val context = remember(base) {
                        object : ContextWrapper(base) {
                            override fun startActivity(intent: Intent) { attempts.add(Intent(intent)) }
                        }
                    }
                    CompositionLocalProvider(LocalContext provides context) {
                        if (shown.value) AdMediaItem(Modifier.size(300.dp, 360.dp), null, 300, 360)
                    }
                }
            }
            listOf("_self", "_blank").forEachIndexed { index, target ->
                var webView: WebView? = null
                await { scenario.onActivity { webView = findWebView(it.window.decorView) }; webView != null }
                scenario.onActivity {
                    webView!!.loadDataWithBaseURL(null, """
                        <html><head><meta name="viewport" content="width=device-width,initial-scale=1"></head>
                        <body style="margin:0"><a href="https://advertiser.invalid/offer" target="$target"
                        style="display:block;width:300px;height:360px">Advertisement</a></body></html>
                    """.trimIndent(), "text/html", "UTF-8", null)
                }
                await {
                    val done = CountDownLatch(1)
                    var ready = false
                    scenario.onActivity {
                        webView!!.evaluateJavascript("document.readyState") { value ->
                            ready = value == "\"complete\""
                            done.countDown()
                        }
                    }
                    done.await(2, TimeUnit.SECONDS) && ready
                }
                assertEquals(index, attempts.size)
                // The offline link fills the WebView found in the native UI tree.
                val bounds = Rect()
                scenario.onActivity {
                    assertTrue(webView!!.getGlobalVisibleRect(bounds))
                    assertTrue(webView!!.hasWindowFocus())
                }
                val down = SystemClock.uptimeMillis()
                listOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP).forEach { action ->
                    val event = MotionEvent.obtain(down, SystemClock.uptimeMillis(), action,
                        bounds.exactCenterX(), bounds.exactCenterY(), 0)
                    event.source = InputDevice.SOURCE_TOUCHSCREEN
                    assertTrue(automation.injectInputEvent(event, true))
                    event.recycle()
                }
                await { attempts.size == index + 1 }
                assertEquals("https://advertiser.invalid/offer", attempts.last().dataString)
                scenario.onActivity { shown.value = false }
                await {
                    var removed = false
                    scenario.onActivity { removed = findWebView(it.window.decorView) == null }
                    removed
                }
                scenario.onActivity { shown.value = true }
            }
        }
    }

    private fun findWebView(view: View): WebView? {
        if (view is WebView) return view
        if (view is ViewGroup) for (index in 0 until view.childCount) {
            findWebView(view.getChildAt(index))?.let { return it }
        }
        return null
    }

    private fun await(condition: () -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 5_000
        while (!condition()) {
            check(SystemClock.uptimeMillis() < deadline) { "Timed out waiting for the ad surface" }
            SystemClock.sleep(50)
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }
}
