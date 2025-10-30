package com.example.yachayfood.ui.view.historial

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.yachayfood.data.AppDatabase
import com.example.yachayfood.models.ProductoEntity
import kotlinx.coroutines.launch

class ListadoProductosViewModel(application: Application) : AndroidViewModel(application) {

    private val productoDao = AppDatabase.getInstance(application).productoDao()

    private val _listaDeProductos = MutableLiveData<List<ProductoEntity>>()
    val listaDeProductos: LiveData<List<ProductoEntity>> get() = _listaDeProductos

    private var categoriasSeleccionadas = mutableSetOf<String>()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private var listaCompleta = listOf<ProductoEntity>()

    fun obtenerTodosLosProductos() {
        viewModelScope.launch {
            try {
                val productos = productoDao.getAllProductos()
                listaCompleta = productos
                _listaDeProductos.postValue(productos)
            } catch (e: Exception) {
                _error.postValue("Error al cargar productos: ${e.message}")
            }
        }
    }

    fun filtrarPorClasificacion(clasificacion: String) {
        viewModelScope.launch {
            try {
                if (categoriasSeleccionadas.contains(clasificacion)) {
                    categoriasSeleccionadas.remove(clasificacion)
                } else {
                    categoriasSeleccionadas.add(clasificacion)
                }
                aplicarFiltros()
            } catch (e: Exception) {
                _error.postValue("Error al filtrar productos: ${e.message}")
            }
        }
    }

    fun buscarProductos(query: String) {
        viewModelScope.launch {
            try {
                aplicarFiltros(query)
            } catch (e: Exception) {
                _error.postValue("Error al filtrar productos: ${e.message}")
            }
        }
    }
    private fun aplicarFiltros(query: String = "") {
        val texto = query.trim()

        val filtrados = listaCompleta.filter { producto ->

            val coincideTexto = if (texto.isEmpty()) true
            else (producto.nombre ?: "").contains(texto, ignoreCase = true)


            val textoCategoriaProducto = when (producto.clasificacionYachay) {
                "AD" -> "Natural y Recomendado"
                "A" -> "Saludable"
                "B" -> "Aceptable"
                "C" -> "Consumo Moderado"
                "D" -> "No Recomendado"
                else -> "No Clasificado"
            }

            val coincideCategoria = if (categoriasSeleccionadas.isEmpty()) true
            else categoriasSeleccionadas.contains(textoCategoriaProducto)

            coincideTexto && coincideCategoria
        }

        _listaDeProductos.postValue(filtrados)
    }

}