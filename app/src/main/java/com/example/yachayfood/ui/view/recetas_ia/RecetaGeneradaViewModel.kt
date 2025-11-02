package com.example.yachayfood.ui.view.recetas_ia

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.yachayfood.api.gemini.GeminiApiClient
import com.example.yachayfood.data.database.AppDatabase
import com.example.yachayfood.models.IngredienteReceta
import com.example.yachayfood.models.ProductoEntity
import com.example.yachayfood.models.RecetaEntity
import com.example.yachayfood.repository.RecetaRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.json.JSONObject

class RecetaGeneradaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecetaRepository
    private val gson = Gson()

    private val _recetaGenerada = MutableLiveData<RecetaEntity?>()
    val recetaGenerada: LiveData<RecetaEntity?> get() = _recetaGenerada

    private val _esModoVista = MutableLiveData<Boolean>(false)
    val esModoVista: LiveData<Boolean> get() = _esModoVista

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _guardadoExitoso = MutableLiveData<Boolean>(false)
    val guardadoExitoso: LiveData<Boolean> get() = _guardadoExitoso

    init {
        val recetaDao = AppDatabase.getInstance(application).recetaDao()
        repository = RecetaRepository(recetaDao)
    }

    fun iniciarModoVista(receta: RecetaEntity) {
        _recetaGenerada.postValue(receta)
        _esModoVista.postValue(true)
    }

    fun generarReceta(productos: List<ProductoEntity>) {
        // No generar si ya estamos en modo vista
        if (_esModoVista.value == true) return

        viewModelScope.launch {
            val prompt = crearPromptReceta(productos)
            val respuestaJson = GeminiApiClient.obtenerAnalisisProducto(prompt)

            if (respuestaJson == null) {
                _error.postValue("No se pudo generar la receta. Inténtalo de nuevo.")
                return@launch
            }

            Log.d("GeminiReceta", "Respuesta JSON: $respuestaJson")
            val receta = parsearRespuestaGemini(respuestaJson)

            if (receta == null) {
                _error.postValue("Error al procesar la respuesta de la IA.")
            } else {
                _recetaGenerada.postValue(receta)
            }
        }
    }

    fun guardarRecetaActual() {
        val receta = _recetaGenerada.value ?: return
        viewModelScope.launch {
            try {
                repository.guardarReceta(receta)
                _guardadoExitoso.postValue(true)
            } catch (e: Exception) {
                _error.postValue("Error al guardar la receta: ${e.message}")
            }
        }
    }

    private fun crearPromptReceta(productos: List<ProductoEntity>): String {
        val listaProductosTexto = productos.joinToString("\n") {
            "- ${it.nombre} (Marca: ${it.marca}, Descripcion: ${it.descripcion}, Ingredientes: ${it.ingredientes}, Calificación: ${it.clasificacionYachay ?: it.clasificacion})"
        }

        return """
        Eres un asistente de cocina creativo llamado Yachay. Recibes una lista de productos escaneados. 
        Tu tarea es crear una receta simple y saludable (si lo saludable no es posible entonces no tienes que seguir esta regla)
        que use todos los productos escaneados que te pasaré como ingredientes principales y tambien ingredientes sugeridos por ti
        (los nombres de los ingredientes sugeridos que sea como se les dice en Perú).
        
        Productos Escaneados:
        $listaProductosTexto

        Debes responder ÚNICAMENTE con un objeto JSON válido, sin texto introductorio, markdown (```json), ni explicaciones.
        La estructura del JSON debe ser la siguiente:
        {
          "nombre": "(string: Nombre creativo para la receta)",
          "descripcion_corta": "(string: Subtítulo corto y atractivo, 1-2 líneas)",
          "clasificacion_receta": "(string: Tu veredicto nutricional. Solo una de estas 3 opciones: 'Nutritiva', 'Aceptable', 'No Recomendado')",
          "clasificacion_descripcion": "(string: Justificación corta de tu veredicto, 1-2 líneas)",
          "ingredientes": [
            {"nombre": "(string: Ej: 1 taza de Avena Instantánea)", "tipo": "(string: 'Producto Escaneado' si es de la lista, o 'Sugerencia IA' si es un ingrediente adicional que sugieres)"},
            {"nombre": "(string: Ej: 1/2 plátano maduro)", "tipo": "(string: 'Sugerencia IA para dulzor natural' o similar)"}
          ],
          "pasos": [
            "(string: Paso 1. Sé claro y conciso)",
            "(string: Paso 2. Continúa los pasos)"
          ],
          "analisis_nutricional": {
            "calorias_aproximadas": "(string: Ej: '350 kcal por porción')",
            "macros": "(string: Resumen simple de macros. Ej: 'Alta en fibra y carbohidratos complejos, moderada en proteína.')"
          }
        }
        """
    }

    private fun parsearRespuestaGemini(jsonString: String): RecetaEntity? {
        return try {
            val cleanJson = jsonString.trim().removePrefix("```json").removeSuffix("```")
            val jsonObject = JSONObject(cleanJson)

            // Parseo de ingredientes
            val ingredientesJsonArray = jsonObject.getJSONArray("ingredientes")
            val ingredientesList = mutableListOf<IngredienteReceta>()
            for (i in 0 until ingredientesJsonArray.length()) {
                val ingObj = ingredientesJsonArray.getJSONObject(i)
                ingredientesList.add(
                    IngredienteReceta(
                        nombre = ingObj.getString("nombre"),
                        tipo = ingObj.getString("tipo")
                    )
                )
            }

            // Parseo de pasos
            val pasosJsonArray = jsonObject.getJSONArray("pasos")
            val pasosList = mutableListOf<String>()
            for (i in 0 until pasosJsonArray.length()) {
                pasosList.add(pasosJsonArray.getString(i))
            }

            // Parseo de análisis nutricional
            val analisisObj = jsonObject.getJSONObject("analisis_nutricional")
            val calorias = analisisObj.getString("calorias_aproximadas")
            val macros = analisisObj.getString("macros")

            RecetaEntity(
                nombre = jsonObject.getString("nombre"),
                descripcionCorta = jsonObject.getString("descripcion_corta"),
                clasificacionReceta = jsonObject.getString("clasificacion_receta"),
                clasificacionDescripcion = jsonObject.getString("clasificacion_descripcion"),
                ingredientes = ingredientesList,
                pasos = pasosList,
                caloriasAproximadas = calorias,
                macros = macros
            )
        } catch (e: Exception) {
            Log.e("GeminiRecetaParseError", "Error al parsear JSON: ${e.message}", e)
            null
        }
    }

}