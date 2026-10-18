package com.klipy.demoapp.domain.models

/** Picker entry, including local Recent/Trending entries; query is sent separately from its label. */
data class Category(
    val title: String,
    val url: String,
    val query: String
)

fun Category.isRecent() = query == "recent"
