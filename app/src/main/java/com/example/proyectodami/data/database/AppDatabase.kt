package com.example.proyectodami.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectodami.data.dao.CarritoDao
import com.example.proyectodami.data.dao.ProductoDao
import com.example.proyectodami.data.dao.UsuarioDao
import com.example.proyectodami.data.entity.CarritoItem
import com.example.proyectodami.data.entity.Producto
import com.example.proyectodami.data.entity.Usuario

@Database(entities = [Usuario::class, Producto::class, CarritoItem::class],
    version = 4,
    exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun carritoDao(): CarritoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "goodfood_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}