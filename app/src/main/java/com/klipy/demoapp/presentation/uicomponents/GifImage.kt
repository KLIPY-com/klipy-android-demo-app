package com.klipy.demoapp.presentation.uicomponents

import android.os.Build.VERSION.SDK_INT
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * Composable to display gifs using Coil library
 */
@Composable
fun GifImage(
    modifier: Modifier = Modifier,
    key: Any,
    url: String?,
    contentScale: ContentScale,
    placeholder: Painter? = null,
    error: Painter? = null
) {
    val context = LocalContext.current
    val imageRequest = remember(key) {
        ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build()
    }
    AsyncImage(
        modifier = modifier,
        model = imageRequest,
        contentDescription = "Media",
        imageLoader = remember {
            ImageLoader.Builder(context)
                .components {
                    if (SDK_INT >= 28) {
                        add(AnimatedImageDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
        },
        contentScale = contentScale,
        placeholder = placeholder,
        error = error
    )
}

/**
 * Same composable but takes URLs as placeholder and error
 */
@Composable
fun GifImage(
    modifier: Modifier = Modifier,
    key: Any,
    url: String?,
    contentScale: ContentScale,
    placeholderUrl: String?,
    errorUrl: String?
) {
    val context = LocalContext.current

    // Function to create an ImageRequest
    fun createImageRequest(imageUrl: String?): ImageRequest? {
        if (imageUrl == null) return null
        return ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build()
    }

    // Create an ImageLoader that supports both GIF and static images
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
    // Main image request
    val imageRequest = remember(key) { createImageRequest(url) }
    // Placeholder and error images as Painters
    val placeholderPainter = placeholderUrl?.let {
        rememberAsyncImagePainter(model = createImageRequest(it), imageLoader = imageLoader)
    }
    val errorPainter = errorUrl?.let {
        rememberAsyncImagePainter(model = createImageRequest(it), imageLoader = imageLoader)
    }
    AsyncImage(
        modifier = modifier,
        model = imageRequest,
        contentDescription = "Media",
        imageLoader = imageLoader,
        contentScale = contentScale,
        placeholder = placeholderPainter,
        error = errorPainter
    )
}
