package com.example.yachayfood.repository

import android.util.Log
import com.example.yachayfood.api.ApiClient
import com.example.yachayfood.api.gemini.GeminiApiClient
import com.example.yachayfood.data.AppDatabase
import com.example.yachayfood.models.NutrimentsEntity
import com.example.yachayfood.models.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.math.round

class ProductoRepository(database: AppDatabase) {

    private val productoDao = database.productoDao()
    private val api = ApiClient.instance

    suspend fun buscarProductoPorCodigo(codigo: String): ProductoEntity? = withContext(Dispatchers.IO) {

        val productoLocal = productoDao.obtenerProductoPorCodigo(codigo)
        if (productoLocal != null) {
            productoDao.actualizarFechaEscaneo(codigo, System.currentTimeMillis())
            return@withContext productoLocal
        }

        val response = api.getProductByCode(codigo)
        val body = response.body()

        if (response.isSuccessful && body?.status == 1 && body.product != null) {
            val p = body.product

            val productoEntity = ProductoEntity(
                codigo = codigo,
                nombre = p.product_name ?: "Producto sin nombre",
                descripcion = p.generic_name ?: "Sin descripción",
                clasificacion = p.nutriscore_grade?: "Desconocido",
                /*clasificacion = p.nutriscore_score?.let { score ->
                    when {
                        score <= 2 -> "A"
                        score <= 5 -> "B"
                        score <= 8 -> "C"
                        score <= 11 -> "D"
                        else -> "E"
                    }
                } ?: "B",*/
                marca = p.brands ?: "No especificado",
                paises = p.countries ?: "No especificado",
                empaque = p.packaging ?: "No especificado",
                cantidad = p.quantity?.replace("[^0-9.]".toRegex(), "")?.toDoubleOrNull() ?: 0.0,
                imagenUrl = p.image_url ?: "Imagen no disponible",
                ingredientes = p.ingredients_text ?: "No especificado",
                categorias = p.categories?: "No especificado",
                //categorias = p.categories_tags?.joinToString(",") { it.removePrefix("en:") } ?: "Desconocido",
                nutriscoreScore = p.nutriscore_score ?: 0,
                fechaEscaneo = System.currentTimeMillis(),
                nutriments = NutrimentsEntity(
                    energy_kcal_100g = round(p.nutriments?.energy_kcal_100g ?: 0.0),
                    energy_100g = round(p.nutriments?.energy_100g ?: 0.0),
                    fat_100g = round(p.nutriments?.fat_100g ?: 0.0),
                    saturated_fat_100g = round(p.nutriments?.saturated_fat_100g ?: 0.0),
                    sugars_100g = round(p.nutriments?.sugars_100g ?: 0.0),
                    proteins_100g = round(p.nutriments?.proteins_100g ?: 0.0),
                    carbohydrates_100g = round(p.nutriments?.carbohydrates_100g ?: 0.0),
                    fiber_100g = round(p.nutriments?.fiber_100g ?: 0.0)
                )
            )

            // --- INICIO DE LA INTEGRACIÓN CON GEMINI ---
            val prompt = crearPromptParaGemini(productoEntity)

            val respuestaGemini = GeminiApiClient.obtenerAnalisisProducto(prompt)
            Log.d("GeminiResponse", "Respuesta de Gemini: $respuestaGemini")

            val productoActualizado = if (respuestaGemini != null) {
                parsearRespuestaGemini(productoEntity, respuestaGemini)
            } else {
                productoEntity
            }
            // --- FIN DE LA INTEGRACIÓN CON GEMINI ---

            productoDao.insertarProducto(productoActualizado)
            return@withContext productoActualizado
        }

        return@withContext null
    }

