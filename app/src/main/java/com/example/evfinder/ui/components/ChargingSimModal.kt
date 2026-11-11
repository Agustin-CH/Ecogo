package com.example.evfinder.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.evfinder.model.ChargingSessionState
import com.example.evfinder.ui.theme.EcoGreenPrimary

@Composable
fun ChargingSimModal(
    session: ChargingSessionState,
    onStopSession: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressAnimated by animateFloatAsState(
        targetValue = session.currentBatteryPercent / 100f,
        label = "BatteryProgress"
    )

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EcoGreenPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.EvStation, contentDescription = null, tint = EcoGreenPrimary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (session.isCompleted) "Carga Finalizada" else "Sesión de Carga en Vivo",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = session.stationName,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Battery Circular/Linear Progress Box
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (session.isCompleted) Icons.Default.CheckCircle else Icons.Default.Bolt,
                                contentDescription = null,
                                tint = EcoGreenPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${session.currentBatteryPercent}%",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progressAnimated },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = EcoGreenPrimary,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Meta: ${session.targetBatteryPercent}% de batería",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Live Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBox(
                        title = "Energía",
                        value = String.format("%.2f kWh", session.energyKwhDelivered)
                    )
                    StatBox(
                        title = "Potencia",
                        value = "${session.chargingPowerKw} kW"
                    )
                    StatBox(
                        title = "Costo Actual",
                        value = "$${session.currentCost.toInt()} ARS"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (session.isCharging && !session.isCompleted) {
                    Button(
                        onClick = onStopSession,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Detener y Pagar", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onClose,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Aceptar / Ver Recibo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
