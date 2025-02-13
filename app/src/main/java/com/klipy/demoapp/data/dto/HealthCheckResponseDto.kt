package com.klipy.demoapp.data.dto

import com.google.gson.annotations.SerializedName

data class HealthCheckResponseDto(
    @SerializedName("gifs")
    val gifs: HealthCheckDataDto? = null,
    @SerializedName("clips")
    val clips: HealthCheckDataDto? = null,
    @SerializedName("stickers")
    val stickers: HealthCheckDataDto? = null,
)

data class HealthCheckDataDto(
    @SerializedName("is_alive")
    val isAlive: Boolean? = null
)