    private fun crearPromptParaGemini(producto: ProductoEntity): String {
        val n = producto.nutriments
        val infoNutricional = """
            - Calorías (kcal por 100g): ${n.energy_kcal_100g}
            - Grasas (por 100g): ${n.fat_100g}g
            - Grasas Saturadas (por 100g): ${n.saturated_fat_100g}g
            - Azúcares (por 100g): ${n.sugars_100g}g
            - Proteínas (por 100g): ${n.proteins_100g}g
            - Carbohidratos (por 100g): ${n.carbohydrates_100g}g
            - Fibra (por 100g): ${n.fiber_100g}g
        """.trimIndent()

        // Nota: Open Food Facts no suele proveer Sodio o Grasas Trans directamente en 'nutriments'.
        // La IA deberá inferirlo de los ingredientes si es posible, o responder "no" si no hay info.
        // Para Sodio, a veces está en p.nutriments?.sodium_100g, pero no está en tu data class Nutriments.
        // Lo ideal sería añadir Sodio a NutrimentsEntity y a ProductData si la API lo devuelve.
        // Por ahora, la IA se basará en los ingredientes.

        return """
        Eres un asistente de nutrición llamado Yachay. Analiza el siguiente producto alimenticio y responde 
        únicamente con un objeto JSON válido, sin texto introductorio ni explicaciones adicionales.

        Producto: ${producto.nombre}
        Marca: ${producto.marca}
        Descripción: ${producto.descripcion}
        País de Origen: ${producto.paises}
        NutriScore: ${producto.nutriscoreScore}
        Categorías: ${producto.categorias}
        Ingredientes: ${producto.ingredientes}
        Información Nutricional (por 100g):
        $infoNutricional

        Basado en esta información, genera una respuesta JSON con la siguiente estructura exacta:
        {
          "analisis": "(string: un resumen corto y amigable para el usuario sobre si el producto es saludable, 
                      mencionando brevemente por qué)",
          "octogonos": {
            "grasas_saturadas": "(string: 'si' si consideras que es 'Alto en Grasas Saturadas' basado en la info, 
                                'no' si no lo es o no hay datos)",
            "azucar": "(string: 'si' si consideras que es 'Alto en Azúcar' basado en la info, 
                       'no' si no lo es o no hay datos)",
            "sodio": "(string: 'si' si consideras que es 'Alto en Sodio' (busca sal o sodio en ingredientes), 
                      'no' si no lo es o no hay datos)",
            "grasas_trans": "(string: 'si' si contiene ingredientes como 'grasa parcialmente hidrogenada' 
                             u otros indicadores de grasas trans, 'no' si no los tiene)"
          },
          "clasificacion": "(string: da una clasificación simple: 'AD' para productos 100% naturales y recomendados, 
                          'A' para mínimamente procesados y saludables, 'B' para procesados aceptables, 
                          'C' para procesados que se deben consumir con moderación, 'D' para ultra-procesados 
                          no recomendados)"
        }
        """
    }

    private fun parsearRespuestaGemini(
        producto: ProductoEntity,
        jsonString: String
    ): ProductoEntity {
        return try {
            // A veces Gemini envuelve la respuesta en ```json ... ```
            val cleanJson = jsonString
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val jsonObject = JSONObject(cleanJson)
            val analisis = jsonObject.getString("analisis")
            val clasificacion = jsonObject.getString("clasificacion")

            val octogonos = jsonObject.getJSONObject("octogonos")
            val grasasSaturadas = octogonos.getString("grasas_saturadas")
            val azucar = octogonos.getString("azucar")
            val sodio = octogonos.getString("sodio")
            val grasasTrans = octogonos.getString("grasas_trans")

            producto.copy(
                analisisYachay = analisis,
                clasificacionYachay = clasificacion,
                octogonoGrasasSaturadas = grasasSaturadas,
                octogonoAzucar = azucar,
                octogonoSodio = sodio,
                octogonoGrasasTrans = grasasTrans
            )
        } catch (e: Exception) {
            Log.e("GeminiParseError", "Error al parsear JSON de Gemini: ${e.message}")
            producto.copy(analisisYachay = "No se pudo completar el análisis de IA.")
        }
    }

    /*suspend fun obtenerProductoPorCodigo(codigo: String): Producto? = withContext(Dispatchers.IO) {
        val response = api.getProductByCode(codigo)
        if (!response.isSuccessful) return@withContext null

        val productData = response.body()?.product ?: return@withContext null
        val nutriments = productData.nutriments

        val nutrientes = Nutriente(
            carbohidratos = nutriments?.carbohydrates_100g ?: 0.0,
            energia = nutriments?.energy_kcal_100g ?: 0.0,
            grasas = nutriments?.fat_100g ?: 0.0,
            grasasSaturadas = nutriments?.saturated_fat_100g ?: 0.0,
            hidratosCarbono = nutriments?.carbohydrates_100g ?: 0.0,
            azucares = nutriments?.sugars_100g ?: 0.0,
            fibrasAlimentarias = nutriments?.fiber_100g ?: 0.0,
            proteinas = nutriments?.proteins_100g ?: 0.0
        )

        return@withContext Producto(
            codigoProducto = codigo,
            nombreProducto = productData.product_name ?: "Desconocido",
            descripcion = productData.generic_name ?: "",
            clasificacion = "A",
            categorias = productData.categories_tags?.toMutableList() ?: mutableListOf(),
            cantidad = productData.quantity?.filter { it.isDigit() }?.toDoubleOrNull() ?: 0.0,
            empaquetado = productData.packaging ?: "",
            paises = productData.countries ?: "",
            ingredientes = productData.ingredients_text?.split(",")?.map { it.trim() }?.toMutableList() ?: mutableListOf(),
            imagenUrl = productData.image_url ?: "",
            marcas = productData.brands ?: "",
            pais = productData.countries_tags?.firstOrNull() ?: "",
            nutrientes = nutrientes
        )
    }*/

}