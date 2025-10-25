package com.example.yachayfood.network

import com.example.yachayfood.api.OpenFoodFactsResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApi {
    @GET("api/v0/product/{code}.json")
    suspend fun getProductByCode(@Path("code") code: String): Response<OpenFoodFactsResponse>
}