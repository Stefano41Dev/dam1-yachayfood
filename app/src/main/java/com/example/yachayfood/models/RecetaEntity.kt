package com.example.yachayfood.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "recetas")
@TypeConverters(RecetaConverters::class)
data class RecetaEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val descripcionCorta: String,
    val clasificacionReceta: String, // "Nutritiva", "Aceptable", "No Recomendado"
    val clasificacionDescripcion: String,
    val ingredientes: List<IngredienteReceta>,
    val pasos: List<String>,
    val caloriasAproximadas: String,
    val macros: String,
    val fechaGuardado: Long = System.currentTimeMillis()

): Parcelable

@Parcelize
data class IngredienteReceta(
    val nombre: String,
    val tipo: String // "Producto Escaneado" o "Sugerencia IA"
): Parcelable

// Convertidores para que Room pueda guardar las Listas
class RecetaConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromIngredienteList(value: List<IngredienteReceta>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toIngredienteList(value: String): List<IngredienteReceta> {
        val listType = object : TypeToken<List<IngredienteReceta>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}