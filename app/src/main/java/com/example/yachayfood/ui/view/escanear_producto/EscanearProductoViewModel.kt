package com.example.yachayfood.ui.view.escanear_producto

import androidx.lifecycle.*
import com.example.yachayfood.data.AppDatabase
import com.example.yachayfood.models.ProductoEntity
import com.example.yachayfood.repository.ProductoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EscanearProductoViewModel(private val database: AppDatabase) : ViewModel() {

    private val repository = ProductoRepository(database)

    private val _producto = MutableLiveData<ProductoEntity?>()
    val producto: LiveData<ProductoEntity?> = _producto

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    fun buscarProductoPorCodigo(codigo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val productoEntity = repository.buscarProductoPorCodigo(codigo)

                withContext(Dispatchers.Main) {
                    if (productoEntity != null) {
                        _mensaje.value = "Producto cargado correctamente"
                        _producto.value = productoEntity
                    } else {
                        _mensaje.value = "No se encontró el producto"
                        _producto.value = null
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mensaje.value = "Error: ${e.message}"
                    _producto.value = null
                }
            }
        }
    }

}
