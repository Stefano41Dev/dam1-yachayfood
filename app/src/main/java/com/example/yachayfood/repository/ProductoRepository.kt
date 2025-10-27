package com.example.yachayfood.repository

import com.example.yachayfood.api.ApiClient
import com.example.yachayfood.data.AppDatabase
import com.example.yachayfood.models.NutrimentsEntity
import com.example.yachayfood.models.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.round
class ProductoRepository(database: AppDatabase) {

    private val productoDao = database.productoDao()
    private val api = ApiClient.instance

    suspend fun buscarProductoPorCodigo(codigo: String): ProductoEntity? = withContext(Dispatchers.IO) {

        val productoLocal = productoDao.obtenerProductoPorCodigo(codigo)
        if (productoLocal != null) {
            productoDao.actualizarFechaEscaneo(codigo, System.currentTimeMillis())
            return@withContext productoLocal
        }


        val response = api.getProductByCode(codigo)
        val body = response.body()

        if (response.isSuccessful && body?.status == 1 && body.product != null) {
            val p = body.product

            val productoEntity = ProductoEntity(
                codigo = codigo,
                nombre = p.product_name ?: "Producto sin nombre",
                descripcion = p.generic_name ?: "Sin descripción",
                clasificacion = p.nutriscore_grade?: "Desconocido",
                /*clasificacion = p.nutriscore_score?.let { score ->
                    when {
                        score <= 2 -> "A"
                        score <= 5 -> "B"
                        score <= 8 -> "C"
                        score <= 11 -> "D"
                        else -> "E"
                    }
                } ?: "B",*/
                marca = p.brands ?: "No especificado",
                paises = p.countries ?: "No especificado",
                empaque = p.packaging ?: "No especificado",
                cantidad = p.quantity?.replace("[^0-9.]".toRegex(), "")?.toDoubleOrNull() ?: 0.0,
                imagenUrl = p.image_url ?: "Imagen no disponible",
                ingredientes = p.ingredients_text ?: "No especificado",
                categorias = p.categories?: "No especificado",
                //categorias = p.categories_tags?.joinToString(",") { it.removePrefix("en:") } ?: "Desconocido",
                nutriscoreScore = p.nutriscore_score ?: 0,
                fechaEscaneo = System.currentTimeMillis(),
                nutriments = NutrimentsEntity(
                    energy_kcal_100g = round(p.nutriments?.energy_kcal_100g ?: 0.0),
                    energy_100g = round(p.nutriments?.energy_100g ?: 0.0),
                    fat_100g = round(p.nutriments?.fat_100g ?: 0.0),
                    saturated_fat_100g = round(p.nutriments?.saturated_fat_100g ?: 0.0),
                    sugars_100g = round(p.nutriments?.sugars_100g ?: 0.0),
                    proteins_100g = round(p.nutriments?.proteins_100g ?: 0.0),
                    carbohydrates_100g = round(p.nutriments?.carbohydrates_100g ?: 0.0),
                    fiber_100g = round(p.nutriments?.fiber_100g ?: 0.0)
                )
            )

            productoDao.insertarProducto(productoEntity)
            return@withContext productoEntity
        }

        return@withContext null
    }


    /*suspend fun obtenerProductoPorCodigo(codigo: String): Producto? = withContext(Dispatchers.IO) {
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
    }*/

}