package com.klipy.demoapp.presentation.features.mediaitempreview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Actions(
    modifier: Modifier = Modifier,
    mediaType: String,
    showHideFromRecentButton: Boolean,
    onSent: () -> Unit,
    onReport: (reason: String) -> Unit,
    onHideFromRecent: () -> Unit
) {
    val boxShape = RoundedCornerShape(10.dp)
    var reportReasonsVisible by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .background(
                shape = boxShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6F),
            )
            .clip(
                shape = boxShape
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (reportReasonsVisible.not()) {
            GeneralActions(
                mediaType = mediaType,
                showHideFromRecentButton = showHideFromRecentButton,
                onSent = onSent,
                onReportClicked = {
                    reportReasonsVisible = true
                },
                onHideFromRecent = onHideFromRecent
            )
        } else {
            ReportReasons(
                onReport = onReport,
                onBackClicked = {
                    reportReasonsVisible = false
                }
            )
        }
    }
}

@Composable
private fun GeneralActions(
    mediaType: String,
    showHideFromRecentButton: Boolean,
    onSent: () -> Unit,
    onReportClicked: () -> Unit,
    onHideFromRecent: () -> Unit
) {
    Action(
        modifier = Modifier.wrapContentSize(),
        icon = Icons.AutoMirrored.Filled.Send,
        text = "Send $mediaType",
        onAction = onSent
    )
    if (showHideFromRecentButton) {
        Action(
            modifier = Modifier.wrapContentSize(),
            icon = Icons.Outlined.DeleteOutline,
            text = "Hide from \"Recents\"",
            onAction = onHideFromRecent
        )
    }
    Action(
        modifier = Modifier.wrapContentSize(),
        icon = Icons.Filled.Report,
        text = "Report",
        onAction = onReportClicked
    )
}

@Composable
private fun ReportReasons(
    reasons: List<ReportReason> = reportReasonsList,
    onReport: (reason: String) -> Unit,
    onBackClicked: () -> Unit
) {
    reasons.forEach {
        Action(
            modifier = Modifier.wrapContentSize(),
            icon = it.imageVector,
            text = it.text,
            onAction = {
                onReport.invoke(it.text)
            }
        )
    }
    Action(
        modifier = Modifier.wrapContentSize(),
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        text = "Back",
        onAction = onBackClicked
    )
}

@Composable
private fun Action(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    onAction: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable {
                onAction.invoke()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier
                .size(22.dp),
            imageVector = icon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            style = TextStyle(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None
                )
            ),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

private val reportReasonsList = listOf(
    ReportReason("Violence", Icons.Filled.Campaign),
    ReportReason("Pornography", Icons.Filled.NoAdultContent),
    ReportReason("Child Abuse", Icons.Filled.FrontHand),
    ReportReason("Copyright", Icons.Filled.Copyright),
    ReportReason("Other", Icons.Filled.WarningAmber),
)

private data class ReportReason(
    val text: String,
    val imageVector: ImageVector
)