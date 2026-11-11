package com.example.evfinder.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.window.Dialog
import com.example.evfinder.model.ChargingStation
import com.example.evfinder.model.StationStatus
import com.example.evfinder.ui.components.ReportProblemDialog
import com.example.evfinder.ui.theme.EcoGreenPrimary
import com.example.evfinder.ui.theme.StatusAvailable
import com.example.evfinder.ui.theme.StatusBusy
import com.example.evfinder.ui.theme.StatusOutOfService
import com.example.evfinder.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailScreen(
    station: ChargingStation,
    isFavorite: Boolean,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isReportDialogOpen by remember { mutableStateOf(false) }
    var isReviewDialogOpen by remember { mutableStateOf(false) }
    val waitingListUpdateTrigger by viewModel.waitingListUpdateTrigger.collectAsState()
    
    val reviews = remember(station.id) { viewModel.getReviewsForStation(station.id) }
    val waitingList = remember(station.id, waitingListUpdateTrigger) { viewModel.getWaitingList(station.id) }
    val activeReservation by viewModel.activeReservation.collectAsState()
    val isInQueue = remember(station.id, waitingListUpdateTrigger) { viewModel.isUserInWaitingList(station.id) }

    val hasActiveReservationForStation = activeReservation != null && activeReservation?.stationId == station.id
    val isAvailable = station.status == StationStatus.AVAILABLE && station.availablePlugs > 0
    val isBusy = station.status == StationStatus.BUSY || station.availablePlugs == 0
    val isOutOfService = station.status == StationStatus.OUT_OF_SERVICE

    // Calculate estimated battery charging time (20% -> 80% = ~30 kWh)
    val estChargeMins = (30.0 / station.powerKw * 60.0).toInt().coerceIn(15, 90)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Estación", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al mapa")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(station.id) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { viewModel.navigateToStationDirections(context, station) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Directions, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Iniciar Navegación (Cómo llegar)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Operator Tag & Station Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EcoGreenPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = station.operator.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${station.rating}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = station.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${station.address}, ${station.city}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Info Grid Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Status Row
                    val (statusBg, statusText) = when {
                        isOutOfService -> Pair(StatusOutOfService, "Fuera de servicio por mantenimiento")
                        isBusy -> Pair(StatusBusy, "Ocupado totalmente (0 mangueras libres)")
                        else -> Pair(StatusAvailable, "Disponible (${station.availablePlugs} de ${station.totalPlugs} mangueras libres)")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(statusBg)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusText,
                            fontWeight = FontWeight.Bold,
                            color = statusBg,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailInfoItem(
                            icon = Icons.Default.Bolt,
                            label = "Potencia Máx",
                            value = "${station.powerKw} kW"
                        )
                        DetailInfoItem(
                            icon = Icons.Default.AttachMoney,
                            label = "Precio",
                            value = "$${station.pricePerKwh.toInt()} / kWh"
                        )
                        DetailInfoItem(
                            icon = Icons.Default.Timer,
                            label = "Est. Carga (80%)",
                            value = "~$estChargeMins min"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "La reserva de 15 min otorga tiempo de gracia para conducir hasta la estación.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Actions Row (Airtight Logical Workflow)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isOutOfService) {
                    // Out of service states
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        enabled = false,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sin Servicio", fontSize = 12.sp)
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        enabled = false,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Carga Bloqueada", fontSize = 12.sp)
                    }
                } else if (isBusy) {
                    // When station is BUSY: Button 1 = Join/Leave Queue
                    if (isInQueue) {
                        Button(
                            onClick = { viewModel.leaveWaitingList(station) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salir de Fila", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.joinWaitingList(station) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Unirse a Fila", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Button 2: Disabled unless user holds active reservation
                    Button(
                        onClick = { viewModel.startChargingSession(station) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        enabled = hasActiveReservationForStation,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (hasActiveReservationForStation) "Iniciar Carga" else "Ocupado", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // When station is AVAILABLE: Button 1 = Reserve 15m
                    Button(
                        onClick = {
                            if (station.connectors.isNotEmpty()) {
                                viewModel.reservePlug(station, station.connectors.first())
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        enabled = isAvailable,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reservar (15m)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Button 2: Start Charging Session
                    Button(
                        onClick = { viewModel.startChargingSession(station) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        enabled = isAvailable || hasActiveReservationForStation,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Iniciar Carga", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Button 3: Report Technical Problem
                OutlinedButton(
                    onClick = { isReportDialogOpen = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                }
            }

            // Waiting Queue Info Banner (If station is BUSY or has waiting queue)
            if (isBusy || waitingList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B).copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.People, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Fila de Espera Actual (${waitingList.size} personas)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFFD97706)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        if (waitingList.isEmpty()) {
                            Text(text = "No hay personas en la fila. Toca 'Unirse a Fila' para apartar el próximo lugar.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            waitingList.forEach { userInQueue ->
                                Text(text = "• $userInQueue", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Connectors Section
            Text(
                text = "Tipos de Conectores Disponibles",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            station.connectors.forEach { connector ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EcoGreenPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Power,
                                contentDescription = null,
                                tint = EcoGreenPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = connector.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = connector.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Reviews & Community Feedback Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reseñas de Usuarios (${reviews.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedButton(
                    onClick = { isReviewDialogOpen = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Reseña", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (reviews.isEmpty()) {
                Text(
                    text = "Aún no hay reseñas registradas para esta estación.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                reviews.forEach { review ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = review.userName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(text = "${review.rating}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = review.comment, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = review.dateText, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Coordinates Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Ubicación GPS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Lat: ${station.latitude}, Long: ${station.longitude}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (isReportDialogOpen) {
        ReportProblemDialog(
            stationName = station.name,
            onReportSubmitted = { comment -> viewModel.reportStationProblem(station.id, comment) },
            onDismiss = { isReportDialogOpen = false }
        )
    }

    if (isReviewDialogOpen) {
        AddReviewDialog(
            onReviewSubmitted = { rating, comment ->
                viewModel.addReview(station.id, rating, comment)
            },
            onDismiss = { isReviewDialogOpen = false }
        )
    }
}

@Composable
fun AddReviewDialog(
    onReviewSubmitted: (Double, String) -> Unit,
    onDismiss: () -> Unit
) {
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5.0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Escribir Reseña", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Escribe tu experiencia con el cargador...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onReviewSubmitted(rating, comment)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Publicar Reseña", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DetailInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
