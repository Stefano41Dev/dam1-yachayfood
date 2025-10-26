package com.example.yachayfood.repository

import com.example.yachayfood.api.ApiClient
import com.example.yachayfood.api.OpenFoodFactsApi
import com.example.yachayfood.models.Nutriente
import com.example.yachayfood.models.basedata.NutrimentsEntity
import com.example.yachayfood.models.basedata.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductoRepository {
    private val api = ApiClient.retrofit.create(OpenFoodFactsApi::class.java)

    suspend fun buscarProductosApi(codigo: String): List<Open> = withContext(Dispatchers.IO) {
        val response = api.getProductByCode(codigo)
        if (!response.isSuccessful) return@withContext emptyList()

        val productData = response.body()?.product ?: return@withContext emptyList()

        val nutriments = productData.nutriments
        val nutrientes = NutrimentsEntity(
            carbohydrates_100g = nutriments?.carbohydrates_100g ?: 0.0,
            energy_kcal_100g = nutriments?.energy_kcal_100g ?: 0.0,
            fat_100g = nutriments?.fat_100g ?: 0.0,
            saturated_fat_100g = nutriments?.saturated_fat_100g ?: 0.0,
            //carbohydrates_100g = nutriments?.carbohydrates_100g ?: 0.0,
            sugars_100g = nutriments?.sugars_100g ?: 0.0,
            fiber_100g = nutriments?.fiber_100g ?: 0.0,
            proteins_100g = nutriments?.proteins_100g ?: 0.0,
            energy_100g = nutriments?.energy_100g
        )

        listOf(
            ProductoEntity(
                codigo = codigo,
                nombre = productData.product_name ?: "Desconocido",
                descripcion = productData.generic_name ?: "",
                clasificacion = "A",
                categorias = productData.categories_tags?.joinToString(",") ?: "",
                cantidad = productData.quantity?.filter { it.isDigit() }?.toDoubleOrNull() ?: 0.0,
                empaque = productData.packaging ?: "",
                paises = productData.countries ?: "",
                ingredientes = productData.ingredients_text?.split(",")?.map { it.trim() }?.toMutableList() ?: mutableListOf(),
                imagenUrl = productData.image_url ?: "",
                marca = productData.brands ?: "",
                //pais = productData.countries_tags?.firstOrNull() ?: "",
                nutriments = nutrientes
            )
        )
    }

    suspend fun obtenerProductoPorCodigo(codigo: String): Producto? = withContext(Dispatchers.IO) {
        val response = api.getProductByCode(codigo)
        if (!response.isSuccessful) return@withContext null

        val productData = response.body()?.product ?: return@withContext null
        val nutriments = productData.nutriments

        val nutrientes = Nutriente(
            carbohidratos = nutriments?.carbohydrates_100g ?: 0.0,
            energia = nutriments?.energy_kcal_100g ?: 0.0,
            grasas = nutriments?.fat_100g ?: 0.0,
            grasasSaturadas = nutriments?.saturated_fat_100g ?: 0.0,
            hidratosCarbono = nutriments?.carbohydrates_100g ?: 0.0,
            azucares = nutriments?.sugars_100g ?: 0.0,
            fibrasAlimentarias = nutriments?.fiber_100g ?: 0.0,
            proteinas = nutriments?.proteins_100g ?: 0.0
        )

        return@withContext Producto(
            codigoProducto = codigo,
            nombreProducto = productData.product_name ?: "Desconocido",
            descripcion = productData.generic_name ?: "",
            clasificacion = "A",
            categorias = productData.categories_tags?.toMutableList() ?: mutableListOf(),
            cantidad = productData.quantity?.filter { it.isDigit() }?.toDoubleOrNull() ?: 0.0,
            empaquetado = productData.packaging ?: "",
            paises = productData.countries ?: "",
            ingredientes = productData.ingredients_text?.split(",")?.map { it.trim() }?.toMutableList() ?: mutableListOf(),
            imagenUrl = productData.image_url ?: "",
            marcas = productData.brands ?: "",
            pais = productData.countries_tags?.firstOrNull() ?: "",
            nutrientes = nutrientes
        )
    }

}