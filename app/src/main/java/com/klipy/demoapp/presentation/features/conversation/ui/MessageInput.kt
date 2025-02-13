package com.klipy.demoapp.presentation.features.conversation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MessageInput(
    modifier: Modifier = Modifier,
    text: String,
    arrowIcon: ImageVector?,
    onTextChanged: (String) -> Unit,
    onMessageSent: () -> Unit,
    onMoreClicked: () -> Unit,
    onArrowClicked: () -> Unit
) {
    var inputIsFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    fun clearFocus() {
        focusRequester.freeFocus()
        focusManager.clearFocus(true)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        MessageTextInput(
            modifier = Modifier
                .weight(1F)
                .wrapContentHeight()
                .width(0.dp),
            text = text,
            focusRequester = focusRequester,
            onTextChanged = onTextChanged,
            onMessageSent = {
                onMessageSent.invoke()
                clearFocus()
            },
            onFocused = {
                inputIsFocused = it
            }
        )
        Spacer(Modifier.width(8.dp))
        Image(
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    clearFocus()
                    onMoreClicked.invoke()
                },
            imageVector = Icons.Filled.Apps,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            contentDescription = "",
        )
        Spacer(Modifier.width(8.dp))
        if ((inputIsFocused.not() || text.isBlank()) && arrowIcon != null) {
            Image(
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        clearFocus()
                        onArrowClicked.invoke()
                    },
                imageVector = arrowIcon,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                contentDescription = "",
            )
        } else {
            Image(
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        onMessageSent.invoke()
                        clearFocus()
                    },
                imageVector = Icons.AutoMirrored.Filled.Send,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                contentDescription = "",
            )
        }
    }
}

@Composable
private fun MessageTextInput(
    modifier: Modifier = Modifier,
    text: String,
    focusRequester: FocusRequester,
    onTextChanged: (String) -> Unit,
    onMessageSent: () -> Unit,
    onFocused: (focused: Boolean) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    onFocused.invoke(focusState.isFocused)
                },
            value = text,
            onValueChange = { onTextChanged(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions {
                if (text.isNotBlank()) onMessageSent()
            },
            textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
        )
        if (text.isEmpty()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Enter message",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F)
            )
        }
    }
}