package com.example.proyectodami.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectodami.data.entity.Reserva

@Dao
interface ReservaDao {
    @Insert
    suspend fun insertarReserva(reserva: Reserva): Long

    @Query("SELECT * FROM reserva ORDER BY id DESC")
    suspend fun obtenerReservas(): List<Reserva>

    @Query("DELETE FROM reserva WHERE id = :id")
    suspend fun eliminarReserva(id: Long)
}
