package com.example.proyectodami.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reserva")
data class Reserva(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val telefono: String,
    val correo: String,
    val fecha: String,   // formato "dd/MM/yyyy"
    val hora: String,    // formato "HH:mm"
    val personas: Int,
    val observaciones: String?
)