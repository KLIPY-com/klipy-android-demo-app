package com.klipy.demoapp.data.dto

import com.google.gson.annotations.SerializedName

data class CategoriesResponseDto(
    @SerializedName("result")
    val result: Boolean? = null,
    @SerializedName("data")
    val data: List<String>? = null
)