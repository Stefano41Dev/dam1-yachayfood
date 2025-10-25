package com.example.yachayfood.models

import java.io.Serializable

data class Nutriente(
    val energia: Double,
    val grasas: Double,
    val grasasSaturadas: Double,
    val azucares: Double,
    val proteinas: Double,
    val carbohidratos: Double,
    val hidratosCarbono: Double,
    val fibrasAlimentarias: Double
) : Serializable