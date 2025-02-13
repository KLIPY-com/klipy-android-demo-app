package com.klipy.demoapp.presentation.features.conversation

import com.klipy.demoapp.domain.models.Category
import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.presentation.features.conversation.model.ClipMessage
import com.klipy.demoapp.presentation.features.conversation.model.GifMessage
import com.klipy.demoapp.presentation.features.conversation.model.MediaType
import com.klipy.demoapp.presentation.features.conversation.model.MessageUiModel
import com.klipy.demoapp.presentation.features.conversation.model.TextMessage

data class ConversationViewState(
    val conversationId: String?,
    val title: String = getConversationTitle(conversationId),
    val messages: List<MessageUiModel> = getMockMessages(conversationId),
    val messageText: String = "",
    val isLoading: Boolean = false,
    val categories: List<Category>? = null,
    val chosenCategory: Category? = null,
    val mediaTypes: List<MediaType>? = null,
    val chosenMediaType: MediaType? = null,
    val searchInput: String = "",
    val mediaItems: List<MediaItem> = emptyList(),
    val selectedMediaItem: MediaItem? = null,
    val playingClip: ClipMessage? = null
)

private fun getConversationTitle(conversationId: String?): String {
    return when (conversationId) {
        "0" -> "KLIPY"
        "1" -> "John Brown"
        "2" -> "Sarah \uD83D\uDC85\uD83C\uDFFB"
        "3" -> "Alex"
        else -> ""
    }
}

private fun getMockMessages(conversationId: String?): List<MessageUiModel> {
    return when (conversationId) {
        "0" -> mockMessages1
        "1" -> mockMessages2
        "2" -> mockMessages3
        "3" -> mockMessages4
        else -> emptyList()
    }
}

private val mockMessages1 = listOf(
    TextMessage(
        text = "Hey! I’m using this demo app to help me with the integration to KLIPY.",
        isFromCurrentUser = true
    ),
    TextMessage(
        text = "Hi! Welcome to the KLIPY Demo App.",
        isFromCurrentUser = false
    ),
    TextMessage(
        text = "Feel free to use all the fun content",
        isFromCurrentUser = false
    ),
    GifMessage(
        url = "https://static.klipy.com/ii/d7aec6f6f171607374b2065c836f92f4/4d/7b/tOuOhBXs.gif",
        width = 498,
        height = 372,
        isFromCurrentUser = false
    ),
)

private val mockMessages2 = listOf(
    TextMessage(
        text = "Hey John",
        isFromCurrentUser = true
    ),
    GifMessage(
        url = "https://static.klipy.com/ii/da290b156d64898341638f3c299e7478/e8/ae/ejlWiBy8.gif",
        width = 480,
        height = 480,
        isFromCurrentUser = true
    ),
    TextMessage(
        text = "Hi, how’s it going?",
        isFromCurrentUser = false
    ),
    GifMessage(
        url = "https://static.klipy.com/ii/c98c4a4935d23b95805f0befee091d8a/e5/bb/FHYQfewc.gif",
        width = 499,
        height = 499,
        isFromCurrentUser = false
    ),
    TextMessage(
        text = "All good!",
        isFromCurrentUser = true
    )
)

private val mockMessages3 = listOf(
    TextMessage(
        text = "Please remind me about my appointment time",
        isFromCurrentUser = true
    ),
    TextMessage(
        text = "at 4",
        isFromCurrentUser = false
    ),
    GifMessage(
        url = "https://static.klipy.com/ii/c98c4a4935d23b95805f0befee091d8a/a1/7e/YG9FpzVE.gif",
        width = 240,
        height = 340,
        isFromCurrentUser = false
    ),
)

private val mockMessages4 = listOf(
    TextMessage(
        text = "hey, how’s it going?",
        isFromCurrentUser = false
    ),
)