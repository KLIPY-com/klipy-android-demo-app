package com.klipy.demoapp.data.dto

import com.google.gson.annotations.SerializedName

data class FileTypesDto(
    @SerializedName("gif")
    val gif: FileMetaDataDto? = null,
    @SerializedName("webp")
    val webp: FileMetaDataDto? = null,
    @SerializedName("mp4")
    val mp4: FileMetaDataDto? = null,
)