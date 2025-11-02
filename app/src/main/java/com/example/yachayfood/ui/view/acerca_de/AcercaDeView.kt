package com.example.yachayfood.ui.view.acerca_de

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.yachayfood.databinding.ActivityAcercaDeBinding

class AcercaDeView: AppCompatActivity() {

    private lateinit var binding: ActivityAcercaDeBinding
    private val viewModel: AcercaDeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcercaDeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
        setupInfoEquipo()
    }

    @SuppressLint("SetTextI18n")
    private fun setupInfoEquipo() {
        val version = "Versión: 1.4 (Curso D.A.M I)"
        val equipo = "Equipo: Piero Juarez Fernandez, Stefano Gonzales Reyna, Hawell Urbina Fabian"
        val proposito = "Propósito: YachayFood es una app diseñada para ayudarte a tomar decisiones más saludables. Escanea productos, analiza sus ingredientes con IA y genera recetas creativas."

        binding.txtEquipoInfo.text = "$version\n$equipo\n\n$proposito"
    }

    private fun setupListeners() {
        binding.btnBorrarDatos.setOnClickListener {
            mostrarDialogoConfirmacion()
        }
    }

    private fun setupObservers() {
        viewModel.borradoCompleto.observe(this) { completado ->
            if (completado) {
                Toast.makeText(this, "Se borraron todos los productos y recetas", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que deseas borrar todos tus productos escaneados y recetas guardadas? Esta acción no se puede deshacer.")
            .setPositiveButton("Sí, Borrar Todo") { _, _ ->
                viewModel.borrarTodosLosDatos()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

}