package com.klipy.demoapp.data.infrastructure

interface ScreenMeasurementsProvider {

    var device: Measurements
    var mediaSelectorContainer: Measurements
}

class ScreenMeasurementsProviderImpl: ScreenMeasurementsProvider {
    override var device: Measurements = Measurements(width = 0, height = 0)
    override var mediaSelectorContainer: Measurements = Measurements(width = 0, height = 0)
}

data class Measurements(
    val width: Int,
    val height: Int
)