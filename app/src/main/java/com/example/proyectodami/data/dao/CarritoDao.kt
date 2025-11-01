package com.example.proyectodami.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.proyectodami.data.entity.CarritoItem

@Dao
interface CarritoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarAlCarrito(item: CarritoItem)

    @Update
    suspend fun actualizarItem(item: CarritoItem)

    @Delete
    suspend fun eliminarItem(item: CarritoItem)

    @Query("SELECT * FROM carrito")
    suspend fun obtenerCarrito(): List<CarritoItem>

    @Query("DELETE FROM carrito")
    suspend fun vaciarCarrito()

    @Query("SELECT SUM(precio * cantidad) FROM carrito")
    suspend fun obtenerTotal(): Double?

    @Query("SELECT * FROM carrito WHERE productoId = :id LIMIT 1")
    suspend fun obtenerPorProductoId(id: Int): CarritoItem?
}
