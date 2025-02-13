package com.klipy.demoapp.presentation.features.conversation.model

import java.util.UUID

sealed class MessageUiModel(
    open val id: String,
    open val isFromCurrentUser: Boolean
)

data class TextMessage(
    override val id: String = UUID.randomUUID().toString(),
    override val isFromCurrentUser: Boolean,
    val text: String
) : MessageUiModel(id, isFromCurrentUser)

data class GifMessage(
    override val id: String = UUID.randomUUID().toString(),
    override val isFromCurrentUser: Boolean,
    val url: String,
    val width: Int,
    val height: Int
) : MessageUiModel(id, isFromCurrentUser)

data class ClipMessage(
    override val id: String = UUID.randomUUID().toString(),
    override val isFromCurrentUser: Boolean,
    val url: String,
    val width: Int,
    val height: Int
) : MessageUiModel(id, isFromCurrentUser)