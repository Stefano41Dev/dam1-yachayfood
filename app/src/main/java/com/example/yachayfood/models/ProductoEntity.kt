package com.example.yachayfood.models
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String,
    val nombre: String?,
    val descripcion: String?,
    val clasificacion: String?,
    val marca: String?,
    val paises: String?,
    val empaque: String?,
    val cantidad: Double,
    val imagenUrl: String?,
    val ingredientes: String?,
    val categorias: String?,
    val nutriscoreScore: Int?,
    val fechaEscaneo: Long = System.currentTimeMillis(),

    @Embedded(prefix = "nutriments_")
    val nutriments: NutrimentsEntity,

    // --- Campos añadidos para la IA ---
    val analisisYachay: String? = null,
    val clasificacionYachay: String? = null,
    val octogonoGrasasSaturadas: String? = null, // "si" o "no"
    val octogonoAzucar: String? = null, // "si" o "no"
    val octogonoSodio: String? = null, // "si" o "no"
    val octogonoGrasasTrans: String? = null // "si" o "no"
) : Parcelable


