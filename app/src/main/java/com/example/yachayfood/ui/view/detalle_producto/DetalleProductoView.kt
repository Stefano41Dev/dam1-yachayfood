package com.example.yachayfood.ui.view.detalle_producto

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.yachayfood.databinding.ActivityDetalleProductoBinding
import com.example.yachayfood.models.Producto

class DetalleProductoView : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleProductoBinding
    private val viewModel: DetalleProductoViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val producto = intent.getParcelableExtra<Producto>("producto")
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

    private fun mostrarDatosProducto(producto: Producto) {
        binding.txtNombreProducto.text = producto.nombreProducto
        binding.txtDescripcion.text = producto.descripcion
        binding.txtClasificacion.text = "Clasificación: ${producto.clasificacion}"
        binding.txtCategoria.text = "Categoría: ${producto.categorias.joinToString(", ")}"

        Glide.with(this)
            .load(producto.imagenUrl)
            .into(binding.imgProducto)

        val n = producto.nutrientes
        binding.txtTablaNutricional.text = """
            Energía: ${n.energia} kcal
            Grasas: ${n.grasas} g
            Grasas Saturadas: ${n.grasasSaturadas} g
            Azúcares: ${n.azucares} g
            Proteínas: ${n.proteinas} g
            Carbohidratos: ${n.carbohidratos} g
            Hidratos de Carbono: ${n.hidratosCarbono} g
            Fibras Alimentarias: ${n.fibrasAlimentarias} g
        """.trimIndent()

        binding.txtIngredientes.text = producto.ingredientes.joinToString(", ")
    }
}
