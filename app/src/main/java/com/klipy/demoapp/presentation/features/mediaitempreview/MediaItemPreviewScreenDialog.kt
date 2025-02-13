@file:OptIn(ExperimentalMaterial3Api::class)

package com.klipy.demoapp.presentation.features.mediaitempreview

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Copyright
import androidx.compose.material.icons.filled.FrontHand
import androidx.compose.material.icons.filled.NoAdultContent
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.domain.models.isRecent
import com.klipy.demoapp.presentation.features.conversation.ConversationViewModel
import com.klipy.demoapp.presentation.features.conversation.model.MediaType
import com.klipy.demoapp.presentation.features.conversation.model.getSingularName
import com.klipy.demoapp.presentation.uicomponents.ExoPlayerView
import com.klipy.demoapp.presentation.uicomponents.GifImage
import com.klipy.demoapp.presentation.uicomponents.emptyClickable

@Composable
fun MediaItemPreviewScreenDialog(
    viewModel: ConversationViewModel,
    navigateBack: () -> Unit,
    onSent: (mediaItem: MediaItem) -> Unit
) {
    Dialog(onDismissRequest = navigateBack) {
        (LocalView.current.parent as? DialogWindowProvider)?.window?.setDimAmount(0f)
        MediaItemPreviewScreen(viewModel, navigateBack, onSent)
    }
}

@Composable
private fun MediaItemPreviewScreen(
    viewModel: ConversationViewModel,
    navigateBack: () -> Unit,
    onSent: (mediaItem: MediaItem) -> Unit
) {
    val viewState by viewModel.viewState.collectAsState()
    val mediaItem = viewState.selectedMediaItem ?: return
    val showHideFromRecentButton = viewState.chosenCategory?.isRecent() == true
    val context = LocalContext.current
    val width = 300.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    navigateBack.invoke()
                }
            )
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        if (mediaItem.mediaType == MediaType.CLIP) {
            mediaItem.highQualityMetaData?.let { mp4 ->
                val ratio = mp4.height.toFloat() / mp4.width
                ExoPlayerView(
                    modifier = Modifier
                        .width(width)
                        .height(width * ratio),
                    url = mp4.url,
                    isMuted = false
                )
            }
        } else {
            mediaItem.highQualityMetaData?.let { gif ->
                val ratio = gif.height.toFloat() / gif.width
                GifImage(
                    modifier = Modifier
                        .width(width)
                        .height(width * ratio)
                        .emptyClickable(),
                    key = mediaItem,
                    url = gif.url,
                    contentScale = ContentScale.Crop,
                    placeholderUrl = mediaItem.lowQualityMetaData?.url,
                    errorUrl = mediaItem.lowQualityMetaData?.url
                )
            }
        }
        Actions(
            modifier = Modifier
                .wrapContentWidth()
                .emptyClickable(),
            mediaType = mediaItem.mediaType.getSingularName(),
            showHideFromRecentButton = showHideFromRecentButton,
            onSent = {
                onSent.invoke(mediaItem)
            },
            onReport = { reason ->
                viewModel.report(mediaItem, reason)
                Toast.makeText(
                    context,
                    "Klipy moderators will review your report. Thank you!",
                    Toast.LENGTH_SHORT
                ).show()
                navigateBack.invoke()
            },
            onHideFromRecent = {
                viewModel.hideFromRecent(mediaItem)
                navigateBack.invoke()
            }
        )
    }
}