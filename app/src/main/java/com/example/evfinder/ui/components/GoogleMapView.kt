package com.example.evfinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evfinder.R
import com.example.evfinder.model.ChargingStation
import com.example.evfinder.model.StationStatus
import com.example.evfinder.ui.theme.EcoGreenPrimary
import com.example.evfinder.ui.theme.StatusAvailable
import com.example.evfinder.ui.theme.StatusBusy
import com.example.evfinder.ui.theme.StatusOutOfService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun GoogleMapView(
    stations: List<ChargingStation>,
    selectedStation: ChargingStation?,
    onStationSelected: (ChargingStation) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val mapProperties = MapProperties(
        isMyLocationEnabled = false,
        mapStyleOptions = if (isDark) {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
        } else null
    )

    // Buenos Aires center
    val defaultLocation = LatLng(-34.6083, -58.3672)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12.5f)
    }

    // Animate camera when station selected
    LaunchedEffect(selectedStation) {
        selectedStation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 14.5f),
                700
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true,
                mapToolbarEnabled = false
            )
        ) {
            stations.forEach { station ->
                val isSelected = selectedStation?.id == station.id
                val pinColor = when (station.status) {
                    StationStatus.AVAILABLE -> StatusAvailable
                    StationStatus.BUSY -> StatusBusy
                    StationStatus.OUT_OF_SERVICE -> StatusOutOfService
                }

                MarkerComposable(
                    state = rememberMarkerState(
                        key = station.id,
                        position = LatLng(station.latitude, station.longitude)
                    ),
                    title = station.name,
                    snippet = "${station.powerKw} kW - ${station.status.label}",
                    onClick = {
                        onStationSelected(station)
                        true
                    }
                ) {
                    GoogleMapMarkerPin(
                        station = station,
                        pinColor = pinColor,
                        isSelected = isSelected
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleMapMarkerPin(
    station: ChargingStation,
    pinColor: Color,
    isSelected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) EcoGreenPrimary else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            shadowElevation = if (isSelected) 8.dp else 4.dp
        ) {
            Text(
                text = "${station.powerKw}kW",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(if (isSelected) 42.dp else 34.dp)
                .shadow(if (isSelected) 10.dp else 4.dp, CircleShape)
                .clip(CircleShape)
                .background(if (isSelected) Color.White else pinColor)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(if (isSelected) pinColor else Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EvStation,
                    contentDescription = station.name,
                    tint = if (isSelected) Color.White else pinColor,
                    modifier = Modifier.size(if (isSelected) 22.dp else 18.dp)
                )
            }
        }
    }
}
