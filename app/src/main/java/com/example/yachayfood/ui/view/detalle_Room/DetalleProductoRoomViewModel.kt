package com.example.yachayfood.ui.view.detalle_Room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yachayfood.models.basedata.ProductoEntity

class DetalleProductoRoomViewModel(application: Application) : AndroidViewModel(application) {

    private val _producto = MutableLiveData<ProductoEntity?>()
    val producto: LiveData<ProductoEntity?> = _producto

    fun setProducto(producto: ProductoEntity?) {
        _producto.value = producto
    }
}
