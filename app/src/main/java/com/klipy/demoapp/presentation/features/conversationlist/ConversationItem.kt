package com.klipy.demoapp.presentation.features.conversationlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klipy.demoapp.presentation.features.conversationlist.model.ConversationUiModel
import com.klipy.demoapp.presentation.theme.KlipyDemoAppTheme

@Composable
fun ConversationItem(
    conversation: ConversationUiModel,
    onConversationClick: (id: Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = { onConversationClick.invoke(conversation.id) }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar()
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            NameAndTime(
                name = conversation.name,
                time = conversation.time
            )
            Spacer(modifier = Modifier.height(4.dp))
            MessageAndBadge(
                lastMessage = conversation.lastMessage,
                unreadMessages = conversation.unreadMessages
            )
        }

    }
}

@Composable
private fun Avatar() {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Default User Avatar",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
private fun NameAndTime(name: String, time: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.weight(1F))
        Text(
            text = time,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}

@Composable
fun MessageAndBadge(lastMessage: String, unreadMessages: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = lastMessage,
            color =  if (unreadMessages > 0) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7F)
            },
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.weight(1F))
        if (unreadMessages != 0) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(20.dp)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = unreadMessages.toString(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    style = TextStyle(
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        )
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun ConversationItemPreview() {
    KlipyDemoAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            ConversationItem(
                conversation = ConversationUiModel.createMockList().first(),
                onConversationClick = {}
            )
        }
    }
}