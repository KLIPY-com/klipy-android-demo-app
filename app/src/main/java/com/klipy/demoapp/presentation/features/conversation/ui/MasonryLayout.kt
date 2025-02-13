@file:OptIn(ExperimentalFoundationApi::class)

package com.klipy.demoapp.presentation.features.conversation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.presentation.algorithm.MasonryMeasurementsCalculator
import com.klipy.demoapp.presentation.algorithm.MediaItemRow
import com.klipy.demoapp.presentation.uicomponents.EndlessLazyColumn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MasonryLayout(
    modifier: Modifier,
    items: List<MediaItem>,
    listState: LazyListState = rememberLazyListState(),
    isLoading: Boolean,
    gap: Dp,
    loadMore: () -> Unit,
    onMediaItemClicked: (mediaItem: MediaItem) -> Unit,
    onMediaItemLongClicked: (mediaItem: MediaItem) -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val width = with(density) { constraints.maxWidth.toDp() }
        val rows = remember { mutableStateOf<List<MediaItemRow>>(emptyList()) }

        LaunchedEffect(items) {
            // If items changed need to calculate positions and width/height of elements
            val newRows = withContext(Dispatchers.Default) {
                MasonryMeasurementsCalculator.createRows(
                    items,
                    width.value.toInt(),
                    gap.value.toInt()
                )
            }
            rows.value = newRows
        }
        if (width != 0.dp) {
            EndlessLazyColumn(
                modifier = Modifier.fillMaxSize(),
                listState = listState,
                items = rows.value,
                isLoading = isLoading,
                gap = gap,
                itemKey = { it.firstOrNull()?.mediaItem?.id ?: "" },
                loadMore = loadMore,
                itemContent = {
                    MediaContent(
                        data = it,
                        gap = gap,
                        onMediaItemClicked = onMediaItemClicked,
                        onMediaItemLongClicked = onMediaItemLongClicked
                    )
                }
            )
        }
    }
}