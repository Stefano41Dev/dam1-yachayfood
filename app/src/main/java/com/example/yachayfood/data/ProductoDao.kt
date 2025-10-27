package com.example.yachayfood.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yachayfood.models.ProductoEntity

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertarProducto(producto: ProductoEntity)

    @Query("SELECT * FROM productos ORDER BY fechaEscaneo DESC LIMIT 3")
    suspend fun obtenerRecientes(): List<ProductoEntity>

    @Query("SELECT * FROM productos ORDER BY fechaEscaneo DESC")
    suspend fun obtenerTodos(): List<ProductoEntity>

    // Devuelve todos los productos con el mismo código
    @Query("SELECT * FROM productos WHERE codigo = :codigo")
    suspend fun obtenerProductosPorCodigo(codigo: String): List<ProductoEntity>

    // Opcional: solo el primero
    @Query("SELECT * FROM productos WHERE TRIM(codigo) = :codigo LIMIT 1")
    suspend fun obtenerProductoPorCodigo(codigo: String): ProductoEntity?

    @Query("SELECT * FROM productos ORDER BY fechaEscaneo DESC LIMIT 3")
    suspend fun obtenerUltimosTres(): List<ProductoEntity>

    @Query("SELECT * FROM productos ORDER BY nombre ASC") // Ordena alfabéticamente
    suspend fun getAllProductos(): List<ProductoEntity>

    @Query("UPDATE productos SET fechaEscaneo = :nuevaFecha WHERE codigo = :codigo")
    suspend fun actualizarFechaEscaneo(codigo: String, nuevaFecha: Long)
}