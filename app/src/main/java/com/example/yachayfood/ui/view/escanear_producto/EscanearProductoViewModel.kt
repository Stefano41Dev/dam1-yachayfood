package com.example.yachayfood.ui.view.escanear_producto

import androidx.lifecycle.*
import com.example.yachayfood.data.AppDatabase
import com.example.yachayfood.models.basedata.NutrimentsEntity
import com.example.yachayfood.models.basedata.ProductoEntity
import com.example.yachayfood.models.Producto
import com.example.yachayfood.models.Nutriente
import com.example.yachayfood.models.basedata.toProducto
import com.example.yachayfood.api.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.round

class EscanearProductoViewModel(private val database: AppDatabase) : ViewModel() {

    private val productoDao = database.productoDao()

    private val _producto = MutableLiveData<Producto?>()
    val producto: LiveData<Producto?> = _producto

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    fun buscarProductoPorCodigo(codigo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val productoLocal = productoDao.obtenerProductoPorCodigo(codigo)

                if (productoLocal != null) {
                    productoDao.actualizarFechaEscaneo(codigo, System.currentTimeMillis())
                    withContext(Dispatchers.Main) {
                        _mensaje.value = "Producto cargado desde base local"
                        _producto.value = productoLocal.toProducto()
                    }
                    return@launch
                }

                val response = ApiClient.instance.getProductByCode(codigo)
                if (response.isSuccessful && response.body()?.status == 1) {
                    val productData = response.body()!!.product

                    val producto = Producto(
                        codigoProducto = codigo,
                        nombreProducto = productData?.product_name ?: "Producto sin nombre",
                        descripcion = productData?.generic_name ?: "Sin descripción",
                        clasificacion = productData?.nutriscore_score?.let { score ->
                            when {
                                score <= 2 -> "A"
                                score <= 5 -> "B"
                                score <= 8 -> "C"
                                score <= 11 -> "D"
                                else -> "E"
                            }
                        } ?: "B",
                        categorias = productData?.categories_tags?.map { it.removePrefix("en:") }?.toMutableList()
                            ?: mutableListOf("Desconocido"),
                        cantidad = productData?.quantity?.replace("[^0-9.]".toRegex(), "")?.toDoubleOrNull() ?: 0.0,
                        empaquetado = productData?.packaging ?: "No especificado",
                        paises = productData?.countries ?: "Desconocido",
                        ingredientes = productData?.ingredients_text?.split(",")?.map { it.trim() }?.toMutableList()
                            ?: mutableListOf("No disponible"),
                        imagenUrl = productData?.image_url ?: "",
                        marcas = productData?.brands ?: "Desconocido",
                        pais = productData?.countries ?: "Desconocido",
                        nutrientes = Nutriente(
                            energia = round(productData?.nutriments?.energy_kcal_100g
                                ?: productData?.nutriments?.energy_100g?.div(4.184) ?: 0.0).toDouble(),
                            grasas = round(productData?.nutriments?.fat_100g ?: 0.0).toDouble(),
                            grasasSaturadas = round(productData?.nutriments?.saturated_fat_100g ?: 0.0).toDouble(),
                            azucares = round(productData?.nutriments?.sugars_100g ?: 0.0).toDouble(),
                            proteinas = round(productData?.nutriments?.proteins_100g ?: 0.0).toDouble(),
                            carbohidratos = round(productData?.nutriments?.carbohydrates_100g ?: 0.0).toDouble(),
                            hidratosCarbono = round(productData?.nutriments?.carbohydrates_100g ?: 0.0).toDouble(),
                            fibrasAlimentarias = round(productData?.nutriments?.fiber_100g ?: 0.0).toDouble()
                        )
                    )

                    val productoEntity = ProductoEntity(
                        codigo = producto.codigoProducto,
                        nombre = producto.nombreProducto,
                        marca = producto.marcas,
                        paises = producto.paises,
                        empaque = producto.empaquetado,
                        cantidad = producto.cantidad,
                        imagenUrl = producto.imagenUrl,
                        ingredientes = producto.ingredientes.joinToString(","),
                        categorias = producto.categorias.joinToString(","),
                        nutriscoreScore = productData?.nutriscore_score,
                        fechaEscaneo = System.currentTimeMillis(),
                        nutriments = productData?.nutriments?.let { nutr ->
                            NutrimentsEntity(
                                energy_kcal_100g = nutr.energy_kcal_100g,
                                energy_100g = nutr.energy_100g,
                                fat_100g = nutr.fat_100g,
                                saturated_fat_100g = nutr.saturated_fat_100g,
                                sugars_100g = nutr.sugars_100g,
                                proteins_100g = nutr.proteins_100g,
                                carbohydrates_100g = nutr.carbohydrates_100g,
                                fiber_100g = nutr.fiber_100g
                            )
                        }
                    )

                    productoDao.insertarProducto(productoEntity)

                    withContext(Dispatchers.Main) {
                        _mensaje.value = "Producto cargado desde la API"
                        _producto.value = producto
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _mensaje.value = "No se encontró el producto"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mensaje.value = "Error: ${e.message}"
                }
            }
        }
    }
}
