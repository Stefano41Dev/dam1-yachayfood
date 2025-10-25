package com.example.yachayfood.ui.view.pantalla_principal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yachayfood.data.database.AppDatabase
import com.example.yachayfood.models.ProductoEntity
import com.example.yachayfood.repository.ProductoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PantallaPrincipalViewModel(
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val productoDao = appDatabase.productoDao()
    private val repository = ProductoRepository()

    private val _productosRecientes = MutableLiveData<List<ProductoEntity>>()
    val productosRecientes: LiveData<List<ProductoEntity>> get() = _productosRecientes

    fun cargarProductosRecientes() {
        viewModelScope.launch(Dispatchers.IO) {
            val recientes = productoDao.obtenerUltimosTres()
            withContext(Dispatchers.Main) {
                _productosRecientes.value = recientes
            }
        }
    }
}