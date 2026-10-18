package com.klipy.demoapp.data.mapper

import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.klipy.demoapp.data.dto.CategoriesResponseDto
import com.klipy.demoapp.domain.models.Category

/**
 * Maps the two verified category response formats to picker entries.
 * Production fields retain their separate label, query, and preview meanings.
 *
 * @return Local Recent/Trending entries followed by server categories, or empty for absent data.
 * @throws JsonParseException for a malformed category; no query or preview is guessed.
 */
internal fun CategoriesResponseDto.toCategories(): List<Category> {
    val payload = data?.takeUnless { it.isJsonNull } ?: return emptyList()
    val categories = when {
        payload.isJsonArray -> payload.asJsonArray.map { legacyCategory(it.categoryString()) }
        payload.isJsonObject -> {
            val records = payload.asJsonObject.get("categories")
            if (records == null || !records.isJsonArray) throw JsonParseException("Expected data.categories array")
            records.asJsonArray.map { record ->
                if (!record.isJsonObject) throw JsonParseException("Expected category object")
                val fields = record.asJsonObject
                Category(
                    title = fields.get("category").categoryString(),
                    query = fields.get("query").categoryString(),
                    url = fields.get("preview_url").categoryString()
                )
            }
        }
        else -> throw JsonParseException("Unsupported categories response")
    }
    return listOf(legacyCategory("recent"), legacyCategory("trending")) + categories
}

private fun legacyCategory(value: String) = Category(
    title = value, query = value, url = "https://api.klipy.com/assets/images/category/$value.png"
)

private fun JsonElement?.categoryString(): String {
    if (this == null || !isJsonPrimitive || !asJsonPrimitive.isString || asString.isBlank()) {
        throw JsonParseException("Expected nonempty category string")
    }
    return asString
}
