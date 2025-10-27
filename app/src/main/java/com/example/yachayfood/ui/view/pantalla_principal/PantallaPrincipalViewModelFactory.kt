package com.example.yachayfood.ui.view.pantalla_principal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yachayfood.data.AppDatabase

class PantallaPrincipalViewModelFactory(
    private val database: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PantallaPrincipalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PantallaPrincipalViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}