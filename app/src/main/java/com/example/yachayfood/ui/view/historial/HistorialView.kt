package com.example.yachayfood.ui.view.historial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yachayfood.adapter.EscaneosRecientesAdapter
import com.example.yachayfood.models.basedata.toProducto
import com.example.yachayfood.databinding.ActivityHistorialBinding
import com.example.yachayfood.ui.view.detalle_producto.DetalleProductoView

class HistorialView : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialBinding
    private lateinit var escaneosAdapter: EscaneosRecientesAdapter

    // Usamos un ViewModel específico para esta pantalla
    private val historialViewModel: HistorialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Configurar el RecyclerView y el Adapter
        setupRecyclerView()

        // 2. Observar los datos del ViewModel
        historialViewModel.listaDeProductos.observe(this) { listaProductos ->
            if (listaProductos.isNotEmpty()) {
                // Actualizamos la lista en el adapter cuando el ViewModel nos da datos
                escaneosAdapter.actualizarLista(listaProductos)
            } else {
                Toast.makeText(this, "El historial está vacío", Toast.LENGTH_SHORT).show()
            }
        }

        historialViewModel.error.observe(this) { mensajeError ->
            Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
        }

        // 3. Pedir los datos al ViewModel cuando se crea la actividad
        historialViewModel.obtenerTodosLosProductos()
    }

    private fun setupRecyclerView() {
        // Inicializa el adapter pasándole la lógica del clic
        escaneosAdapter = EscaneosRecientesAdapter(emptyList()) { productoEntity ->
            // ESTA ES LA LÓGICA CLAVE QUE FALTABA
            // Cuando un ítem es presionado:

            // a. Convertimos el ProductoEntity de la base de datos a un Producto (Parcelable)
            val productoParaNavegar = productoEntity.toProducto()

            // b. Creamos el Intent para ir a DetalleProductoActivity
            val intent = Intent(this, DetalleProductoView::class.java).apply {
                // c. Adjuntamos el producto Parcelable como un "extra"
                putExtra("producto", productoParaNavegar)
            }

            // d. Iniciamos la actividad de detalle
            startActivity(intent)
        }

        binding.recyclerViewHistorial.apply { // Asegúrate de que el ID sea correcto
            adapter = escaneosAdapter
            layoutManager = LinearLayoutManager(this@HistorialView)
        }
    }
}
