package com.klipy.demoapp.data.infrastructure

import android.content.Context

interface ScreenMeasurementsProvider {

    var device: Measurements
    var mediaSelectorContainer: Measurements
    fun getDensityScaleFactor(): Float
}

class ScreenMeasurementsProviderImpl(
    private val context: Context
): ScreenMeasurementsProvider {
    override var device: Measurements = Measurements(width = 0, height = 0)
    override var mediaSelectorContainer: Measurements = Measurements(width = 0, height = 0)

    override fun getDensityScaleFactor(): Float {
        val metrics = context.resources.displayMetrics
        return metrics.densityDpi / 160f // 160 DPI is the baseline density (mdpi)
    }
}

data class Measurements(
    val width: Int,
    val height: Int
)