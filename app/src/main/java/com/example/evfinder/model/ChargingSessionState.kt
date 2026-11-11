package com.example.evfinder.model

data class ChargingSessionState(
    val isCharging: Boolean = false,
    val stationId: String = "",
    val stationName: String = "",
    val currentBatteryPercent: Int = 20,
    val targetBatteryPercent: Int = 80,
    val energyKwhDelivered: Double = 0.0,
    val chargingPowerKw: Int = 50,
    val pricePerKwh: Double = 480.0,
    val elapsedTimeSeconds: Int = 0,
    val isCompleted: Boolean = false
) {
    val currentCost: Double
        get() = energyKwhDelivered * pricePerKwh
}
