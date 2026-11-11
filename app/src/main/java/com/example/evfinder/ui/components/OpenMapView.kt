package com.example.evfinder.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.evfinder.model.ChargingStation
import com.example.evfinder.model.StationStatus
import com.example.evfinder.ui.theme.EcoGreenPrimary
import com.example.evfinder.ui.theme.StatusAvailable
import com.example.evfinder.ui.theme.StatusBusy
import com.example.evfinder.ui.theme.StatusOutOfService
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OpenMapView(
    stations: List<ChargingStation>,
    selectedStation: ChargingStation?,
    onStationSelected: (ChargingStation) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapView = remember {
        // Fix for OSM 403 Access Blocked: Set a compliant user-agent string
        Configuration.getInstance().userAgentValue = "Mozilla/5.0 (Android 14; Mobile; rv:122.0) Gecko/122.0 Firefox/122.0"
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid_config", android.content.Context.MODE_PRIVATE))

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            isTilesScaledToDpi = true
            setTilesScaledToDpi(true)
            zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(13.0)
            controller.setCenter(GeoPoint(-34.6083, -58.3672)) // Buenos Aires center
        }
    }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
        }
    }

    // Animate to selected station location
    LaunchedEffect(selectedStation) {
        selectedStation?.let {
            mapView.controller.animateTo(GeoPoint(it.latitude, it.longitude), 14.5, 700L)
        }
    }

    // Update map markers whenever stations or selection changes
    LaunchedEffect(stations, selectedStation) {
        mapView.overlays.clear()

        stations.forEach { station ->
            val isSelected = selectedStation?.id == station.id
            val pinColor = when (station.status) {
                StationStatus.AVAILABLE -> StatusAvailable
                StationStatus.BUSY -> StatusBusy
                StationStatus.OUT_OF_SERVICE -> StatusOutOfService
            }

            val marker = Marker(mapView).apply {
                position = GeoPoint(station.latitude, station.longitude)
                title = station.name
                snippet = "${station.powerKw} kW - ${station.status.label}"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = createCustomMarkerBitmapDrawable(
                    context = context,
                    station = station,
                    pinColor = pinColor,
                    isSelected = isSelected
                )
                setOnMarkerClickListener { _, _ ->
                    onStationSelected(station)
                    true
                }
            }
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun createCustomMarkerBitmapDrawable(
    context: android.content.Context,
    station: ChargingStation,
    pinColor: androidx.compose.ui.graphics.Color,
    isSelected: Boolean
): BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val widthPx = (if (isSelected) 56 * density else 46 * density).toInt()
    val heightPx = (if (isSelected) 68 * density else 56 * density).toInt()

    val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Draw Power kW badge background
    paint.color = if (isSelected) EcoGreenPrimary.toArgb() else android.graphics.Color.WHITE
    paint.style = Paint.Style.FILL
    val badgeRect = RectF(
        widthPx * 0.1f,
        0f,
        widthPx * 0.9f,
        heightPx * 0.32f
    )
    canvas.drawRoundRect(badgeRect, 10f, 10f, paint)

    // Draw Power kW text
    paint.color = if (isSelected) android.graphics.Color.WHITE else android.graphics.Color.BLACK
    paint.textSize = 10f * density
    paint.isFakeBoldText = true
    paint.textAlign = Paint.Align.CENTER
    canvas.drawText("${station.powerKw}kW", widthPx / 2f, heightPx * 0.22f, paint)

    // Draw Main Circle Pin
    val circleCenterX = widthPx / 2f
    val circleCenterY = heightPx * 0.65f
    val circleRadius = (if (isSelected) 18 * density else 14 * density) / 2f

    // Pin outer ring
    paint.color = pinColor.toArgb()
    canvas.drawCircle(circleCenterX, circleCenterY, circleRadius + (2 * density), paint)

    // Pin inner circle
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, paint)

    // Center dot indicator
    paint.color = pinColor.toArgb()
    canvas.drawCircle(circleCenterX, circleCenterY, circleRadius * 0.5f, paint)

    return BitmapDrawable(context.resources, bitmap)
}
