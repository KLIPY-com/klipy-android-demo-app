package com.klipy.demoapp.domain.models

data class MediaData(
    val mediaItems: List<MediaItem>,
    val itemMinWidth: Int,
    val adMaxResizePercentage: Float
) {
    companion object {
        val EMPTY = MediaData(emptyList(), 0, 0F)
    }
}