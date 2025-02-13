package com.klipy.demoapp.data

import android.graphics.BitmapFactory
import android.util.Base64
import coil3.Bitmap

/**
 * Converts BASE64 String to Bitmap
 */
fun String.base64toBitmap(): Bitmap? {
    val decodedString: ByteArray =
        Base64.decode(this.substring(this.indexOf(",") + 1), Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}