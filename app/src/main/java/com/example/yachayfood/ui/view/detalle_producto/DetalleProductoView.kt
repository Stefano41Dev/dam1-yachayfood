package com.example.yachayfood.ui.view.detalle_producto

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
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

    @SuppressLint("SetTextI18n")
    private fun mostrarDatosProducto(producto: ProductoEntity) {

        // --- DATOS BÁSICOS ---
        binding.txtNombreProducto.text = producto.nombre ?: "Nombre no disponible"
        binding.txtDescripcion.text = producto.descripcion ?: "Sin descripción."

        Glide.with(this)
            .load(producto.imagenUrl)
            .into(binding.imgProducto)

        // --- LÓGICA DE OCTÓGONOS ---
        binding.imgOctogonoGrasasSaturadas.visibility = if (producto.octogonoGrasasSaturadas == "si") View.VISIBLE else View.GONE
        binding.imgOctogonoAzucar.visibility = if (producto.octogonoAzucar == "si") View.VISIBLE else View.GONE
        binding.imgOctogonoSodio.visibility = if (producto.octogonoSodio == "si") View.VISIBLE else View.GONE
        binding.imgOctogonoGrasasTrans.visibility = if (producto.octogonoGrasasTrans == "si") View.VISIBLE else View.GONE

        // --- CLASIFICACIÓN YACHAY ---
        binding.txtClasificacion.text = "Clasificación: ${producto.clasificacionYachay ?: producto.clasificacion ?: "N/A"}"
        binding.txtCategoria.text = "Categoría: ${getCategoriaFromClasificacion(producto.clasificacionYachay ?: producto.clasificacion)}"

        // --- ANÁLISIS YACHAY ---
        if (producto.analisisYachay.isNullOrEmpty()) {
            binding.txtTituloAnalisisYachay.visibility = View.GONE
            binding.txtAnalisisYachay.visibility = View.GONE
        } else {
            binding.txtTituloAnalisisYachay.visibility = View.VISIBLE
            binding.txtAnalisisYachay.visibility = View.VISIBLE
            binding.txtAnalisisYachay.text = producto.analisisYachay
        }

        // --- INFO NUTRICIONAL ---
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

        binding.txtIngredientes.text = producto.ingredientes ?: "No especificados."
    }

    // Helper para obtener la categoría descriptiva
    private fun getCategoriaFromClasificacion(clasificacion: String?): String {
        return when (clasificacion?.uppercase()) {
            "AD" -> "Natural y Recomendado"
            "A" -> "Saludable"
            "B" -> "Aceptable"
            "C" -> "Consumo Moderado"
            "D" -> "No Recomendado"
            else -> "No clasificado"
        }
    }
}
