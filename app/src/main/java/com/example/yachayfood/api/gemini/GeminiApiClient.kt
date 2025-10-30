package com.example.yachayfood.api.gemini

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiApiClient {

    private const val GEMINI_API_KEY = "AIzaSyDz6WY3yqyFkfQ5YLX8imPYk2hIyldnrgs"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash-lite",
        apiKey = GEMINI_API_KEY
    )

    suspend fun obtenerAnalisisProducto(prompt: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(prompt)
                response.text
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}