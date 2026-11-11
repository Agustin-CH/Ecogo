package com.example.evfinder.data

import com.example.evfinder.model.ChargingReservation
import com.example.evfinder.model.ChargingStation
import com.example.evfinder.model.ConnectorType
import com.example.evfinder.model.PowerCategory
import com.example.evfinder.model.StationFilter
import com.example.evfinder.model.StationReview
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
            name = "ChargeBox Net - Recoleta Mall",
            operator = "ChargeBox",
            address = "Vicente López 2050",
            city = "Buenos Aires",
            latitude = -34.5888,
            longitude = -58.3912,
            powerKw = 22,
            connectors = listOf(ConnectorType.TYPE_2),
            status = StationStatus.AVAILABLE,
            pricePerKwh = 390.0,
            openingHours = "10:00 - 22:00",
            rating = 4.4,
            totalPlugs = 2,
            availablePlugs = 1
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

    // Waiting Queue Map: stationId -> List of waiting users
    private val waitingListMap = mutableMapOf<String, MutableList<String>>(
        "st_03" to mutableListOf("Roberto M. (Fila #1)"),
        "st_09" to mutableListOf("Gustavo P. (Fila #1)", "Ana K. (Fila #2)")
    )

    // Initial reviews map
    private val reviewsMap = mutableMapOf<String, MutableList<StationReview>>(
        "st_01" to mutableListOf(
            StationReview("r1", "st_01", "Mariano V.", 5.0, "Carga ultra rápida excelente. El tótem es nuevo.", "Hace 2 días"),
            StationReview("r2", "st_01", "Carlos Taxista", 4.8, "Muy buena ubicación en Puerto Madero. Hay cafetería al lado.", "Hace 1 semana")
        ),
        "st_02" to mutableListOf(
            StationReview("r3", "st_02", "Laura G.", 4.5, "Funciona muy bien el conector CCS2.", "Hace 3 días")
        )
    )

    private val _activeReservation = MutableStateFlow<ChargingReservation?>(null)
    val activeReservation: StateFlow<ChargingReservation?> = _activeReservation.asStateFlow()

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

    fun getReviewsForStation(stationId: String): List<StationReview> {
        return reviewsMap[stationId] ?: emptyList()
    }

    fun addReview(stationId: String, userName: String, rating: Double, comment: String) {
        val newReview = StationReview(
            id = "r_${System.currentTimeMillis()}",
            stationId = stationId,
            userName = userName.ifBlank { "Usuario ECOGO" },
            rating = rating,
            comment = comment,
            dateText = "Reciente"
        )
        val list = reviewsMap.getOrPut(stationId) { mutableListOf() }
        list.add(0, newReview)
    }

    fun reportProblem(stationId: String, comment: String) {
        // Add a 1-star review to reflect the problem in the community feed
        val problemReview = StationReview(
            id = "r_${System.currentTimeMillis()}",
            stationId = stationId,
            userName = "Usuario Anónimo",
            rating = 1.0,
            comment = "⚠️ Reporte de falla técnica: $comment",
            dateText = "Reciente"
        )
        val list = reviewsMap.getOrPut(stationId) { mutableListOf() }
        list.add(0, problemReview)
        
        // We no longer turn off the station completely for one report. 
        // We just register it for administrators/community to see.
    }

    fun getWaitingList(stationId: String): List<String> {
        return waitingListMap[stationId] ?: emptyList()
    }

    fun isUserInWaitingList(stationId: String, userName: String): Boolean {
        val queue = waitingListMap[stationId] ?: return false
        return queue.any { it.contains(userName) || it.contains("Tu Turno") }
    }

    fun joinWaitingList(stationId: String, userName: String): Int {
        val queue = waitingListMap.getOrPut(stationId) { mutableListOf() }
        val entryName = "$userName (Tu Turno)"
        queue.add(entryName)
        return queue.size
    }

    fun leaveWaitingList(stationId: String, userName: String) {
        val queue = waitingListMap[stationId] ?: return
        queue.removeAll { it.contains(userName) || it.contains("Tu Turno") }
    }

    fun reservePlug(station: ChargingStation, connector: ConnectorType): Result<ChargingReservation> {
        if (station.status != StationStatus.AVAILABLE || station.availablePlugs <= 0) {
            return Result.failure(Exception("No hay mangueras disponibles para reservar en esta estación."))
        }

        // Decrement available plugs in state
        val updatedList = _allStations.value.map { st ->
            if (st.id == station.id) {
                val newAvailable = (st.availablePlugs - 1).coerceAtLeast(0)
                val newStatus = if (newAvailable == 0) StationStatus.BUSY else st.status
                st.copy(availablePlugs = newAvailable, status = newStatus)
            } else st
        }
        _allStations.value = updatedList

        val reservation = ChargingReservation(
            id = "res_${System.currentTimeMillis()}",
            stationId = station.id,
            stationName = station.name,
            connectorType = connector
        )
        _activeReservation.value = reservation
        return Result.success(reservation)
    }

    fun startCharging(stationId: String): Result<Boolean> {
        val currentRes = _activeReservation.value
        val station = _allStations.value.firstOrNull { it.id == stationId }
            ?: return Result.failure(Exception("Estación no encontrada."))

        if (station.status == StationStatus.OUT_OF_SERVICE) {
            return Result.failure(Exception("La estación se encuentra fuera de servicio por mantenimiento."))
        }

        // Check if station has available plugs OR user holds an active reservation for this station
        val hasReservation = currentRes != null && currentRes.stationId == stationId
        if (station.availablePlugs <= 0 && !hasReservation) {
            return Result.failure(Exception("Todas las mangueras están ocupadas. Únete a la fila de espera."))
        }

        // Consume plug or active reservation
        if (hasReservation) {
            _activeReservation.value = null
        } else {
            val updatedList = _allStations.value.map { st ->
                if (st.id == stationId) {
                    val newAvailable = (st.availablePlugs - 1).coerceAtLeast(0)
                    val newStatus = if (newAvailable == 0) StationStatus.BUSY else st.status
                    st.copy(availablePlugs = newAvailable, status = newStatus)
                } else st
            }
            _allStations.value = updatedList
        }

        return Result.success(true)
    }

    fun finishCharging(stationId: String) {
        val updatedList = _allStations.value.map { st ->
            if (st.id == stationId) {
                val newAvailable = (st.availablePlugs + 1).coerceAtMost(st.totalPlugs)
                val newStatus = if (st.status == StationStatus.BUSY && newAvailable > 0) StationStatus.AVAILABLE else st.status
                st.copy(availablePlugs = newAvailable, status = newStatus)
            } else st
        }
        _allStations.value = updatedList
    }

    fun cancelReservation() {
        val currentRes = _activeReservation.value ?: return
        val updatedList = _allStations.value.map { st ->
            if (st.id == currentRes.stationId) {
                val newAvailable = (st.availablePlugs + 1).coerceAtMost(st.totalPlugs)
                val newStatus = if (st.status == StationStatus.BUSY && newAvailable > 0) StationStatus.AVAILABLE else st.status
                st.copy(availablePlugs = newAvailable, status = newStatus)
            } else st
        }
        _allStations.value = updatedList
        _activeReservation.value = null
    }

    // OLD METHOD REMOVED - REPLACED BY reportProblem(stationId, comment)

    fun filterStations(filter: StationFilter): List<ChargingStation> {
        return _allStations.value.filter { station ->
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
            // Fix: We must show stations that support AT LEAST ONE of the selected connectors
            val connectorMatch = filter.selectedConnectors.isEmpty() ||
                    station.connectors.any { it in filter.selectedConnectors }

            // Status filter
            val statusMatch = filter.statusFilter == null || station.status == filter.statusFilter

            queryMatch && powerMatch && connectorMatch && statusMatch
        }
    }
}
