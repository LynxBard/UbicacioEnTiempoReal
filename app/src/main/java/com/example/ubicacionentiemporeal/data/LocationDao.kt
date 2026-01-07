package com.example.ubicacionentiemporeal.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    // Insertar nueva ubicación
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    // Obtener historial completo.
    // Usamos Flow para que la UI se actualice automáticamente cuando llegue un dato nuevo (Reactividad).
    @Query("SELECT * FROM locations ORDER BY timestamp DESC")
    fun getAllLocations(): Flow<List<LocationEntity>>

    // Opción para limpiar el historial (Requisito 5)
    @Query("DELETE FROM locations")
    suspend fun deleteAll()
}