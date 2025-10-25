package com.example.yachayfood.ui.view.detalle_producto

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.yachayfood.databinding.ActivityDetalleProductoBinding
import com.example.yachayfood.models.Producto

class DetalleProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleProductoBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar el producto que fue enviado desde EscanearProductoActivity
        val producto = intent.getParcelableExtra<Producto>("producto")
        if (producto != null) {
            mostrarDatosProducto(producto)
        } else {
            binding.txtNombreProducto.text = "No se encontró información del producto"
        }

        // Botón eliminar (ejemplo: puedes agregar funcionalidad real después)
        binding.btnBorrarProducto.setOnClickListener {
            finish() // Por ahora solo cerramos la pantalla
        }
    }

    private fun mostrarDatosProducto(producto: Producto) {
        binding.txtNombreProducto.text = producto.nombreProducto
        binding.txtDescripcion.text = producto.descripcion
        binding.txtClasificacion.text = "Clasificación: ${producto.clasificacion}"
        binding.txtCategoria.text = "Categoría: ${producto.categorias.joinToString(", ")}"

        // Cargar imagen con Glide
        Glide.with(this)
            .load(producto.imagenUrl)
            .into(binding.imgProducto)

        // Mostrar nutrientes
        val n = producto.nutrientes
        val textoNutricional = """
            Energía: ${n.energia} kcal
            Grasas: ${n.grasas} g
            Grasas Saturadas: ${n.grasasSaturadas} g
            Azúcares: ${n.azucares} g
            Proteínas: ${n.proteinas} g
            Carbohidratos: ${n.carbohidratos} g
            Hidratos de Carbono: ${n.hidratosCarbono} g
            Fibras Alimentarias: ${n.fibrasAlimentarias} g
        """.trimIndent()

        binding.txtTablaNutricional.text = textoNutricional

        // Mostrar ingredientes
        binding.txtIngredientes.text = producto.ingredientes.joinToString(", ")
    }
}
