package com.klipy.demoapp.data.dto

import com.google.gson.annotations.SerializedName

data class FileMetaDataDto(
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("width")
    val width: Int? = null,
    @SerializedName("height")
    val height: Int? = null,
    @SerializedName("size")
    val size: Long? = null,
)