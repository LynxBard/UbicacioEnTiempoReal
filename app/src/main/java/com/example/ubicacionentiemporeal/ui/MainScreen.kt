package com.example.ubicacionentiemporeal.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class) // Soluciona el error de FilterChip
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onHistoryClick: () -> Unit
) {
    val context = LocalContext.current
    val pathPoints by viewModel.pathPoints.collectAsState()
    val latestLocation = pathPoints.firstOrNull()

    // Verificación manual de permisos (Sin librerías externas)
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    // Estado de la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(19.5045, -99.1469), 15f) // ESCOM Coordinates ;)
    }

    // Efecto para seguir al usuario
    LaunchedEffect(latestLocation) {
        latestLocation?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 18f))
        }
    }

    // UI Principal
    Column(modifier = Modifier.fillMaxSize()) {

        // 1. Tarjeta de Estado
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Estado: ${if (viewModel.isTracking) "Rastreando... 🟢" else "Inactivo 🔴"}",
                    style = MaterialTheme.typography.titleMedium
                )
                if (latestLocation != null) {
                    Text("Lat: ${latestLocation.latitude}")
                    Text("Lng: ${latestLocation.longitude}")
                } else {
                    Text("Esperando primera ubicación...")
                }
            }
        }

        // 2. Mapa
        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission // Muestra el punto azul si hay permiso
                )
            ) {
                // Dibujar la ruta
                if (pathPoints.isNotEmpty()) {
                    Polyline(
                        points = pathPoints,
                        color = Color.Blue,
                        width = 15f
                    )
                }

                // Marcador en la última posición
                latestLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Mi Ubicación"
                    )
                }
            }
        }

        // 3. Controles
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Intervalo de actualización:")

            // Fila de Chips para seleccionar tiempo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IntervalChip("10s", 10000L, viewModel)
                IntervalChip("60s", 60000L, viewModel)
                IntervalChip("5min", 300000L, viewModel)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { viewModel.toggleTracking(context) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isTracking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (viewModel.isTracking) "DETENER" else "INICIAR RASTREO")
                }

                OutlinedButton(onClick = onHistoryClick) {
                    Text("HISTORIAL")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntervalChip(label: String, interval: Long, viewModel: MainViewModel) {
    val selected = viewModel.selectedInterval == interval
    FilterChip(
        selected = selected,
        onClick = { viewModel.setInterval(interval) },
        label = { Text(label) },
        leadingIcon = if (selected) {
            { Icon(imageVector = Icons.Default.Check, contentDescription = null) }
        } else null
    )
}