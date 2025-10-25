package com.example.yachayfood.ui.view.escanear_producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yachayfood.repository.ProductoRepository
import com.example.yachayfood.models.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoViewModel : ViewModel() {

    private val repository = ProductoRepository()

    private val _producto = MutableStateFlow<Producto?>(null)
    val producto: StateFlow<Producto?> get() = _producto

    fun buscarProductoPorCodigo(codigo: String) {
        viewModelScope.launch {
            val resultado = repository.obtenerProductoPorCodigo(codigo)
            _producto.value = resultado
        }
    }
}