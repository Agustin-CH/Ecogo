package com.example.evfinder.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evfinder.data.AuthRepository
import com.example.evfinder.data.StationRepository
import com.example.evfinder.model.ChargingReservation
import com.example.evfinder.model.ChargingSessionState
import com.example.evfinder.model.ChargingStation
import com.example.evfinder.model.ConnectorType
import com.example.evfinder.model.PowerCategory
import com.example.evfinder.model.StationFilter
import com.example.evfinder.model.StationReview
import com.example.evfinder.model.StationStatus
import com.example.evfinder.model.User
import com.example.evfinder.model.VehicleModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val stationRepository: StationRepository = StationRepository()
) : ViewModel() {

    val currentUser: StateFlow<User> = authRepository.currentUser
    val favorites: StateFlow<Set<String>> = stationRepository.favoritesFlow
    val activeReservation: StateFlow<ChargingReservation?> = stationRepository.activeReservation

    private val _filterState = MutableStateFlow(StationFilter())
    val filterState: StateFlow<StationFilter> = _filterState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _selectedStation = MutableStateFlow<ChargingStation?>(null)
    val selectedStation: StateFlow<ChargingStation?> = _selectedStation.asStateFlow()

    private val _isFilterSheetOpen = MutableStateFlow(false)
    val isFilterSheetOpen: StateFlow<Boolean> = _isFilterSheetOpen.asStateFlow()

    private val _isAuthDialogOpen = MutableStateFlow(false)
    val isAuthDialogOpen: StateFlow<Boolean> = _isAuthDialogOpen.asStateFlow()

    private val _isVehicleSelectorOpen = MutableStateFlow(false)
    val isVehicleSelectorOpen: StateFlow<Boolean> = _isVehicleSelectorOpen.asStateFlow()

    private val _isChargingModalOpen = MutableStateFlow(false)
    val isChargingModalOpen: StateFlow<Boolean> = _isChargingModalOpen.asStateFlow()

    private val _chargingSession = MutableStateFlow(ChargingSessionState())
    val chargingSession: StateFlow<ChargingSessionState> = _chargingSession.asStateFlow()

    private var chargingJob: Job? = null

    val filteredStations: StateFlow<List<ChargingStation>> = combine(
        stationRepository.allStations,
        _filterState
    ) { _, filter ->
        stationRepository.filterStations(filter)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Single unified UI State
    val uiState: StateFlow<MapUiState> = combine(
        filteredStations,
        _filterState,
        _selectedStation,
        _isFilterSheetOpen,
        _isAuthDialogOpen,
        _isLoading,
        _userMessage
    ) { flows ->
        @Suppress("UNCHECKED_CAST")
        MapUiState(
            stations = flows[0] as List<ChargingStation>,
            filter = flows[1] as StationFilter,
            selectedStation = flows[2] as ChargingStation?,
            isFilterSheetOpen = flows[3] as Boolean,
            isAuthDialogOpen = flows[4] as Boolean,
            isLoading = flows[5] as Boolean,
            userMessage = flows[6] as String?
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MapUiState())

    init {
        viewModelScope.launch {
            currentUser.collect { user ->
                stationRepository.updateFavoritesForUser(user.id, user.isGuest)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _filterState.value = _filterState.value.copy(searchQuery = query)
    }

    fun onPowerCategorySelected(category: PowerCategory) {
        _filterState.value = _filterState.value.copy(powerCategory = category)
    }

    fun onConnectorTypeToggled(connector: ConnectorType) {
        val current = _filterState.value.selectedConnectors.toMutableSet()
        if (current.contains(connector)) {
            current.remove(connector)
        } else {
            current.add(connector)
        }
        _filterState.value = _filterState.value.copy(selectedConnectors = current)
    }

    fun onStatusFilterSelected(status: StationStatus?) {
        _filterState.value = _filterState.value.copy(statusFilter = status)
    }

    fun resetFilters() {
        _filterState.value = StationFilter()
        _userMessage.value = "Filtros limpiados"
    }

    fun toggleFilterSheet(open: Boolean) {
        _isFilterSheetOpen.value = open
    }

    fun toggleAuthDialog(open: Boolean) {
        _isAuthDialogOpen.value = open
    }

    fun toggleVehicleSelector(open: Boolean) {
        _isVehicleSelectorOpen.value = open
    }

    fun toggleVehiclePicker(open: Boolean) {
        _isVehicleSelectorOpen.value = open
    }

    fun toggleChargingModal(open: Boolean) {
        _isChargingModalOpen.value = open
    }

    fun selectVehicle(vehicle: VehicleModel?) {
        authRepository.updateUserVehicle(vehicle)
        if (vehicle != null) {
            // Automatically apply filter matching vehicle's supported connectors
            _filterState.value = _filterState.value.copy(selectedConnectors = vehicle.supportedConnectors.toSet())
            _userMessage.value = "Filtro de conectores actualizado para ${vehicle.brand} ${vehicle.model}"
        }
        _isVehicleSelectorOpen.value = false
    }

    fun selectStation(station: ChargingStation?) {
        _selectedStation.value = station
    }

    fun toggleFavorite(stationId: String) {
        val user = currentUser.value
        if (user.isGuest) {
            _userMessage.value = "Inicia sesión para guardar tus estaciones favoritas"
            _isAuthDialogOpen.value = true
            return
        }
        val result = stationRepository.toggleFavorite(user.id, user.isGuest, stationId)
        result.onSuccess { isFav ->
            _userMessage.value = if (isFav) "Guardado en Favoritos" else "Eliminado de Favoritos"
        }.onFailure { err ->
            _userMessage.value = err.message
        }
    }

    // Reservation & Waiting List Feature
    fun reservePlug(station: ChargingStation, connector: ConnectorType) {
        val user = currentUser.value
        if (user.isGuest) {
            _userMessage.value = "Inicia sesión para reservar un punto de carga"
            _isAuthDialogOpen.value = true
            return
        }

        val result = stationRepository.reservePlug(station, connector)
        result.onSuccess {
            _userMessage.value = "¡Punto de carga reservado con éxito por 15 minutos!"
        }.onFailure { err ->
            _userMessage.value = err.message
        }
    }

    fun joinWaitingList(station: ChargingStation) {
        val user = currentUser.value
        if (user.isGuest) {
            _userMessage.value = "Inicia sesión para unirte a la fila de espera"
            _isAuthDialogOpen.value = true
            return
        }

        val position = stationRepository.joinWaitingList(station.id, user.name)
        _userMessage.value = "Te uniste a la fila de espera para ${station.name}. Posición #$position"
    }

    fun getWaitingList(stationId: String): List<String> {
        return stationRepository.getWaitingList(stationId)
    }

    fun cancelReservation() {
        stationRepository.cancelReservation()
        _userMessage.value = "Reserva cancelada"
    }

    // Live Charging Simulation Feature
    fun startChargingSession(station: ChargingStation) {
        val user = currentUser.value
        if (user.isGuest) {
            _userMessage.value = "Inicia sesión para realizar una carga de batería"
            _isAuthDialogOpen.value = true
            return
        }

        val startResult = stationRepository.startCharging(station.id)
        if (startResult.isFailure) {
            _userMessage.value = startResult.exceptionOrNull()?.message
            return
        }

        _chargingSession.value = ChargingSessionState(
            isCharging = true,
            stationId = station.id,
            stationName = station.name,
            currentBatteryPercent = 18,
            targetBatteryPercent = 80,
            energyKwhDelivered = 0.0,
            chargingPowerKw = station.powerKw,
            pricePerKwh = station.pricePerKwh,
            elapsedTimeSeconds = 0,
            isCompleted = false
        )
        _isChargingModalOpen.value = true

        chargingJob?.cancel()
        chargingJob = viewModelScope.launch {
            while (_chargingSession.value.isCharging && !_chargingSession.value.isCompleted) {
                delay(1000)
                val current = _chargingSession.value
                val newSeconds = current.elapsedTimeSeconds + 1
                val newEnergy = current.energyKwhDelivered + (current.chargingPowerKw / 3600.0) * 8.0 // Accelerated factor for demo
                val newBattery = (current.currentBatteryPercent + 1).coerceAtMost(current.targetBatteryPercent)
                val isDone = newBattery >= current.targetBatteryPercent

                _chargingSession.value = current.copy(
                    elapsedTimeSeconds = newSeconds,
                    energyKwhDelivered = newEnergy,
                    currentBatteryPercent = newBattery,
                    isCompleted = isDone,
                    isCharging = !isDone
                )

                if (isDone) {
                    stationRepository.finishCharging(station.id)
                    _userMessage.value = "¡Carga completada al ${current.targetBatteryPercent}%!"
                }
            }
        }
    }

    fun stopChargingSession() {
        chargingJob?.cancel()
        val finalState = _chargingSession.value
        stationRepository.finishCharging(finalState.stationId)
        _chargingSession.value = finalState.copy(isCharging = false, isCompleted = true)
        _userMessage.value = "Carga finalizada. Total: $${finalState.currentCost.toInt()} ARS"
    }

    fun closeChargingModal() {
        chargingJob?.cancel()
        _isChargingModalOpen.value = false
        _chargingSession.value = ChargingSessionState()
    }

    // Reviews & Problem Reporting Feature
    fun getReviewsForStation(stationId: String): List<StationReview> {
        return stationRepository.getReviewsForStation(stationId)
    }

    fun addReview(stationId: String, rating: Double, comment: String) {
        val user = currentUser.value
        stationRepository.addReview(stationId, if (user.isGuest) "Usuario ECOGO" else user.name, rating, comment)
        _userMessage.value = "¡Gracias por compartir tu opinión!"
    }

    fun reportStationProblem(stationId: String) {
        stationRepository.reportProblem(stationId)
        _userMessage.value = "Reporte enviado. Estación marcada en mantenimiento."
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(500)
            _isLoading.value = false
            val result = authRepository.login(email, pass)
            result.onSuccess { user ->
                _isAuthDialogOpen.value = false
                _userMessage.value = "¡Bienvenido de nuevo, ${user.name}!"
                onSuccess()
            }.onFailure { err ->
                _userMessage.value = err.message
            }
        }
    }

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(500)
            _isLoading.value = false
            val result = authRepository.register(name, email, pass)
            result.onSuccess { user ->
                _isAuthDialogOpen.value = false
                _userMessage.value = "¡Cuenta creada con éxito! Bienvenido, ${user.name}."
                onSuccess()
            }.onFailure { err ->
                _userMessage.value = err.message
            }
        }
    }

    fun loginAsGuest() {
        authRepository.loginAsGuest()
        _isAuthDialogOpen.value = false
        _userMessage.value = "Modo invitado activado"
    }

    fun logout() {
        authRepository.logout()
        _userMessage.value = "Sesión cerrada"
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun navigateToStationDirections(context: Context, station: ChargingStation) {
        val gmmIntentUri = Uri.parse("geo:${station.latitude},${station.longitude}?q=${station.latitude},${station.longitude}(${Uri.encode(station.name)})")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        try {
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            val genericIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            try {
                context.startActivity(genericIntent)
            } catch (ex: Exception) {
                _userMessage.value = "No se encontró una aplicación de mapas para dar indicaciones"
            }
        }
    }

    fun refreshStations() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(700)
            _isLoading.value = false
            _userMessage.value = "Información de estaciones actualizada"
        }
    }
}
