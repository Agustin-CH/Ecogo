package com.example.evfinder.model

data class StationReview(
    val id: String,
    val stationId: String,
    val userName: String,
    val rating: Double,
    val comment: String,
    val dateText: String
)
