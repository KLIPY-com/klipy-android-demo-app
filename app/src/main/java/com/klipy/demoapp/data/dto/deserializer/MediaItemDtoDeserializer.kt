package com.klipy.demoapp.data.dto.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.klipy.demoapp.data.dto.MediaItemDto
import java.lang.reflect.Type

class MediaItemDtoDeserializer : JsonDeserializer<MediaItemDto> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): MediaItemDto {
        val jsonObject = json.asJsonObject

        return if (jsonObject.has("type") && jsonObject.get("type").asString == "clip") {
            context.deserialize(jsonObject, MediaItemDto.ClipMediaItemDto::class.java)
        } else if (jsonObject.has("type") && jsonObject.get("type").asString == "ad") {
            context.deserialize(jsonObject, MediaItemDto.AdMediaItemDto::class.java)
        } else {
            context.deserialize(jsonObject, MediaItemDto.GeneralMediaItemDto::class.java)
        }
    }
}