package com.example.yachayfood.ui.view.recetas_ia

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yachayfood.adapter.ProductoSeleccionadoAdapter
import com.example.yachayfood.databinding.ActivityRecetasIaBinding
import com.example.yachayfood.ui.view.recetas_guardadas.ListadoRecetasView

class RecetasIAView: AppCompatActivity() {

    private lateinit var binding: ActivityRecetasIaBinding
    private val viewModel: RecetasIAViewModel by viewModels()
    private lateinit var adapter: ProductoSeleccionadoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecetasIaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        setupObservers()

        viewModel.cargarProductos()
    }

    private fun setupRecyclerView() {
        adapter = ProductoSeleccionadoAdapter(emptyList()) { producto, isChecked ->
            viewModel.onProductoSeleccionado(producto, isChecked)
        }
        binding.recyclerProductosSeleccion.layoutManager = LinearLayoutManager(this)
        binding.recyclerProductosSeleccion.adapter = adapter
    }

    private fun setupListeners() {
        binding.inputBuscarProducto.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.buscarProducto(newText ?: "")
                return true
            }
        })

        binding.btnGenerarReceta.setOnClickListener {
            val seleccionados = viewModel.productosSeleccionados.value
            if (seleccionados.isNullOrEmpty()) {
                Toast.makeText(this, "Debes seleccionar al menos un producto", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, RecetaGeneradaView::class.java).apply {
                    putParcelableArrayListExtra("productos_seleccionados", ArrayList(seleccionados))
                }
                startActivity(intent)
            }
        }

        binding.btnVerRecetasGuardadas.setOnClickListener {
            startActivity(Intent(this, ListadoRecetasView::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        viewModel.listaFiltrada.observe(this) { lista ->
            adapter.actualizarLista(lista)
        }

        viewModel.productosSeleccionados.observe(this) { seleccionados ->
            val count = seleccionados.size
            binding.btnGenerarReceta.text = "Generar Receta IA - $count producto(s)"
            // Habilitar o deshabilitar el botón
            binding.btnGenerarReceta.isEnabled = count > 0
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

}