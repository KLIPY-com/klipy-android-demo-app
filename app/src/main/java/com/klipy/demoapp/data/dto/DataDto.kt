package com.klipy.demoapp.data.dto

import com.google.gson.annotations.SerializedName

data class DataDto(
    @SerializedName("data")
    val data: List<MediaItemDto>? = null,
    @SerializedName("has_next")
    val hasNext: Boolean? = null,
    @SerializedName("meta")
    val meta: MetaDto? = null
)

data class MetaDto(
    @SerializedName("item_min_width")
    val itemMinWidth: Int? = null,
    @SerializedName("ad_max_resize_percent")
    val adMaxResizePercentage: Int? = null
)