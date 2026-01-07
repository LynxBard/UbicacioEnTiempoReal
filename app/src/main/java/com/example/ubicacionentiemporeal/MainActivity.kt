package com.example.ubicacionentiemporeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.ubicacionentiemporeal.ui.MainScreen

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ubicacionentiemporeal.ui.theme.HistoryScreen
import com.example.ubicacionentiemporeal.ui.MainScreen
import com.example.ubicacionentiemporeal.ui.MainViewModel
import com.example.ubicacionentiemporeal.ui.theme.AppTheme
import com.example.ubicacionentiemporeal.ui.theme.RastreadorESCOMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { }

        requestPermissionLauncher.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.POST_NOTIFICATIONS
        ))

        setContent {
            // Estados globales de la App
            var currentScreen by remember { mutableStateOf("MAIN") }
            var currentTheme by remember { mutableStateOf(AppTheme.IPN) }

            // Usamos el ViewModel aquí para compartirlo entre pantallas
            val mainViewModel: MainViewModel = viewModel()

            RastreadorESCOMTheme(appTheme = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {

                        // Selector de Tema (Pequeña barra superior global o solo visible en Main)
                        if (currentScreen == "MAIN") {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Tema: ", style = MaterialTheme.typography.bodySmall)
                                Switch(
                                    checked = currentTheme == AppTheme.ESCOM,
                                    onCheckedChange = { isEscom ->
                                        currentTheme = if (isEscom) AppTheme.ESCOM else AppTheme.IPN
                                    }
                                )
                                Text(
                                    if (currentTheme == AppTheme.ESCOM) " ESCOM 🔵" else " IPN 🟣",
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // Navegación simple
                        when (currentScreen) {
                            "MAIN" -> MainScreen(
                                viewModel = mainViewModel,
                                onHistoryClick = { currentScreen = "HISTORY" }
                            )
                            "HISTORY" -> HistoryScreen(
                                viewModel = mainViewModel,
                                onBack = { currentScreen = "MAIN" }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel",
                "Rastreo en segundo plano",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}