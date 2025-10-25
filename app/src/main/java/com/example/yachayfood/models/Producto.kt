package com.example.yachayfood.models

import android.os.Parcelable
import com.example.yachayfood.data.local.ProductoEntity
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Producto(
    val codigoProducto: String,
    val nombreProducto: String,
    val descripcion: String,
    val clasificacion: String,
    val categorias: MutableList<String>,
    val cantidad: Double,
    val empaquetado: String,
    val paises: String,
    val ingredientes: MutableList<String>,
    val imagenUrl: String,
    val marcas: String,
    val pais: String,
    val nutrientes: Nutriente
) : Parcelable

fun Producto.toProductoEntity(): ProductoEntity {
    return ProductoEntity(
        codigo = this.codigoProducto,
        nombre = this.nombreProducto,
        marca = this.marcas,
        paises = this.pais,
        empaque = this.empaquetado,
        cantidad = this.cantidad,
        imagenUrl = this.imagenUrl,
        ingredientes = this.ingredientes.joinToString(","),
        categorias = this.categorias.joinToString(","),
        nutriscoreScore = this.nutrientes.energia.toInt(), // ejemplo
        nutriments = com.example.yachayfood.data.local.NutrimentsEntity(
            energy_kcal_100g = this.nutrientes.energia,
            energy_100g = this.nutrientes.energia,
            fat_100g = this.nutrientes.grasas,
            saturated_fat_100g = this.nutrientes.grasasSaturadas,
            sugars_100g = this.nutrientes.azucares,
            proteins_100g = this.nutrientes.proteinas,
            carbohydrates_100g = this.nutrientes.carbohidratos,
            fiber_100g = this.nutrientes.fibrasAlimentarias
        )
    )
}
