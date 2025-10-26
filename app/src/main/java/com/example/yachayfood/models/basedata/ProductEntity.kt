package com.example.yachayfood.models.basedata

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
/*import com.example.yachayfood.models.Nutriente
import com.example.yachayfood.models.Producto*/
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
    val nutriments: NutrimentsEntity?
) : Parcelable

/*fun ProductoEntity.toProducto(): Producto {
    return Producto(
        codigoProducto = this.codigo,
        nombreProducto = this.nombre ?: "",
        descripcion = this.marca ?: "",
        clasificacion = this.nutriscoreScore?.toString() ?: "N/A",
        categorias = this.categorias?.split(",")?.toMutableList() ?: mutableListOf(),
        cantidad = this.cantidad,
        empaquetado = this.empaque ?: "",
        paises = this.paises ?: "",
        ingredientes = this.ingredientes?.split(",")?.toMutableList() ?: mutableListOf(),
        imagenUrl = this.imagenUrl ?: "",
        marcas = this.marca ?: "",
        pais = this.paises ?: "",
        nutrientes = Nutriente(
            energia = this.nutriments?.energy_kcal_100g ?: 0.0,
            grasas = this.nutriments?.fat_100g ?: 0.0,
            grasasSaturadas = this.nutriments?.saturated_fat_100g ?: 0.0,
            azucares = this.nutriments?.sugars_100g ?: 0.0,
            proteinas = this.nutriments?.proteins_100g ?: 0.0,
            carbohidratos = this.nutriments?.carbohydrates_100g ?: 0.0,
            hidratosCarbono = this.nutriments?.carbohydrates_100g ?: 0.0,
            fibrasAlimentarias = this.nutriments?.fiber_100g ?: 0.0
        )
    )
}*/

