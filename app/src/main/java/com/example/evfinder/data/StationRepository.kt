package com.example.evfinder.data

import com.example.evfinder.model.ChargingStation
import com.example.evfinder.model.ConnectorType
import com.example.evfinder.model.PowerCategory
import com.example.evfinder.model.StationFilter
import com.example.evfinder.model.StationStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StationRepository {

    private val initialStations = listOf(
        ChargingStation(
            id = "st_01",
            name = "YPF Punto Eléctrico - Puerto Madero",
            operator = "YPF",
            address = "Av. Alicia Moreau de Justo 1000",
            city = "Buenos Aires",
            latitude = -34.6083,
            longitude = -58.3672,
            powerKw = 150,
            connectors = listOf(ConnectorType.CCS_2, ConnectorType.TYPE_2),
            status = StationStatus.AVAILABLE,
            pricePerKwh = 520.0,
            openingHours = "24 Horas",
            rating = 4.9,
            totalPlugs = 4,
            availablePlugs = 3
        ),
        ChargingStation(
            id = "st_02",
            name = "Shell Recharge - Palermo Hollywood",
            operator = "Shell",
            address = "Av. Juan B. Justo 1600",
            city = "Buenos Aires",
            latitude = -34.5825,
            longitude = -58.4350,
            powerKw = 50,
            connectors = listOf(ConnectorType.CCS_2, ConnectorType.CHADEMO, ConnectorType.TYPE_2),
            status = StationStatus.AVAILABLE,
            pricePerKwh = 480.0,
            openingHours = "24 Horas",
            rating = 4.7,
            totalPlugs = 3,
            availablePlugs = 2
        ),
        ChargingStation(
            id = "st_03",
            name = "ChargeBox Net - Alto Palermo",
            operator = "ChargeBox",
            address = "Av. Santa Fe 3253",
            city = "Buenos Aires",
            latitude = -34.5880,
            longitude = -58.4101,
            powerKw = 22,
            connectors = listOf(ConnectorType.TYPE_2),
            status = StationStatus.BUSY,
            pricePerKwh = 390.0,
            openingHours = "09:00 - 22:00",
            rating = 4.5,
            totalPlugs = 2,
            availablePlugs = 0
        ),
        ChargingStation(
            id = "st_04",
            name = "Axion Energy - Belgrano",
            operator = "Axion Energy",
            address = "Av. Cabildo 2200",
            city = "Buenos Aires",
            latitude = -34.5612,
            longitude = -58.4560,
            powerKw = 120,
            connectors = listOf(ConnectorType.CCS_2, ConnectorType.GBT),
            status = StationStatus.AVAILABLE,
            pricePerKwh = 510.0,
            openingHours = "24 Horas",
            rating = 4.8,
            totalPlugs = 4,
            availablePlugs = 2
        ),
        ChargingStation(
            id = "st_05",
            name = "Enel X - San Telmo",
            operator = "Enel X",
            address = "Av. Paseo Colón 800",
            city = "Buenos Aires",
            latitude = -34.6189,
            longitude = -58.3712,
            powerKw = 50,
            connectors = listOf(ConnectorType.CCS_2, ConnectorType.TYPE_2),
            status = StationStatus.OUT_OF_SERVICE,
            pricePerKwh = 450.0,
            openingHours = "24 Horas",
            rating = 3.8,
            totalPlugs = 2,
            availablePlugs = 0
        ),
        ChargingStation(
            id = "st_06",
            name = "YPF Punto Eléctrico - Vicente López",
            operator = "YPF",
            address = "Av. del Libertador 1800",
            city = "Vicente López",
            latitude = -34.5320,
            longitude = -58.4720,
            powerKw = 160,
            connectors = listOf(ConnectorType.CCS_2, ConnectorType.TYPE_2, ConnectorType.TESLA),
            status = StationStatus.AVAILABLE,
            pricePerKwh = 530.0,
            openingHours = "24 Horas",
            rating = 4.9,
            totalPlugs = 4,
            availablePlugs = 4
        ),
        ChargingStation(
            id = "st_07",
            name = "Shell Recharge - Córdoba Centro",
            operator = "Shell",
            address = "Av. Colón 1200",
            city = "Córdoba",
            latitude = -31.4135,
            longitude = -64.1810,
            powerKw = 50,
            connectors = listOf(ConnectorType.CCS_2, ConnectorType.CHADEMO),
            status = StationStatus.AVAILABLE,
            pricePerKwh = 470.0,
            openingHours = "24 Horas",
            rating = 4.6,
            totalPlugs = 2,
            availablePlugs = 1
        ),
        ChargingStation(
            id = "st_08",
            name = "ChargeBox Net - Rosario Costanera",
            operator = "ChargeBox",
            address = "Av. del Huerto 1100",
            city = "Rosario",
            latitude = -32.9468,
            longitude = -60.6305,
            powerKw = 22,
            connectors = listOf(ConnectorType.TYPE_2),
            status = StationStatus.AVAILABLE,
            pricePerKwh = 400.0,
            openingHours = "24 Horas",
            rating = 4.7,
            totalPlugs = 2,
            availablePlugs = 2
        ),
        ChargingStation(
            id = "st_09",
            name = "YPF Punto Eléctrico - Mendoza Plaza",
            operator = "YPF",
            address = "Acceso Este 3280",
            city = "Mendoza",
            latitude = -32.8908,
            longitude = -68.8272,
            powerKw = 100,
            connectors = listOf(ConnectorType.CCS_2, ConnectorType.GBT),
            status = StationStatus.BUSY,
            pricePerKwh = 500.0,
            openingHours = "24 Horas",
            rating = 4.8,
            totalPlugs = 2,
            availablePlugs = 0
        ),
        ChargingStation(
            id = "st_10",
            name = "Axion Energy - La Plata Centro",
            operator = "Axion Energy",
            address = "Calle 7 y 44",
            city = "La Plata",
            latitude = -34.9123,
            longitude = -57.9540,
            powerKw = 50,
            connectors = listOf(ConnectorType.CCS_2, ConnectorType.TYPE_2),
            status = StationStatus.AVAILABLE,
            pricePerKwh = 460.0,
            openingHours = "24 Horas",
            rating = 4.6,
            totalPlugs = 2,
            availablePlugs = 1
        )
    )

    private val _allStations = MutableStateFlow(initialStations)
    val allStations: StateFlow<List<ChargingStation>> = _allStations.asStateFlow()

    // Map of userId -> Set of stationIds
    private val userFavorites = mutableMapOf<String, MutableSet<String>>(
        "taxista@ecogo.app" to mutableSetOf("st_01", "st_04"),
        "usuario@ecogo.app" to mutableSetOf("st_02")
    )

    private val _favoritesFlow = MutableStateFlow<Set<String>>(emptySet())
    val favoritesFlow: StateFlow<Set<String>> = _favoritesFlow.asStateFlow()

    fun updateFavoritesForUser(userId: String, isGuest: Boolean) {
        if (isGuest) {
            _favoritesFlow.value = emptySet()
        } else {
            val favs = userFavorites.getOrPut(userId) { mutableSetOf() }
            _favoritesFlow.value = favs.toSet()
        }
    }

    fun toggleFavorite(userId: String, isGuest: Boolean, stationId: String): Result<Boolean> {
        if (isGuest) {
            return Result.failure(Exception("Debes registrarte o iniciar sesión para guardar estaciones favoritas."))
        }
        val userFavs = userFavorites.getOrPut(userId) { mutableSetOf() }
        val isNowFavorite = if (userFavs.contains(stationId)) {
            userFavs.remove(stationId)
            false
        } else {
            userFavs.add(stationId)
            true
        }
        _favoritesFlow.value = userFavs.toSet()
        return Result.success(isNowFavorite)
    }

    fun filterStations(filter: StationFilter): List<ChargingStation> {
        return initialStations.filter { station ->
            // Search query matches name, operator, city, or address
            val queryMatch = filter.searchQuery.isBlank() ||
                    station.name.contains(filter.searchQuery, ignoreCase = true) ||
                    station.city.contains(filter.searchQuery, ignoreCase = true) ||
                    station.operator.contains(filter.searchQuery, ignoreCase = true) ||
                    station.address.contains(filter.searchQuery, ignoreCase = true)

            // Power filter
            val powerMatch = when (filter.powerCategory) {
                PowerCategory.ALL -> true
                PowerCategory.SLOW_AC -> station.powerKw <= 22
                PowerCategory.FAST_DC -> station.powerKw in 23..100
                PowerCategory.ULTRA_FAST -> station.powerKw > 100
            }

            // Connector type filter
            val connectorMatch = filter.selectedConnectors.isEmpty() ||
                    station.connectors.any { it in filter.selectedConnectors }

            // Status filter
            val statusMatch = filter.statusFilter == null || station.status == filter.statusFilter

            queryMatch && powerMatch && connectorMatch && statusMatch
        }
    }
}
