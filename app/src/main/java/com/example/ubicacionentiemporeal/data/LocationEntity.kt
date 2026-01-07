package com.example.ubicacionentiemporeal.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa la tabla 'locations' en la base de datos.
 * Cumple con el Requisito 3: Guardar latitud, longitud, timestamp y precisión.
 */
@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val latitude: Double,
    val longitude: Double,
    val timestamp: Long, // Guardaremos el tiempo en milisegundos
    val precision: Float // Precisión en metros
)