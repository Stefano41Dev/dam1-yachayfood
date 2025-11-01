package com.example.yachayfood.ui.view.pantalla_principal

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yachayfood.adapter.EscaneosRecientesAdapter
import com.example.yachayfood.data.database.AppDatabase
import com.example.yachayfood.databinding.ActivityPantallaPrincipalBinding
import com.example.yachayfood.models.ProductoEntity
import com.example.yachayfood.ui.view.detalle_Room.DetalleProductoRoomView
import com.example.yachayfood.ui.view.escanear_producto.EscanearProductoView
import com.example.yachayfood.ui.view.historial.ListadoProductosView
import com.example.yachayfood.ui.view.recetas_ia.RecetasIAView

class PantallaPrincipalView : AppCompatActivity() {

    private lateinit var binding: ActivityPantallaPrincipalBinding
    private lateinit var adapter: EscaneosRecientesAdapter

    private var menuVisible = false

    private val viewModel: PantallaPrincipalViewModel by viewModels {
        PantallaPrincipalViewModelFactory(AppDatabase.getInstance(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        setupScanButton()
        setupObservers()
        setupButtonOpciones()
        viewModel.cargarProductosRecientes()
    }

    private fun setupRecyclerView() {
        binding.recyclerEscaneos.layoutManager = GridLayoutManager(this, 1)
        adapter = EscaneosRecientesAdapter(listOf()) { producto ->
            abrirDetalleProducto(producto)
        }
        binding.recyclerEscaneos.adapter = adapter

        val spacingInPixels = (5 * resources.displayMetrics.density).toInt()
        binding.recyclerEscaneos.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: android.view.View, parent: RecyclerView, state: RecyclerView.State
            ) {
                outRect.bottom = spacingInPixels
            }
        })
    }

    private fun setupScanButton() {
        binding.CircleContainer.setOnClickListener {
            startActivity(Intent(this, EscanearProductoView::class.java))
        }
    }

    private fun setupObservers() {
        viewModel.productosRecientes.observe(this) { productos ->
            adapter.actualizarLista(productos)
            if(productos.isEmpty()){
                binding.containerSinEscaneos.visibility = View.VISIBLE
                binding.recyclerEscaneos.visibility = View.GONE
            }else{
                binding.containerSinEscaneos.visibility = View.GONE
                binding.recyclerEscaneos.visibility = View.VISIBLE

            }
        }

    }

    private fun setupButtonOpciones() {
        val menu = binding.menuOverlay
        val dimBackground = binding.dimBackground

        // Botón para ABRIR el menú
        binding.btnOpciones.setOnClickListener {
            toggleMenu(menu, dimBackground)
        }

        // Botón para CERRAR el menú (tocando el fondo)
        dimBackground.setOnClickListener {
            if (menuVisible) toggleMenu(menu, dimBackground)
        }

        // --- Opciones del Menú ---

        // 1. Inicio
        binding.btnMenuInicio.setOnClickListener {
            // Ya estamos en Inicio, así que solo cerramos el menú
            toggleMenu(menu, dimBackground)
        }

        // 2. Productos Escaneados
        binding.btnMenuProductos.setOnClickListener {
            startActivity(Intent(this, ListadoProductosView::class.java))
            toggleMenu(menu, dimBackground)
        }

        // 3. Recetas Creativas IA (Placeholder)
        binding.btnMenuRecetas.setOnClickListener {
            startActivity(Intent(this, RecetasIAView::class.java))
            toggleMenu(menu, dimBackground)
        }

        // 4. Créditos (Placeholder)
        binding.btnMenuCreditos.setOnClickListener {
            Toast.makeText(this, "Créditos (Próximamente)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleMenu(menu: View, dimBackground: View) {
        if (menuVisible) {
            menu.animate()
                .translationX(-menu.width.toFloat())
                .setDuration(300)
                .withEndAction {
                    menu.visibility = View.GONE
                    dimBackground.visibility = View.GONE
                }
        } else {
            dimBackground.visibility = View.VISIBLE
            menu.visibility = View.VISIBLE
            menu.animate()
                .translationX(0f)
                .setDuration(300)
        }
        menuVisible = !menuVisible
    }

    private fun abrirDetalleProducto(producto: ProductoEntity) {
        val intent = Intent(this, DetalleProductoRoomView::class.java)
        intent.putExtra("producto_room", producto)
        startActivity(intent)
    }

    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        if (menuVisible) {
            toggleMenu(binding.menuOverlay, binding.dimBackground)
        } else {
            AlertDialog.Builder(this)
                .setMessage("¿Estás seguro que quieres salir de la aplicación?")
                .setPositiveButton("Sí") { _, _ -> finishAffinity() }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarProductosRecientes()
    }
}
