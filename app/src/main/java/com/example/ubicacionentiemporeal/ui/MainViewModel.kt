package com.example.ubicacionentiemporeal.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.example.ubicacionentiemporeal.data.LocationDatabase
import com.example.ubicacionentiemporeal.service.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = LocationDatabase.getDatabase(application).locationDao()

    // Observamos la BD y transformamos los datos para el Mapa
    // StateFlow convierte el flujo de datos en un estado que Compose puede leer
    val locations = dao.getAllLocations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Transformamos las entidades a LatLng para el Polyline del mapa
    val pathPoints = locations.map { list ->
        list.map { LatLng(it.latitude, it.longitude) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado local de la UI
    var isTracking = false
        private set

    var selectedInterval = 10000L // 10 segundos por defecto
        private set

    fun setInterval(interval: Long) {
        selectedInterval = interval
    }

    fun toggleTracking(context: android.content.Context) {
        val intent = Intent(context, LocationService::class.java)
        if (isTracking) {
            // Detener
            intent.action = LocationService.ACTION_STOP
            context.startService(intent)
            isTracking = false
        } else {
            // Iniciar
            intent.action = LocationService.ACTION_START
            intent.putExtra(LocationService.EXTRA_INTERVAL, selectedInterval)
            context.startService(intent) // En Android 8+ el servicio llamará a startForeground internamente
            isTracking = true
        }
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteAll()
        }
    }
}