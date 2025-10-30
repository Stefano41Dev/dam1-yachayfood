package com.example.yachayfood.ui.view.historial

import android.content.Intent
import com.example.yachayfood.R
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yachayfood.adapter.ListadoProductosAdapter
import com.example.yachayfood.databinding.ActivityListadoProductosBinding

import com.example.yachayfood.ui.view.detalle_producto.DetalleProductoView

class ListadoProductosView : AppCompatActivity() {

    private lateinit var binding: ActivityListadoProductosBinding
    private lateinit var escaneosAdapter: ListadoProductosAdapter
    private val historialViewModel: ListadoProductosViewModel by viewModels()

    /*// Guardamos el último botón seleccionado (para poder deseleccionarlo)
    private var botonSeleccionadoActual: Button? = null
    private var filtroActivo: String? = null*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListadoProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBuscador()
        observarCambios()
        setupBotonesCategorias()
        setupRecyclerView()

        historialViewModel.listaDeProductos.observe(this) { listaProductos ->
            if (listaProductos.isNotEmpty()) {
                escaneosAdapter.actualizarLista(listaProductos)
            }
        }

        historialViewModel.error.observe(this) { mensajeError ->
            Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
        }

        historialViewModel.obtenerTodosLosProductos()
    }

    private fun setupRecyclerView() {
        escaneosAdapter = ListadoProductosAdapter(emptyList()) { productoEntity ->
            val intent = Intent(this, DetalleProductoView::class.java).apply {
                putExtra("producto", productoEntity)
            }
            startActivity(intent)
        }

        binding.recyclerProductos.apply {
            adapter = escaneosAdapter
            layoutManager = LinearLayoutManager(this@ListadoProductosView)
        }
    }

    private fun observarCambios() {
        historialViewModel.listaDeProductos.observe(this) { lista ->
            escaneosAdapter.actualizarLista(lista)
        }
    }

    private fun setupBuscador() {
        val searchView = binding.inputBuscarProducto
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                historialViewModel.buscarProductos(newText ?: "")
                return true
            }
        })
    }

    private fun setupBotonesCategorias() {
        val botones = listOf(
            binding.btnNaturalRecomendado to "Natural y Recomendado",
            binding.btnSaludable to "Saludable",
            binding.btnAceptable to "Aceptable",
            binding.btnConsumoModerado to "Consumo Moderado",
            binding.btnNoRecomendado to "No Recomendado",
            binding.btnNoClasificado to "No Clasificado"
        )

        botones.forEach { (boton, clasificacion) ->
            boton.setOnClickListener {
                marcarBotonSeleccionado(boton, clasificacion)
            }
        }
    }


    private fun marcarBotonSeleccionado(boton: Button, clasificacion: String) {
        val estaSeleccionado = boton.background.constantState ==
                getDrawable(R.drawable.bg_button_category_on)?.constantState

        if (estaSeleccionado) {
            boton.setBackgroundResource(R.drawable.bg_button_category)
        } else {
            boton.setBackgroundResource(R.drawable.bg_button_category_on)
        }

        historialViewModel.filtrarPorClasificacion(clasificacion)
    }

}
