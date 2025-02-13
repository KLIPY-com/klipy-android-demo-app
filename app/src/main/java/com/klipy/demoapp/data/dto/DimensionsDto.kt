package com.klipy.demoapp.data.dto

import com.google.gson.annotations.SerializedName

data class DimensionsDto(
    @SerializedName("hd")
    val hd: FileTypesDto? = null,
    @SerializedName("md")
    val md: FileTypesDto? = null,
    @SerializedName("sm")
    val sm: FileTypesDto? = null,
    @SerializedName("xs")
    val xs: FileTypesDto? = null
)