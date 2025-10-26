package com.example.yachayfood.ui.view.detalle_Room

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.yachayfood.models.basedata.ProductoEntity
import com.example.yachayfood.databinding.ActivityDetalleProductoRoomBinding

class DetalleProductoRoomView : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleProductoRoomBinding
    private val viewModel: DetalleProductoRoomViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProductoRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val producto = intent.getParcelableExtra<ProductoEntity>("producto_room")
        viewModel.setProducto(producto)

        viewModel.producto.observe(this) { producto ->
            producto?.let { mostrarDatosProducto(it) } ?: run {
                binding.txtNombreProducto.text = "No se encontró información del producto"
            }
        }

        binding.btnBorrarProducto.setOnClickListener {
            finish()
        }
    }

    private fun mostrarDatosProducto(producto: ProductoEntity) {
        binding.txtNombreProducto.text = producto.nombre ?: "Sin nombre"
        binding.txtDescripcion.text = producto.marca ?: "Sin marca"
        binding.txtClasificacion.text = "NutriScore: ${producto.nutriscoreScore ?: "N/A"}"
        binding.txtCategoria.text = "Categorías: ${producto.categorias ?: "No disponible"}"

        Glide.with(this)
            .load(producto.imagenUrl)
            .into(binding.imgProducto)

        val n = producto.nutriments
        binding.txtTablaNutricional.text = """
            Energía: ${n?.energy_kcal_100g ?: 0.0} kcal
            Grasas: ${n?.fat_100g ?: 0.0} g
            Grasas Saturadas: ${n?.saturated_fat_100g ?: 0.0} g
            Azúcares: ${n?.sugars_100g ?: 0.0} g
            Proteínas: ${n?.proteins_100g ?: 0.0} g
            Carbohidratos: ${n?.carbohydrates_100g ?: 0.0} g
            Fibras Alimentarias: ${n?.fiber_100g ?: 0.0} g
        """.trimIndent()

        binding.txtIngredientes.text = producto.ingredientes ?: "No especificados"
    }
}
