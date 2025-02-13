package com.klipy.demoapp.presentation.features.conversation.model

enum class MediaType(val title: String) {
    GIF("GIFs"), CLIP("Clips"), STICKER("Stickers"), AD("Ads")
}

fun MediaType.getSingularName(): String {
    return when (this) {
        MediaType.GIF -> "GIF"
        MediaType.STICKER -> "Sticker"
        MediaType.CLIP -> "Clip"
        MediaType.AD -> "AD"
    }
}