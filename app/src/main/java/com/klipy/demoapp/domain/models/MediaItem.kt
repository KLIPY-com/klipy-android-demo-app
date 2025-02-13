package com.klipy.demoapp.domain.models

import coil3.Bitmap
import com.klipy.demoapp.presentation.features.conversation.model.MediaType

data class MediaItem(
    val id: String,
    val placeHolder: Bitmap?,
    val lowQualityMetaData: MetaData?,
    val highQualityMetaData: MetaData?,
    val mediaType: MediaType
)

data class MetaData(
    val url: String,
    val width: Int,
    val height: Int
)

fun MediaItem.isAD() = this.mediaType == MediaType.AD