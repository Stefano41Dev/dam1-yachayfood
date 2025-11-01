package com.example.yachayfood.ui.view.recetas_guardadas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.yachayfood.data.database.AppDatabase
import com.example.yachayfood.models.RecetaEntity
import com.example.yachayfood.repository.RecetaRepository
import kotlinx.coroutines.launch

class ListadoRecetasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecetaRepository
    private var listaCompleta = listOf<RecetaEntity>()

    private val _listaDeRecetas = MutableLiveData<List<RecetaEntity>>()
    val listaDeRecetas: LiveData<List<RecetaEntity>> get() = _listaDeRecetas

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Filtros
    private var queryActual: String = ""
    private var clasificacionSeleccionada: String = "Todos"

    init {
        val recetaDao = AppDatabase.getInstance(application).recetaDao()
        repository = RecetaRepository(recetaDao)
    }

    fun cargarRecetas() {
        viewModelScope.launch {
            try {
                listaCompleta = repository.obtenerRecetasGuardadas()
                aplicarFiltros()
            } catch (e: Exception) {
                _error.postValue("Error al cargar recetas: ${e.message}")
            }
        }
    }

    fun buscarRecetas(query: String) {
        queryActual = query
        aplicarFiltros()
    }

    fun filtrarPorClasificacion(clasificacion: String) {
        clasificacionSeleccionada = clasificacion
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val texto = queryActual.trim()

        val filtrados = listaCompleta.filter { receta ->
            // Filtro de texto
            val coincideTexto = if (texto.isEmpty()) true
            else (receta.nombre).contains(texto, ignoreCase = true)

            // Filtro de categoría
            val coincideClasificacion = if (clasificacionSeleccionada == "Todos") true
            else receta.clasificacionReceta == clasificacionSeleccionada

            coincideTexto && coincideClasificacion
        }
        _listaDeRecetas.postValue(filtrados)
    }

}