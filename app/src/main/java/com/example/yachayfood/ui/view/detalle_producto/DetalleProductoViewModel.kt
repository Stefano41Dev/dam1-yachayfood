package com.example.yachayfood.ui.view.detalle_producto

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yachayfood.data.database.AppDatabase
import com.example.yachayfood.models.Producto
import com.example.yachayfood.models.toProducto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleProductoViewModel(application: Application) : AndroidViewModel(application) {

    private val productoDao = AppDatabase.getInstance(application).productoDao()

    private val _producto = MutableLiveData<Producto?>()
    val producto: LiveData<Producto?> = _producto

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    fun cargarProducto(producto: Producto?) {
        if (producto != null) {
            _producto.value = producto
        }
    }

    fun cargarProductoPorCodigo(codigo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val productoEntity = productoDao.obtenerProductoPorCodigo(codigo)
            withContext(Dispatchers.Main) {
                if (productoEntity != null) {
                    _producto.value = productoEntity.toProducto()
                } else {
                    _mensaje.value = "No se encontró el producto en base local"
                }
            }
        }
    }

}
