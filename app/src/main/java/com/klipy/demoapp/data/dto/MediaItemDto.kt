package com.klipy.demoapp.data.dto

import com.google.gson.annotations.SerializedName

sealed interface MediaItemDto {
    data class GeneralMediaItemDto(
        @SerializedName("slug")
        val slug: String? = null,
        @SerializedName("title")
        val title: String? = null,
        @SerializedName("blur_preview")
        val placeHolder: String? = null,
        @SerializedName("file")
        val file: DimensionsDto? = null,
        @SerializedName("type")
        val type: String? = null
    ) : MediaItemDto

    data class ClipMediaItemDto(
        @SerializedName("slug")
        val slug: String? = null,
        @SerializedName("title")
        val title: String? = null,
        @SerializedName("blur_preview")
        val placeHolder: String? = null,
        @SerializedName("file_meta")
        val fileMeta: FileTypesDto? = null,
        @SerializedName("file")
        val file: ClipFileDto? = null,
        @SerializedName("type")
        val type: String? = null
    ) : MediaItemDto

    data class AdMediaItemDto(
        @SerializedName("width")
        val width: Int? = null,
        @SerializedName("height")
        val height: Int? = null,
        @SerializedName("content")
        val content: String? = null,
        @SerializedName("type")
        val type: String? = null
    ): MediaItemDto
}

data class ClipFileDto(
    @SerializedName("gif")
    val gif: String? = null,
    @SerializedName("mp4")
    val mp4: String? = null,
    @SerializedName("webp")
    val webp: String? = null
)