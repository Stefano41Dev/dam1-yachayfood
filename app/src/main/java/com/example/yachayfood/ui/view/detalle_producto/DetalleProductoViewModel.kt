package com.example.yachayfood.ui.view.detalle_producto

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yachayfood.data.AppDatabase
import com.example.yachayfood.models.ProductoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleProductoViewModel(application: Application) : AndroidViewModel(application) {

    private val productoDao = AppDatabase.getInstance(application).productoDao()

    private val _producto = MutableLiveData<ProductoEntity?>()
    val producto: LiveData<ProductoEntity?> = _producto

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    fun cargarProducto(producto: ProductoEntity?) {
        if (producto != null) {
            _producto.value = producto
        }
    }

    fun cargarProductoPorCodigo(codigo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val productoEntity = productoDao.obtenerProductoPorCodigo(codigo)
            withContext(Dispatchers.Main) {
                if (productoEntity != null) {
                    _producto.value = productoEntity
                } else {
                    _mensaje.value = "No se encontró el producto en base local"
                }
            }
        }
    }

}
