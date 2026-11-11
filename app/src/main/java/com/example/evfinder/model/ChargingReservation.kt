package com.example.evfinder.model

data class ChargingReservation(
    val id: String,
    val stationId: String,
    val stationName: String,
    val connectorType: ConnectorType,
    val reservedAtMillis: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 15,
    val isActive: Boolean = true
)
