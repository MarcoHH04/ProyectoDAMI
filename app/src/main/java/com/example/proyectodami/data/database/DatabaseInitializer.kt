package com.example.proyectodami.data.database

import android.content.Context
import com.example.proyectodami.R
import com.example.proyectodami.data.entity.Producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseInitializer {

    fun llenarDatosIniciales(context: Context) {
        val db = AppDatabase.getDatabase(context)
        val productoDao = db.productoDao()

        CoroutineScope(Dispatchers.IO).launch {
            val cantidad = productoDao.obtenerTodos().size
            if (cantidad == 0) {
                val productosIniciales = listOf(
                    Producto(nombre = "Ceviche", descripcion = "Fresco ceviche de pescado con limón y cebolla roja.", precio = 22.0, categoria = "Entradas", imagen = R.drawable.ic_ceviche),
                    Producto(nombre = "Lomo Saltado", descripcion = "Clásico plato peruano con carne, papas y arroz.", precio = 25.0, categoria = "Platos principales", imagen = R.drawable.ic_lomo),
                    Producto(nombre = "Suspiro Limeño", descripcion = "Tradicional postre peruano de manjar blanco y merengue.", precio = 15.0, categoria = "Postres", imagen = R.drawable.ic_suspiro),
                    Producto(nombre = "Chicha Morada", descripcion = "Refrescante bebida de maíz morado con frutas.", precio = 8.0, categoria = "Bebidas", imagen = R.drawable.ic_chicha)
                )
                productoDao.insertarProductos(productosIniciales)
            }
        }
    }
}