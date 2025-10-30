package com.example.yachayfood.ui.view.detalle_Room

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
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
        // --- DATOS BÁSICOS ---
        binding.txtNombreProducto.text = producto.nombre ?: "Nombre no disponible"
        // El campo 'descripcion' en Figma es el 'lorem ipsum', que coincide con la descripción genérica
        binding.txtDescripcion.text = "Marca: ${producto.marca ?: "No especificada"}\n${producto.descripcion ?: ""}"

        Glide.with(this)
            .load(producto.imagenUrl)
            .placeholder(com.example.yachayfood.R.drawable.ic_launcher_background) // Agregado placeholder
            .into(binding.imgProducto)

        // --- LÓGICA DE OCTÓGONOS ---
        binding.imgOctogonoGrasasSaturadas.visibility = if (producto.octogonoGrasasSaturadas == "si") View.VISIBLE else View.GONE
        binding.imgOctogonoAzucar.visibility = if (producto.octogonoAzucar == "si") View.VISIBLE else View.GONE
        binding.imgOctogonoSodio.visibility = if (producto.octogonoSodio == "si") View.VISIBLE else View.GONE
        binding.imgOctogonoGrasasTrans.visibility = if (producto.octogonoGrasasTrans == "si") View.VISIBLE else View.GONE

        // --- CLASIFICACIÓN YACHAY) ---
        // Usamos la clasificación de Yachay (AD, A, B, C, D) o el NutriScore (A, B, C, D, E) como fallback
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
            Energía: ${n.energy_kcal_100g} kcal
            Grasas: ${n.fat_100g} g
            Grasas Saturadas: ${n.saturated_fat_100g} g
            Azúcares: ${n.sugars_100g} g
            Proteínas: ${n.proteins_100g} g
            Carbohidratos: ${n.carbohydrates_100g} g
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
            // NutriScore fallback
            "E" -> "No Recomendado"
            else -> "No clasificado"
        }
    }
}
