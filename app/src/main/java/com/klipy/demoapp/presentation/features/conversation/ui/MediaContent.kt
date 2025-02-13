@file:OptIn(ExperimentalFoundationApi::class)

package com.klipy.demoapp.presentation.features.conversation.ui

import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import co.kikliko.android.ads_sdk.GIFWebView
import co.kikliko.android.ads_sdk.KlipyContent
import coil3.compose.rememberAsyncImagePainter
import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.domain.models.isAD
import com.klipy.demoapp.presentation.algorithm.MediaItemRow
import com.klipy.demoapp.presentation.uicomponents.GifImage

@Composable
fun MediaContent(
    data: MediaItemRow,
    gap: Dp,
    onMediaItemClicked: (mediaItem: MediaItem) -> Unit,
    onMediaItemLongClicked: (mediaItem: MediaItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                data.first().measuredHeight.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(gap)
    ) {
        data.forEachIndexed { index, mediaItem ->
            val itemWidth = mediaItem.measuredWidth.dp
            if (mediaItem.mediaItem.isAD()) {
                AdMediaItem(
                    modifier = Modifier
                        .width(itemWidth)
                        .fillMaxHeight(),
                    content = mediaItem.mediaItem.lowQualityMetaData?.url,
                    width = mediaItem.measuredWidth,
                    height = mediaItem.measuredHeight
                )
            } else {
                GifImage(
                    modifier = Modifier
                        .width(itemWidth)
                        .fillMaxHeight()
                        .combinedClickable(
                            onClick = {
                                onMediaItemClicked(mediaItem.mediaItem)
                            },
                            onLongClick = {
                                onMediaItemLongClicked(mediaItem.mediaItem)
                            }
                        ),
                    key = mediaItem,
                    url = mediaItem.mediaItem.lowQualityMetaData?.url,
                    contentScale = ContentScale.Crop,
                    placeholder = rememberAsyncImagePainter(mediaItem.mediaItem.placeHolder),
                    error = rememberAsyncImagePainter(mediaItem.mediaItem.placeHolder)
                )
            }
        }
    }
}

@Composable
fun AdMediaItem(
    modifier: Modifier = Modifier,
    content: String?,
    width: Int,
    height: Int
) {
    val density = LocalDensity.current
    val widthPx = with(density) { width.dp.toPx().toInt() }
    val heightPx = with(density) { height.dp.toPx().toInt() }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            GIFWebView(ctx).apply {
                setBackgroundColor(Color.TRANSPARENT)
                layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
                val klipyContent = KlipyContent(
                    isWebView = true,
                    content = content ?: "",
                    width = width,
                    height = height
                )
                this.loadContent(klipyContent)
            }
        },
        update = {
            val newLayoutParams = it.layoutParams
            newLayoutParams.width = widthPx
            newLayoutParams.height = heightPx
            it.layoutParams = newLayoutParams
        },
        onRelease = {
            it.removeAllViews()
            it.destroy()
        }
    )
}