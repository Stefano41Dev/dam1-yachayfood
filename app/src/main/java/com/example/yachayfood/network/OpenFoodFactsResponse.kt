package com.example.yachayfood.network

import com.google.gson.annotations.SerializedName

data class OpenFoodFactsResponse(
    val status: Int,
    val product: ProductData?
)

data class ProductData(
    val product_name: String?,
    val generic_name: String?,
    val brands: String?,
    val countries: String?,
    val packaging: String?,
    val quantity: String?,
    val image_url: String?,
    val ingredients_text: String?,
    val nutriments: Nutriments?,
    val categories_tags: List<String>?,
    val countries_tags: List<String>?,
    val nutriscore_score: Int?
)

data class Nutriments(
    @SerializedName("energy-kcal_100g")
    val energy_kcal_100g: Double?,
    @SerializedName("energy_100g")
    val energy_100g: Double?,
    @SerializedName("fat_100g")
    val fat_100g: Double?,
    @SerializedName("saturated-fat_100g")
    val saturated_fat_100g: Double?,
    @SerializedName("sugars_100g")
    val sugars_100g: Double?,
    @SerializedName("proteins_100g")
    val proteins_100g: Double?,
    @SerializedName("carbohydrates_100g")
    val carbohydrates_100g: Double?,
    @SerializedName("fiber_100g")
    val fiber_100g: Double?
)
