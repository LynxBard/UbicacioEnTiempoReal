package com.example.ubicacionentiemporeal.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**
 * Clase que encapsula la lógica de obtener ubicación con Google Play Services.
 * Maneja los intervalos configurables (Requisito 1).
 */
class LocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) {

    @SuppressLint("MissingPermission") // Los permisos se validan en la UI antes de llamar
    fun getLocationUpdates(intervalo: Long): Flow<Location> {
        return callbackFlow {
            // Validación básica de permisos
            if (!hasLocationPermission(context)) {
                throw LocationException("Faltan permisos de ubicación")
            }

            // Configuración de la petición de ubicación (Intervalos configurables)
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalo)
                .setMinUpdateIntervalMillis(intervalo) // Intervalo mínimo
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        // Enviamos la ubicación al Flow
                        launch { send(location) }
                    }
                }
            }

            // Iniciar actualizaciones
            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            // Limpieza cuando el Flow se cancela (al detener el servicio)
            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun hasLocationPermission(context: Context): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED &&
                context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    class LocationException(message: String): Exception(message)
}