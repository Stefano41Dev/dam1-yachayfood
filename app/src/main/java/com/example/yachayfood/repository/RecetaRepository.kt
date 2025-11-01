package com.example.yachayfood.repository

import com.example.yachayfood.data.RecetaDao
import com.example.yachayfood.models.RecetaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecetaRepository(private val recetaDao: RecetaDao) {

    suspend fun guardarReceta(receta: RecetaEntity) {
        withContext(Dispatchers.IO) {
            recetaDao.insertarReceta(receta)
        }
    }

    // (Esta la usarás para la futura pantalla "Ver Recetas Guardadas")
    suspend fun obtenerRecetasGuardadas(): List<RecetaEntity> {
        return withContext(Dispatchers.IO) {
            recetaDao.obtenerTodasLasRecetas()
        }
    }

}