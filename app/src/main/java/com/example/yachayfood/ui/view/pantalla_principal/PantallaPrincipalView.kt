package com.example.yachayfood.ui.view.pantalla_principal

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yachayfood.adapter.EscaneosRecientesAdapter
import com.example.yachayfood.data.local.AppDatabase
import com.example.yachayfood.data.local.ProductoEntity
import com.example.yachayfood.data.local.toProducto
import com.example.yachayfood.databinding.ActivityPantallaPrincipalBinding
import com.example.yachayfood.models.Producto
import com.example.yachayfood.repository.ProductoRepository
import com.example.yachayfood.ui.view.detalle_Room.DetalleProductoRoomActivity
import com.example.yachayfood.ui.view.detalle_producto.DetalleProductoActivity
import com.example.yachayfood.ui.view.escanear_producto.EscanearProductoActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PantallaPrincipalView : AppCompatActivity() {

    private lateinit var binding: ActivityPantallaPrincipalBinding
    private lateinit var adapter: EscaneosRecientesAdapter
    private val productoRepository = ProductoRepository()
    private val productoDao by lazy { AppDatabase.getInstance(this).productoDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupScanButton()
        cargarProductos()
    }

    private fun setupRecyclerView() {
        binding.recyclerEscaneos.layoutManager = GridLayoutManager(this, 1)
        adapter = EscaneosRecientesAdapter(listOf()) { productoEntity ->
            buscarProducto(productoEntity)
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
            startActivity(Intent(this, EscanearProductoActivity::class.java))
        }
    }

    private fun buscarProducto(productoEntity: ProductoEntity) {
        val intent = Intent(this, DetalleProductoRoomActivity::class.java)
        intent.putExtra("producto_room", productoEntity)
        startActivity(intent)
    }

    private fun cargarProductos() {
        CoroutineScope(Dispatchers.IO).launch {
            val recientes: List<ProductoEntity> = productoDao.obtenerUltimosTres()
            withContext(Dispatchers.Main) {
                adapter.actualizarLista(recientes)
            }
        }
    }

    private fun hayInternet(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("¿Estás seguro que quieres salir de la aplicación?")
            .setPositiveButton("Sí") { _, _ -> finishAffinity() }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onResume() {
        super.onResume()
        cargarProductos()
    }
}
