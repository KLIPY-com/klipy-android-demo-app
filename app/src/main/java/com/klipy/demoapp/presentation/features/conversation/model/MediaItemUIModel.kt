package com.klipy.demoapp.presentation.features.conversation.model

import androidx.compose.runtime.Stable
import com.klipy.demoapp.domain.models.MediaItem

@Stable
data class MediaItemUIModel(
    val mediaItem: MediaItem,
    var measuredWidth: Int,
    var measuredHeight: Int
)