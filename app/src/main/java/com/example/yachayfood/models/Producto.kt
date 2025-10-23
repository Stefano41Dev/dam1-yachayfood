package com.example.yachayfood.models

data class Producto (
    val codigoProducto: String,
    val nombreProducto: String,
    val descripcion: String,
    val clasificacion: Char,
    val categorias: MutableList<String> = mutableListOf<String>(),
    val cantidad: Double,
    val empaquetado: String,
    val paises: String,
    val ingredientes: MutableList<String> = mutableListOf<String>(),
    val imagenUrl: String,
    val marcas: String,
    val pais: String,
    val nutrientes : Nutriente
)