package com.example.yachayfood.ui.view.recetas_ia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.yachayfood.data.database.AppDatabase
import com.example.yachayfood.models.ProductoEntity
import kotlinx.coroutines.launch

class RecetasIAViewModel(application: Application): AndroidViewModel(application) {

    private val productoDao = AppDatabase.getInstance(application).productoDao()

    private var listaCompleta = listOf<ProductoEntity>()

    private val _listaFiltrada = MutableLiveData<List<ProductoEntity>>()
    val listaFiltrada: LiveData<List<ProductoEntity>> get() = _listaFiltrada

    private val _productosSeleccionados = MutableLiveData<Set<ProductoEntity>>(emptySet())
    val productosSeleccionados: LiveData<Set<ProductoEntity>> get() = _productosSeleccionados

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun cargarProductos() {
        viewModelScope.launch {
            try {
                listaCompleta = productoDao.getAllProductos()
                _listaFiltrada.postValue(listaCompleta)
            } catch (e: Exception) {
                _error.postValue("Error al cargar productos: ${e.message}")
            }
        }
    }

    fun buscarProducto(query: String) {
        val texto = query.trim()
        if (texto.isEmpty()) {
            _listaFiltrada.postValue(listaCompleta)
        } else {
            val filtrados = listaCompleta.filter {
                (it.nombre ?: "").contains(texto, ignoreCase = true) ||
                        (it.marca ?: "").contains(texto, ignoreCase = true)
            }
            _listaFiltrada.postValue(filtrados)
        }
    }

    fun onProductoSeleccionado(producto: ProductoEntity, isChecked: Boolean) {
        val setActual = _productosSeleccionados.value?.toMutableSet() ?: mutableSetOf()
        if (isChecked) {
            setActual.add(producto)
        } else {
            setActual.remove(producto)
        }
        _productosSeleccionados.postValue(setActual)
    }

}