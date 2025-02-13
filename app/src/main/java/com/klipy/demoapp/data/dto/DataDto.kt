package com.klipy.demoapp.data.dto

import com.google.gson.annotations.SerializedName

data class DataDto(
    @SerializedName("data")
    val data: List<MediaItemDto>? = null,
    @SerializedName("has_next")
    val hasNext: Boolean? = null
)