package com.example.yachayfood.ui.view.detalle_Room

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.yachayfood.models.ProductoEntity
import com.example.yachayfood.databinding.ActivityDetalleProductoRoomBinding

class DetalleProductoRoomView : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleProductoRoomBinding
    private val viewModel: DetalleProductoRoomViewModel by viewModels()

    @SuppressLint("SetTextI18n")
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

    @SuppressLint("SetTextI18n")
    private fun mostrarDatosProducto(producto: ProductoEntity) {
        binding.txtNombreProducto.text = producto.nombre
        binding.txtDescripcion.text = "Marca: ${producto.marca}"
        binding.txtClasificacion.text = "NutriScore: ${producto.nutriscoreScore}"
        binding.txtCategoria.text = "Categorías: ${producto.categorias}"

        Glide.with(this)
            .load(producto.imagenUrl)
            .into(binding.imgProducto)

        val n = producto.nutriments
        binding.txtTablaNutricional.text = """
            Energía: ${n.energy_kcal_100g} kcal
            Grasas: ${n.fat_100g} g
            Grasas Saturadas: ${n.saturated_fat_100g} g
            Azúcares: ${n.sugars_100g} g
            Proteínas: ${n.proteins_100g} g
            Carbohidratos: ${n.carbohydrates_100g} g
            Fibras Alimentarias: ${n.fiber_100g} g
        """.trimIndent()

        binding.txtIngredientes.text = "Ingredientes: ${producto.ingredientes}"
    }
}
