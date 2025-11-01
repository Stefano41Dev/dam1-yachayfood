package com.example.yachayfood.ui.view.recetas_guardadas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yachayfood.R
import com.example.yachayfood.adapter.RecetasGuardadasAdapter
import com.example.yachayfood.databinding.ActivityListadoRecetasBinding
import com.example.yachayfood.ui.view.recetas_ia.RecetaGeneradaView

class ListadoRecetasView: AppCompatActivity() {

    private lateinit var binding: ActivityListadoRecetasBinding
    private val viewModel: ListadoRecetasViewModel by viewModels()
    private lateinit var adapter: RecetasGuardadasAdapter

    private var botonesFiltro: List<Button> = listOf()
    private var botonSeleccionado: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListadoRecetasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupBuscador()
        setupFiltros()
        observarCambios()

        viewModel.cargarRecetas()
    }

    private fun setupRecyclerView() {
        adapter = RecetasGuardadasAdapter(emptyList()) { receta ->
            val intent = Intent(this, RecetaGeneradaView::class.java).apply {
                // Pasamos la receta y la bandera de "modo vista"
                putExtra("RECETA_GUARDADA", receta)
            }
            startActivity(intent)
        }
        binding.recyclerRecetasGuardadas.layoutManager = LinearLayoutManager(this)
        binding.recyclerRecetasGuardadas.adapter = adapter
    }

    private fun observarCambios() {
        viewModel.listaDeRecetas.observe(this) { lista ->
            adapter.actualizarLista(lista)
            if (lista.isEmpty()) {
                Toast.makeText(this, "No se encontraron recetas", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBuscador() {
        binding.inputBuscarReceta.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.buscarRecetas(newText ?: "")
                return true
            }
        })
    }

    private fun setupFiltros() {
        botonesFiltro = listOf(
            binding.btnFiltroTodos,
            binding.btnFiltroNutritiva,
            binding.btnFiltroAceptable,
            binding.btnFiltroNoRecomendado
        )

        // El botón "Todos" empieza seleccionado
        seleccionarBoton(binding.btnFiltroTodos)

        binding.btnFiltroTodos.setOnClickListener {
            viewModel.filtrarPorClasificacion("Todos")
            seleccionarBoton(binding.btnFiltroTodos)
        }
        binding.btnFiltroNutritiva.setOnClickListener {
            viewModel.filtrarPorClasificacion("Nutritiva")
            seleccionarBoton(binding.btnFiltroNutritiva)
        }
        binding.btnFiltroAceptable.setOnClickListener {
            viewModel.filtrarPorClasificacion("Aceptable")
            seleccionarBoton(binding.btnFiltroAceptable)
        }
        binding.btnFiltroNoRecomendado.setOnClickListener {
            viewModel.filtrarPorClasificacion("No Recomendado")
            seleccionarBoton(binding.btnFiltroNoRecomendado)
        }
    }

    private fun seleccionarBoton(boton: Button) {
        // Deselecciona el botón anterior
        botonSeleccionado?.isSelected = false
        botonSeleccionado?.setTextColor(ContextCompat.getColor(this, R.color.button_filter_off_text))

        // Selecciona el nuevo botón
        boton.isSelected = true
        boton.setTextColor(ContextCompat.getColor(this, R.color.white))

        botonSeleccionado = boton
    }

}