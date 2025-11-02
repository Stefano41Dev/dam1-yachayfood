package com.example.yachayfood.ui.view.acerca_de

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.yachayfood.data.database.AppDatabase
import kotlinx.coroutines.launch

class AcercaDeViewModel(application: Application) : AndroidViewModel(application) {

    private val productoDao = AppDatabase.getInstance(application).productoDao()
    private val recetaDao = AppDatabase.getInstance(application).recetaDao()

    private val _borradoCompleto = MutableLiveData<Boolean>(false)
    val borradoCompleto: LiveData<Boolean> get() = _borradoCompleto

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun borrarTodosLosDatos() {
        viewModelScope.launch {
            try {
                productoDao.clearAllProductos()
                recetaDao.clearAllRecetas()
                _borradoCompleto.postValue(true)
            } catch (e: Exception) {
                _error.postValue("Error al borrar los datos: ${e.message}")
            }
        }
    }

}