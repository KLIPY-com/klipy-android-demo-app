package com.klipy.demoapp.presentation.features.conversation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.klipy.demoapp.presentation.features.conversation.model.ClipMessage
import com.klipy.demoapp.presentation.features.conversation.model.GifMessage
import com.klipy.demoapp.presentation.features.conversation.model.MessageUiModel
import com.klipy.demoapp.presentation.features.conversation.model.TextMessage
import com.klipy.demoapp.presentation.theme.VividPurple
import com.klipy.demoapp.presentation.uicomponents.ExoPlayerView
import com.klipy.demoapp.presentation.uicomponents.GifImage

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messageList: List<MessageUiModel>,
    playingClip: ClipMessage?,
    hideClips: Boolean,
    onClipMessageClicked: (ClipMessage) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom)
    ) {
        item {
            Spacer(modifier = Modifier.height(0.dp))
        }
        items(
            messageList.reversed(),
            key = { it.id }
        ) {
            Message(
                isFromCurrentUser = it.isFromCurrentUser,
                transparentBackground = it !is TextMessage
            ) {
                when (it) {
                    is TextMessage -> {
                        TextMessage(
                            text = it.text,
                            isMine = it.isFromCurrentUser
                        )
                    }

                    is GifMessage -> {
                        GifMessage(
                            url = it.url,
                            ratio = it.height.toFloat() / it.width
                        )
                    }

                    is ClipMessage -> {
                        ClipMessage(
                            url = it.url,
                            ratio = it.height.toFloat() / it.width,
                            isMuted = it != playingClip,
                            hide = hideClips,
                            onClick = {
                                onClipMessageClicked.invoke(it)
                            }
                        )
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(0.dp))
        }
    }
}

@Composable
private fun Message(
    modifier: Modifier = Modifier,
    isFromCurrentUser: Boolean,
    transparentBackground: Boolean,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isFromCurrentUser) 60.dp else 0.dp,
                end = if (isFromCurrentUser) 0.dp else 60.dp
            ),
        contentAlignment = if (isFromCurrentUser) {
            Alignment.CenterEnd
        } else {
            Alignment.CenterStart
        }
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isFromCurrentUser) 16.dp else 4.dp,
                        bottomEnd = if (isFromCurrentUser) 4.dp else 16.dp
                    )
                )
                .background(
                    color = if (transparentBackground) {
                        Color.Transparent
                    } else if (isFromCurrentUser) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        VividPurple
                    }
                )
                .padding(if (transparentBackground) 0.dp else 16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun TextMessage(
    modifier: Modifier = Modifier,
    text: String,
    isMine: Boolean
) {
    Text(
        modifier = modifier,
        text = text,
        color = if (isMine) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            Color.White
        }
    )
}

@Composable
private fun GifMessage(
    modifier: Modifier = Modifier,
    url: String,
    ratio: Float
) {
    val gifWidth = 200.dp
    GifImage(
        modifier = modifier
            .width(gifWidth)
            .height(gifWidth * ratio),
        key = url,
        url = url,
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun ClipMessage(
    modifier: Modifier = Modifier,
    hide: Boolean,
    url: String,
    ratio: Float,
    isMuted: Boolean,
    onClick: () -> Unit
) {
    val clipWidth = 200.dp
    Box(
        modifier = modifier
            .width(clipWidth)
            .height(clipWidth * ratio)
            .alpha(if (hide) 0F else 1F)
            .clickable {
                onClick.invoke()
            }
    ) {
        ExoPlayerView(
            modifier = Modifier.fillMaxSize(),
            url = url,
            isMuted = isMuted
        )
        Box(
            modifier = Modifier
                .padding(
                    end = 10.dp,
                    bottom = 10.dp
                )
                .size(24.dp)
                .background(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.8F)
                )
                .align(Alignment.BottomEnd),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = if (isMuted) {
                    Icons.AutoMirrored.Filled.VolumeOff
                } else {
                    Icons.AutoMirrored.Filled.VolumeUp
                },
                contentDescription = "",
                tint = Color.Black
            )
        }
    }
}