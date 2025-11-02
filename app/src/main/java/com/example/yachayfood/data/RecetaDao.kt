package com.example.yachayfood.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yachayfood.models.RecetaEntity

@Dao
interface RecetaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarReceta(receta: RecetaEntity)

    @Query("SELECT * FROM recetas ORDER BY fechaGuardado DESC")
    suspend fun obtenerTodasLasRecetas(): List<RecetaEntity>

    @Query("DELETE FROM recetas")
    suspend fun clearAllRecetas()
}