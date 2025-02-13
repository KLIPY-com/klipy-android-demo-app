@file:OptIn(ExperimentalFoundationApi::class)

package com.klipy.demoapp.presentation.features.conversation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.klipy.demoapp.domain.models.Category
import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.presentation.features.conversation.model.MediaType
import com.klipy.demoapp.presentation.uicomponents.getScreenMeasurements

@Composable
fun MediaSelector(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    categories: List<Category>?,
    chosenCategory: Category?,
    mediaTypes: List<MediaType>,
    chosenMediaType: MediaType?,
    mediaItems: List<MediaItem>,
    searchInput: String,
    onCategoryClicked: (category: Category) -> Unit,
    onMediaTypeClicked: (mediaType: MediaType) -> Unit,
    onMediaItemClicked: (mediaItem: MediaItem) -> Unit,
    onMediaItemLongClicked: (mediaItem: MediaItem) -> Unit,
    onLoadMoreItems: () -> Unit,
    onSearchInputChanged: (input: String) -> Unit,
    onSearchFocused: (focused: Boolean) -> Unit,
    onContainerMeasured: (Int, Int, Int, Int) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    Column(modifier = modifier.nestedScroll(rememberNestedScrollInteropConnection())) {
        Toolbar(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            searchInput = searchInput,
            categories = categories,
            chosenCategory = chosenCategory,
            focusRequester = focusRequester,
            focusManager = focusManager,
            onSearchInputChanged = onSearchInputChanged,
            onSearchFocused = onSearchFocused,
            onCategoryClicked = onCategoryClicked
        )
        MediaItems(
            modifier = Modifier
                .height(0.dp)
                .fillMaxWidth()
                .weight(1F)
                .background(MaterialTheme.colorScheme.background),
            mediaItems = mediaItems,
            isLoading = isLoading,
            loadMoreItems = onLoadMoreItems,
            onMediaItemClicked = onMediaItemClicked,
            onMediaItemLongClicked = onMediaItemLongClicked,
            onContainerMeasured = onContainerMeasured
        )
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(
                    vertical = 10.dp
                )
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            MediaTypesTabSelector(
                modifier = Modifier.wrapContentWidth(),
                mediaTypes = mediaTypes,
                chosenMediaType = chosenMediaType,
                onMediaTypeClicked = {
                    focusRequester.freeFocus()
                    focusManager.clearFocus(force = true)
                    onMediaTypeClicked.invoke(it)
                }
            )
        }
    }
}

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    searchInput: String,
    categories: List<Category>?,
    chosenCategory: Category?,
    focusRequester: FocusRequester,
    focusManager: FocusManager,
    onSearchInputChanged: (input: String) -> Unit,
    onSearchFocused: (focused: Boolean) -> Unit,
    onCategoryClicked: (category: Category) -> Unit,
) {
    val iconSize = 24.dp
    val context = LocalContext.current
    var isSearchFocused by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .padding(12.dp)
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = Icons.Outlined.Search,
            contentDescription = "Search"
        )
        Spacer(Modifier.width(6.dp))
        SearchInput(
            modifier = Modifier
                .then(
                    if (isSearchFocused) {
                        Modifier.weight(1F)
                    } else {
                        Modifier.width(200.dp)
                    }
                ),
            text = searchInput,
            focusRequester = focusRequester,
            onFocusChanged = { isFocused ->
                isSearchFocused = isFocused
                onSearchFocused.invoke(isFocused)
            },
            onTextChanged = {
                onSearchInputChanged.invoke(it)
            },
        )
        if (isSearchFocused || searchInput.isNotEmpty()) {
            Icon(
                modifier = Modifier
                    .size(iconSize)
                    .clickable {
                        onSearchInputChanged.invoke("")
                        focusRequester.freeFocus()
                        focusManager.clearFocus(force = true)
                    },
                imageVector = Icons.Outlined.Close,
                contentDescription = "Clear Search"
            )
        }
        if (isSearchFocused.not()) {
            categories?.forEach { category ->
                Spacer(modifier = Modifier.width(6.dp))
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(category.url)
                        .build(),
                    contentDescription = "Category",
                    modifier = Modifier
                        .size(iconSize)
                        .clickable {
                            onCategoryClicked.invoke(category)
                        },
                    colorFilter = if (category == chosenCategory) {
                        ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    } else {
                        ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    }
                )
            }
        }
    }
}

@Composable
fun MediaItems(
    modifier: Modifier = Modifier,
    mediaItems: List<MediaItem>,
    isLoading: Boolean,
    loadMoreItems: () -> Unit,
    onMediaItemClicked: (mediaItem: MediaItem) -> Unit,
    onMediaItemLongClicked: (mediaItem: MediaItem) -> Unit,
    onContainerMeasured: (Int, Int, Int, Int) -> Unit
) {
    val listState = rememberLazyListState()
    val gap = 5.dp
    var containerWidth by remember { mutableStateOf(0.dp) }
    BoxWithConstraints(
        modifier = modifier
    ) {
        val density = LocalDensity.current
        val measuredWidth = with(density) { constraints.maxWidth.toDp() }
        if (containerWidth == 0.dp && measuredWidth != 0.dp) {
            containerWidth = measuredWidth
            val screenMeasurements = getScreenMeasurements()
            onContainerMeasured.invoke(
                measuredWidth.value.toInt(),
                with(density) { constraints.maxHeight.toDp() }.value.toInt(),
                screenMeasurements.width,
                screenMeasurements.height
            )
        }
        if (mediaItems.isNotEmpty()) {
            MasonryLayout(
                modifier = Modifier.fillMaxSize(),
                listState = listState,
                items = mediaItems,
                isLoading = isLoading,
                gap = gap,
                loadMore = loadMoreItems,
                onMediaItemClicked = onMediaItemClicked,
                onMediaItemLongClicked = onMediaItemLongClicked
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = if (isLoading) "Loading..." else "No results to show",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6F),
                    fontSize = if (isLoading) 14.sp else 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun MediaTypesTabSelector(
    modifier: Modifier = Modifier,
    mediaTypes: List<MediaType>,
    chosenMediaType: MediaType?,
    onMediaTypeClicked: (mediaType: MediaType) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        mediaTypes.forEach { mediaType ->
            MediaTypeTab(
                modifier = Modifier.clickable { onMediaTypeClicked.invoke(mediaType) },
                text = mediaType.title,
                isSelected = mediaType == chosenMediaType
            )
        }
    }
}

@Composable
private fun MediaTypeTab(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean
) {
    Text(
        modifier = modifier
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 10.dp, vertical = 3.dp),
        text = text,
        color = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    )
}

@Composable
private fun SearchInput(
    modifier: Modifier,
    text: String,
    focusRequester: FocusRequester,
    onFocusChanged: (isFocused: Boolean) -> Unit,
    onTextChanged: (String) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        BasicTextField(
            modifier = Modifier
                .focusable()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    onFocusChanged.invoke(focusState.isFocused)
                },
            value = text,
            onValueChange = { onTextChanged(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
        )
        if (text.isEmpty()) {
            Text(
                text = "Search",
                color = LocalContentColor.current.copy(alpha = 0.6F)
            )
        }
    }
}