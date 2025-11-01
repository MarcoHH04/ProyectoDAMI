package com.example.proyectodami.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectodami.data.dao.ProductoDao
import com.example.proyectodami.data.dao.UsuarioDao
import com.example.proyectodami.data.entity.Producto
import com.example.proyectodami.data.entity.Usuario

@Database(entities = [Usuario::class, Producto::class],
    version = 2,
    exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao

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