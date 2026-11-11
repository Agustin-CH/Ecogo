package com.example.evfinder.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evfinder.model.ChargingStation
import com.example.evfinder.model.PowerCategory
import com.example.evfinder.model.StationFilter
import com.example.evfinder.ui.components.GoogleMapView
import com.example.evfinder.ui.components.OpenMapView
import com.example.evfinder.ui.components.StationSummaryCard
import com.example.evfinder.ui.theme.EcoGreenPrimary
import com.example.evfinder.viewmodel.MainViewModel

@Composable
fun MapScreen(
    stations: List<ChargingStation>,
    filterState: StationFilter,
    selectedStation: ChargingStation?,
    favorites: Set<String>,
    viewModel: MainViewModel,
    onViewDetail: (ChargingStation) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSearchExpanded by remember { mutableStateOf(false) }
    var useOpenStreetMap by remember { mutableStateOf(false) } // Default to Google Maps SDK

    Box(modifier = modifier.fillMaxSize()) {
        // Map View Layer (OpenStreetMap vector engine or Google Maps SDK)
        if (useOpenStreetMap) {
            OpenMapView(
                stations = stations,
                selectedStation = selectedStation,
                onStationSelected = { station ->
                    viewModel.selectStation(station)
                }
            )
        } else {
            GoogleMapView(
                stations = stations,
                selectedStation = selectedStation,
                onStationSelected = { station ->
                    viewModel.selectStation(station)
                }
            )
        }

        // Collapsible / Expandable Top Search Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .animateContentSize(),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                if (!isSearchExpanded && filterState.searchQuery.isEmpty()) {
                    // Compact Collapsed Pill Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { isSearchExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = EcoGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Buscar estación o ciudad...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Active Station Count Badge
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EcoGreenPrimary.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(EcoGreenPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${stations.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoGreenPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = { isSearchExpanded = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Desplegar búsqueda",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    // Expanded Search Bar & Quick Filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = filterState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = { Text("Buscar por ciudad, estación o marca...", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = EcoGreenPrimary
                                )
                            },
                            trailingIcon = {
                                if (filterState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpiar")
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EcoGreenPrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { isSearchExpanded = false },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Plegar búsqueda",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Power Category Filters Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyRow(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(PowerCategory.entries) { category ->
                                val isSelected = filterState.powerCategory == category
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.onPowerCategorySelected(category) },
                                    label = { Text(text = category.label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EcoGreenPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Active Stations Count Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EcoGreenPrimary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(EcoGreenPrimary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${stations.size}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Empty Search Results Card
        if (stations.isEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No se encontraron estaciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "No hay puntos de carga que coincidan con los filtros aplicados.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.resetFilters() },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary)
                    ) {
                        Text("Limpiar filtros")
                    }
                }
            }
        }

        // Floating Action Buttons: Map Layer Toggle, Refresh & Filter
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = if (selectedStation != null) 230.dp else 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Map Layer Switcher (OpenStreetMap vs Google Maps)
            FloatingActionButton(
                onClick = { useOpenStreetMap = !useOpenStreetMap },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EcoGreenPrimary,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(imageVector = Icons.Default.Layers, contentDescription = "Cambiar Capa de Mapa", modifier = Modifier.size(20.dp))
            }

            FloatingActionButton(
                onClick = { viewModel.refreshStations() },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Actualizar", modifier = Modifier.size(20.dp))
            }

            FloatingActionButton(
                onClick = { viewModel.toggleFilterSheet(true) },
                containerColor = EcoGreenPrimary,
                contentColor = Color.White,
                modifier = Modifier.size(48.dp)
            ) {
                BadgedBox(
                    badge = {
                        if (filterState.isActive) {
                            Badge(containerColor = Color.Red)
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.FilterAlt, contentDescription = "Filtros", modifier = Modifier.size(22.dp))
                }
            }
        }

        // Selected Station Summary Card Overlay
        AnimatedVisibility(
            visible = selectedStation != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedStation?.let { station ->
                StationSummaryCard(
                    station = station,
                    isFavorite = favorites.contains(station.id),
                    onToggleFavorite = { viewModel.toggleFavorite(station.id) },
                    onNavigateDirections = { viewModel.navigateToStationDirections(context, station) },
                    onViewDetail = { onViewDetail(station) },
                    onClose = { viewModel.selectStation(null) }
                )
            }
        }
    }
}
