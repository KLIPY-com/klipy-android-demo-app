package com.klipy.demoapp.presentation.features.conversationlist.model

data class ConversationUiModel(
    val id: Int,
    val name: String,
    val time: String,
    val lastMessage: String,
    val unreadMessages: Int
) {
    companion object {
        fun createMockList() = listOf(
            ConversationUiModel(
                id = 0,
                name = "KLIPY",
                time = "19:42",
                lastMessage = "KLIPY sent a gif",
                unreadMessages = 3
            ),
            ConversationUiModel(
                id = 1,
                name = "John Brown",
                time = "23:11",
                lastMessage = "All good!",
                unreadMessages = 0
            ),
            ConversationUiModel(
                id = 2,
                name = "Sarah \uD83D\uDC85\uD83C\uDFFB",
                time = "17:23",
                lastMessage = "Sarah \uD83D\uDC85\uD83C\uDFFB sent a sticker",
                unreadMessages = 0
            ),
            ConversationUiModel(
                id = 3,
                name = "Alex",
                time = "13:02",
                lastMessage = "hey, how’s it going?",
                unreadMessages = 1
            )
        )
    }
}