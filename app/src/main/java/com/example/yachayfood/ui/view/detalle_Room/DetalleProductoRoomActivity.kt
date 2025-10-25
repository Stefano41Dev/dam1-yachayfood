package com.example.yachayfood.ui.view.detalle_Room

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.yachayfood.data.local.ProductoEntity
import com.example.yachayfood.databinding.ActivityDetalleProductoRoomBinding

class DetalleProductoRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleProductoRoomBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProductoRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val producto = intent.getParcelableExtra<ProductoEntity>("producto_room")
        if (producto != null) {
            mostrarDatosProducto(producto)
        } else {
            binding.txtNombreProducto.text = "No se encontró información del producto"
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
        val textoNutricional = """
            Energía: ${n?.energy_kcal_100g ?: 0.0} kcal
            Grasas: ${n?.fat_100g ?: 0.0} g
            Grasas Saturadas: ${n?.saturated_fat_100g ?: 0.0} g
            Azúcares: ${n?.sugars_100g ?: 0.0} g
            Proteínas: ${n?.proteins_100g ?: 0.0} g
            Carbohidratos: ${n?.carbohydrates_100g ?: 0.0} g
            Fibras Alimentarias: ${n?.fiber_100g ?: 0.0} g
        """.trimIndent()

        binding.txtTablaNutricional.text = textoNutricional
        binding.txtIngredientes.text = producto.ingredientes ?: "No especificados"
    }
}