package com.klipy.demoapp.presentation.uicomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.klipy.demoapp.data.infrastructure.Measurements

/**
 * Calculates width and height of device screen
 */
@Composable
fun getScreenMeasurements(): Measurements {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val screenWidthPx = with(density) { screenWidth.toPx() }.toInt()
    val screenHeightPx = with(density) { screenHeight.toPx() }.toInt()

    return Measurements(screenWidthPx, screenHeightPx)
}

@Composable
fun Modifier.emptyClickable(): Modifier {
    return this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = {}
    )
}