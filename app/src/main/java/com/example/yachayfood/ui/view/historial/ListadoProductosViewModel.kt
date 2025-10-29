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

    // LiveData para exponer la lista de productos a la vista
    private val _listaDeProductos = MutableLiveData<List<ProductoEntity>>()
    val listaDeProductos: LiveData<List<ProductoEntity>> get() = _listaDeProductos

    // LiveData para manejar errores
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun obtenerTodosLosProductos() {
        viewModelScope.launch {
            try {
                // Obtenemos todos los productos desde el DAO
                val productos = productoDao.getAllProductos() // Asume que tienes este método en tu DAO
                _listaDeProductos.postValue(productos)
            } catch (e: Exception) {
                _error.postValue("Error al cargar el historial: ${e.message}")
            }
        }
    }
}