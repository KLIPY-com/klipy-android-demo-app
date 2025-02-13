package com.klipy.demoapp.presentation.features.conversation.model

import com.klipy.demoapp.domain.models.MediaItem

data class MediaItemUIModel(
    val mediaItem: MediaItem,
    val measuredWidth: Int,
    val measuredHeight: Int
)