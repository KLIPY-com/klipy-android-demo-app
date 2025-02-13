package com.klipy.demoapp.data.dto.request

import com.google.gson.annotations.SerializedName

data class TriggerViewRequestDto(
    @SerializedName("customer_id")
    val customerId: String
)