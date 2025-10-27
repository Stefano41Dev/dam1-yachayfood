package com.example.yachayfood.api

import com.example.yachayfood.network.OpenFoodFactsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApi {
    @GET("api/v0/product/{code}.json")
    suspend fun getProductByCode(@Path("code") code: String): Response<OpenFoodFactsResponse>
}