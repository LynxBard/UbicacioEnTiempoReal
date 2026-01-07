package com.example.ubicacionentiemporeal.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.example.ubicacionentiemporeal.R
import com.example.ubicacionentiemporeal.data.LocationDatabase
import com.example.ubicacionentiemporeal.data.LocationEntity

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private var isTracking = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // Inicializamos el cliente de ubicación
        locationClient = LocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                // Obtenemos el intervalo seleccionado (por defecto 10s)
                val interval = intent.getLongExtra(EXTRA_INTERVAL, 10000L)
                startService(interval)
            }
            ACTION_STOP -> stopService()
        }
        return START_STICKY
    }

    private fun startService(interval: Long) {
        if (isTracking) {
            // Si ya está rastreando, podríamos reiniciar para cambiar el intervalo
            // Por simplicidad del examen, asumimos que detienen e inician con nuevo intervalo
            return
        }

        isTracking = true

        // 1. Crear Notificación Persistente (Requisito 1 - Background)
        val notification = NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Rastreo ESCOM Activo")
            .setContentText("Guardando ubicación cada ${interval/1000} segundos...")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de tener un icono o usa el de defecto
            .setOngoing(true)
            .build()

        // 2. Iniciar Foreground Service (Necesario para Android 8+)
        startForeground(1, notification)

        // 3. Comenzar a escuchar ubicaciones
        val locationDao = LocationDatabase.getDatabase(applicationContext).locationDao()

        locationClient.getLocationUpdates(interval)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                // Guardar en Room (Requisito 3)
                val entity = LocationEntity(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timestamp = System.currentTimeMillis(),
                    precision = location.accuracy
                )
                locationDao.insertLocation(entity)

                // Actualizar notificación (Opcional, pero se ve bien)
                val updatedNotification = NotificationCompat.Builder(this, "location_channel")
                    .setContentTitle("Rastreo Activo")
                    .setContentText("Lat: ${location.latitude}, Lon: ${location.longitude}")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setOngoing(true)
                    .build()
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(1, updatedNotification)

            }
            .launchIn(serviceScope)
    }

    private fun stopService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        isTracking = false
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Limpiar corrutinas para evitar memory leaks
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_INTERVAL = "EXTRA_INTERVAL"
    }
}