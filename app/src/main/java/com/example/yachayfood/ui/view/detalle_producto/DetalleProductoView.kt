package com.example.yachayfood.ui.view.detalle_producto

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.yachayfood.databinding.ActivityDetalleProductoBinding
import com.example.yachayfood.models.ProductoEntity

class DetalleProductoView : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleProductoBinding
    private val viewModel: DetalleProductoViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val producto = intent.getParcelableExtra<ProductoEntity>("producto")
        val codigoProducto = intent.getStringExtra("codigoProducto")

        viewModel.producto.observe(this) { producto ->
            producto?.let { mostrarDatosProducto(it) }
        }

        viewModel.mensaje.observe(this) { mensaje ->
            binding.txtNombreProducto.text = mensaje
        }

        viewModel.cargarProducto(producto)
        codigoProducto?.let { viewModel.cargarProductoPorCodigo(it) }

        binding.btnBorrarProducto.setOnClickListener {
            finish()
        }
    }

    private fun mostrarDatosProducto(producto: ProductoEntity) {
        binding.txtNombreProducto.text = producto.nombre
        binding.txtDescripcion.text = producto.descripcion
        binding.txtClasificacion.text = "Clasificación: ${producto.clasificacion}"
        binding.txtCategoria.text = "Categoría: ${producto.categorias}"

        Glide.with(this)
            .load(producto.imagenUrl)
            .into(binding.imgProducto)

        val n = producto.nutriments
        binding.txtTablaNutricional.text = """
            Energía: ${n.energy_100g} kcal
            Grasas: ${n.fat_100g} g
            Grasas Saturadas: ${n.saturated_fat_100g} g
            Azúcares: ${n.sugars_100g} g
            Proteínas: ${n.proteins_100g} g
            Carbohidratos: ${n.carbohydrates_100g} g
            Hidratos de Carbono: ${n.carbohydrates_100g} g
            Fibras Alimentarias: ${n.fiber_100g} g
        """.trimIndent()

        binding.txtIngredientes.text = producto.ingredientes
    }
}
