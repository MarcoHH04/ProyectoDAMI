package com.example.proyectodami.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectodami.data.entity.Producto

@Dao
interface ProductoDao {

    @Insert
    suspend fun insertarProducto(producto: Producto)

    @Insert
    suspend fun insertarProductos(productos: List<Producto>)

    @Query("SELECT * FROM productos")
    suspend fun obtenerTodos(): List<Producto>

    @Query("SELECT * FROM productos WHERE categoria = :categoria")
    suspend fun obtenerPorCategoria(categoria: String): List<Producto>

    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :nombre || '%'")
    suspend fun buscarPorNombre(nombre: String): List<Producto>

    @Query("DELETE FROM productos")
    suspend fun eliminarTodos()
}