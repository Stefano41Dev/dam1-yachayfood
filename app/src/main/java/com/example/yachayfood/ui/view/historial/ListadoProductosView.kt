package com.example.yachayfood.ui.view.historial

import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListadoProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        historialViewModel.listaDeProductos.observe(this) { listaProductos ->
            if (listaProductos.isNotEmpty()) {
                escaneosAdapter.actualizarLista(listaProductos)
            } else {
                Toast.makeText(this, "El historial está vacío", Toast.LENGTH_SHORT).show()
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
}
