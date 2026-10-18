package com.klipy.demoapp.data.dto

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

data class CategoriesResponseDto(
    @SerializedName("result")
    val result: Boolean? = null,
    @SerializedName("data")
    // Verified API variants: cached demo string array, or production locale/categories object.
    val data: JsonElement? = null
)
