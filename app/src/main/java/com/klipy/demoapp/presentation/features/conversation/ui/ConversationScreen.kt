@file:OptIn(ExperimentalMaterial3Api::class)

package com.klipy.demoapp.presentation.features.conversation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klipy.demoapp.presentation.features.conversation.ConversationViewModel
import com.klipy.demoapp.presentation.features.conversation.model.MediaType
import com.klipy.demoapp.presentation.features.mediaitempreview.MediaItemPreviewScreenDialog
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ConversationScreen(
    conversationId: String?,
    onBackClicked: () -> Unit
) {
    val viewModel: ConversationViewModel = koinViewModel(parameters = { parametersOf(conversationId) })
    val viewState by viewModel.viewState.collectAsState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    var mediaSelectorVisibility by remember { mutableStateOf(MediaSelectorVisibility.HIDDEN) }
    var mediaSelectorShownOnce by remember { mutableStateOf(false) }
    var previewVisible by remember { mutableStateOf(false) }
    val messageListWeight by animateFloatAsState(
        targetValue = if (mediaSelectorVisibility == MediaSelectorVisibility.FULLY_EXPANDED) 0f else 1f,
    )
    var keyboardHeight by remember { mutableStateOf(300.dp) }
    val mediaSelectorHeight by animateDpAsState(
        targetValue = if (mediaSelectorVisibility.isVisible()) keyboardHeight else 0.dp,
    )
    val arrowIcon = when (mediaSelectorVisibility) {
        MediaSelectorVisibility.HIDDEN -> null
        MediaSelectorVisibility.PARTIALLY_EXPANDED -> Icons.Filled.KeyboardArrowUp
        MediaSelectorVisibility.FULLY_EXPANDED -> Icons.Filled.KeyboardArrowDown
    }
    LaunchedEffect(mediaSelectorVisibility) {
        if (mediaSelectorVisibility.isVisible() && mediaSelectorShownOnce.not()) {
            viewModel.fetchInitialData()
            mediaSelectorShownOnce = true
        }
    }
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .then(
                if (previewVisible) {
                    Modifier.blur(100.dp)
                } else {
                    Modifier
                }
            ),
        topBar = {
            Toolbar(
                title = viewState.title,
                onBackClicked = onBackClicked
            )
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                MessageList(
                    modifier = Modifier
                        .then(
                            if (messageListWeight == 0F) {
                                Modifier.height(0.dp)
                            } else {
                                Modifier.weight(messageListWeight)
                            }
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    messageList = viewState.messages,
                    playingClip = viewState.playingClip,
                    hideClips = previewVisible,
                    onClipMessageClicked = {
                        viewModel.onClipMessageClicked(it)
                    }
                )
                MessageInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .run {
                            val calculatedKeyboardHeight = WindowInsets.ime
                                .exclude(WindowInsets.navigationBars)
                                .asPaddingValues()
                                .calculateBottomPadding()
                            if (keyboardHeight < calculatedKeyboardHeight) {
                                keyboardHeight = calculatedKeyboardHeight
                            }
                            then(
                                Modifier.padding(
                                    bottom = if (mediaSelectorVisibility.isVisible()) {
                                        0.dp
                                    } else {
                                        calculatedKeyboardHeight
                                    }
                                )
                            )
                        },
                    text = viewState.messageText,
                    arrowIcon = arrowIcon,
                    onTextChanged = {
                        viewModel.onMessageTextChanged(it)
                    },
                    onMessageSent = {
                        viewModel.onSendClicked()
                    },
                    onMoreClicked = {
                        mediaSelectorVisibility = if (mediaSelectorVisibility.isVisible()) {
                            MediaSelectorVisibility.HIDDEN
                        } else {
                            MediaSelectorVisibility.PARTIALLY_EXPANDED
                        }
                    },
                    onArrowClicked = {
                        mediaSelectorVisibility =
                            if (mediaSelectorVisibility == MediaSelectorVisibility.PARTIALLY_EXPANDED) {
                                MediaSelectorVisibility.FULLY_EXPANDED
                            } else {
                                MediaSelectorVisibility.PARTIALLY_EXPANDED
                            }
                    }
                )
                MediaSelector(
                    modifier = Modifier
                        .then(
                            if (mediaSelectorVisibility == MediaSelectorVisibility.FULLY_EXPANDED) {
                                Modifier.weight(1F)
                            } else {
                                Modifier.height(mediaSelectorHeight)
                            }
                        ),
                    isLoading = viewState.isLoading,
                    categories = viewState.categories,
                    chosenCategory = viewState.chosenCategory,
                    mediaTypes = viewState.mediaTypes ?: emptyList(),
                    chosenMediaType = viewState.chosenMediaType,
                    mediaItems = viewState.mediaItems,
                    searchInput = viewState.searchInput,
                    onCategoryClicked = {
                        viewModel.onCategoryClicked(it)
                    },
                    onMediaTypeClicked = {
                        viewModel.onMediaTypeClicked(it)
                    },
                    onLoadMoreItems = {
                        viewModel.onLoadMoreItems()
                    },
                    onMediaItemClicked = {
                        if (it.mediaType == MediaType.CLIP) {
                            viewModel.onMediaItemSelected(it)
                            previewVisible = true
                        } else {
                            viewModel.onMediaItemClicked(it)
                            mediaSelectorVisibility = MediaSelectorVisibility.PARTIALLY_EXPANDED
                        }
                    },
                    onMediaItemLongClicked = { mediaItem ->
                        viewModel.onMediaItemSelected(mediaItem)
                        previewVisible = true
                    },
                    onSearchInputChanged = {
                        viewModel.onSearchInputEntered(it)
                    },
                    onSearchFocused = {
                        if (it && mediaSelectorVisibility != MediaSelectorVisibility.FULLY_EXPANDED) {
                            mediaSelectorVisibility = MediaSelectorVisibility.FULLY_EXPANDED
                        }
                    },
                    onContainerMeasured = { width, height, screenWidth, screenHeight ->
                        viewModel.onScreenMeasured(
                            containerWidthDp = width,
                            containerHeightDp = height,
                            screenWidthPx = screenWidth,
                            screenHeightPx = screenHeight
                        )
                    }
                )
            }
        }
    }
    if (previewVisible) {
        MediaItemPreviewScreenDialog(
            viewModel,
            navigateBack = {
                previewVisible = false
            },
            onSent = {
                previewVisible = false
                viewModel.onMediaItemClicked(it)
                mediaSelectorVisibility = MediaSelectorVisibility.PARTIALLY_EXPANDED
            }
        )
    }
    BackHandler(
        enabled = mediaSelectorVisibility.isVisible()
    ) {
        if (mediaSelectorVisibility == MediaSelectorVisibility.FULLY_EXPANDED) {
            mediaSelectorVisibility = MediaSelectorVisibility.PARTIALLY_EXPANDED
        } else if (mediaSelectorVisibility == MediaSelectorVisibility.PARTIALLY_EXPANDED) {
            mediaSelectorVisibility = MediaSelectorVisibility.HIDDEN
        }
    }
}

@Composable
private fun Toolbar(
    title: String,
    onBackClicked: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )
        },
        navigationIcon = {
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .size(26.dp)
                    .clickable {
                        onBackClicked.invoke()
                    },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        },
        actions = {
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .size(26.dp),
                imageVector = Icons.Outlined.Call,
                contentDescription = "Call",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

private enum class MediaSelectorVisibility {
    HIDDEN, PARTIALLY_EXPANDED, FULLY_EXPANDED;

    fun isVisible() = this != HIDDEN
}