package com.example.yachayfood.ui.view.recetas_ia

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.yachayfood.R
import com.example.yachayfood.databinding.ActivityRecetaGeneradaBinding
import com.example.yachayfood.models.ProductoEntity
import com.example.yachayfood.models.RecetaEntity
import androidx.core.graphics.toColorInt

class RecetaGeneradaView: AppCompatActivity() {

    private lateinit var binding: ActivityRecetaGeneradaBinding
    private val viewModel: RecetaGeneradaViewModel by viewModels()

    // Callback para deshabilitar el botón de atrás
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Toast.makeText(this@RecetaGeneradaView, "Usa los botones en pantalla", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecetaGeneradaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Deshabilitar retroceso nativo
//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                Toast.makeText(this@RecetaGeneradaView, "Usa los botones en pantalla", Toast.LENGTH_SHORT).show()
//            }
//        })

        // Obtener productos y generar receta
        val recetaGuardada = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("RECETA_GUARDADA", RecetaEntity::class.java)
        } else {
            intent.getParcelableExtra("RECETA_GUARDADA")
        }

        if (recetaGuardada != null) {
            // MODO VISTA (Viendo receta guardada)
            viewModel.iniciarModoVista(recetaGuardada)
        } else {
            onBackPressedDispatcher.addCallback(this, backPressedCallback) // Deshabilitar retroceso

            val productos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayListExtra("productos_seleccionados", ProductoEntity::class.java)
            } else {
                intent.getParcelableArrayListExtra("productos_seleccionados")
            }

            if (productos.isNullOrEmpty()) {
                Toast.makeText(this, "Error: No se seleccionaron productos", Toast.LENGTH_LONG).show()
                finish()
            } else {
                viewModel.generarReceta(productos)
            }
        }

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnGenerarOtro.setOnClickListener {
            finish() // Simplemente cierra esta actividad y vuelve a la de selección
        }

        binding.btnGuardarReceta.setOnClickListener {
            viewModel.guardarRecetaActual()
        }
    }

    private fun setupObservers() {
        viewModel.recetaGenerada.observe(this) { receta ->
            if (receta != null) {
                binding.progressBarReceta.visibility = View.GONE
                binding.scrollReceta.visibility = View.VISIBLE
                mostrarDatosReceta(receta)
            }
        }

        viewModel.esModoVista.observe(this) { esModoVista ->
            if (esModoVista) {
                // Ocultamos los botones de abajo
                binding.bottomButtonContainer.visibility = View.GONE
                // Permitimos el botón de retroceso nativo
                backPressedCallback.remove()
            }
        }

        viewModel.error.observe(this) { error ->
            binding.progressBarReceta.visibility = View.GONE
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            // Podrías mostrar un texto de error en lugar del scroll
        }

        viewModel.guardadoExitoso.observe(this) { guardado ->
            if (guardado) {
                Toast.makeText(this, "Receta guardada con éxito", Toast.LENGTH_SHORT).show()
                finish() // Vuelve a la pantalla anterior
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarDatosReceta(receta: RecetaEntity) {
        // Encabezado
        binding.txtRecetaNombre.text = receta.nombre
        binding.txtRecetaDescripcion.text = receta.descripcionCorta

        // Card de Clasificación
        binding.txtRecetaClasificacion.text = "Clasificación: ${receta.clasificacionReceta}"
        binding.txtRecetaClasificacionDesc.text = receta.clasificacionDescripcion

        // Lógica de color de la Card
        when (receta.clasificacionReceta) {
            "Nutritiva" -> {
                binding.cardClasificacionReceta.setBackgroundResource(R.drawable.bg_clasificacion_verde)
                binding.txtRecetaClasificacion.setTextColor(ContextCompat.getColor(this, R.color.text_classification_safe))
                binding.txtRecetaClasificacionDesc.setTextColor(ContextCompat.getColor(this, R.color.text_classification_safe))
            }
            "Aceptable" -> {
                binding.cardClasificacionReceta.setBackgroundResource(R.drawable.bg_clasificacion_naranja)
                binding.txtRecetaClasificacion.setTextColor(ContextCompat.getColor(this, R.color.text_classification_warning))
                binding.txtRecetaClasificacionDesc.setTextColor(ContextCompat.getColor(this, R.color.text_classification_warning))
            }
            "No Recomendado" -> {
                binding.cardClasificacionReceta.setBackgroundResource(R.drawable.bg_clasificacion_rojo)
                binding.txtRecetaClasificacion.setTextColor(ContextCompat.getColor(this, R.color.text_classification_danger))
                binding.txtRecetaClasificacionDesc.setTextColor(ContextCompat.getColor(this, R.color.text_classification_danger))
            }
        }

        // Ingredientes
        val ingredientesTexto = receta.ingredientes.joinToString("\n") {
            "• ${it.nombre} (${it.tipo})"
        }
        binding.txtRecetaIngredientes.text = ingredientesTexto

        // Pasos
        val pasosTexto = receta.pasos.mapIndexed { index, paso ->
            "${index + 1}. $paso"
        }.joinToString("\n")
        binding.txtRecetaPasos.text = pasosTexto

        // Análisis Nutricional
        binding.txtRecetaAnalisis.text = "Calorías: ${receta.caloriasAproximadas}\nMacros: ${receta.macros}"
    }

}