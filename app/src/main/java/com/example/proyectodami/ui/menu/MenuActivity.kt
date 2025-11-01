package com.example.proyectodami.ui.menu

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.proyectodami.R
import com.example.proyectodami.data.database.AppDatabase
import com.example.proyectodami.data.database.DatabaseInitializer
import com.example.proyectodami.data.entity.Producto
import com.example.proyectodami.ui.carrito.CarritoActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {

    private lateinit var contenedorPlatos: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        contenedorPlatos = findViewById(R.id.contenedorPlatos)

        val db = AppDatabase.getDatabase(this)
        val productoDao = db.productoDao()

        val fabCarrito = findViewById<FloatingActionButton>(R.id.fabCarrito)
        fabCarrito.setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            startActivity(intent)
        }

        //limpiar el carrito cada vez que se abre el menú
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@MenuActivity).carritoDao().vaciarCarrito()
        }
        // hasta aqui seria el codigo que borra el carrito

        DatabaseInitializer.llenarDatosIniciales(this)

        CoroutineScope(Dispatchers.IO).launch {
            val listaProductos = productoDao.obtenerTodos()
            runOnUiThread {
                mostrarProductos(listaProductos)
            }
        }
    }

    private fun mostrarProductos(lista: List<Producto>) {
        contenedorPlatos.removeAllViews()

        for (producto in lista) {
            val cardView = layoutInflater.inflate(R.layout.item_producto, contenedorPlatos, false) as CardView

            val imgPlato = cardView.findViewById<ImageView>(R.id.imgProducto)
            val tvNombre = cardView.findViewById<TextView>(R.id.tvNombre)
            val tvDescripcion = cardView.findViewById<TextView>(R.id.tvDescripcion)
            val tvPrecio = cardView.findViewById<TextView>(R.id.tvPrecio)
            val btnAgregar = cardView.findViewById<Button>(R.id.btnAgregar)

            tvNombre.text = producto.nombre
            tvDescripcion.text = producto.descripcion
            tvPrecio.text = "S/ %.2f".format(producto.precio)
            imgPlato.setImageResource(producto.imagen)

            btnAgregar.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val carritoDao = AppDatabase.getDatabase(this@MenuActivity).carritoDao()

                    val carritoActual = carritoDao.obtenerCarrito()
                    val itemExistente = carritoActual.find { it.productoId == producto.id }

                    if (itemExistente != null) {
                        val actualizado = itemExistente.copy(cantidad = itemExistente.cantidad + 1)
                        carritoDao.actualizarItem(actualizado)
                    } else {
                        val nuevoItem = com.example.proyectodami.data.entity.CarritoItem(
                            productoId = producto.id,
                            nombre = producto.nombre,
                            precio = producto.precio,
                            cantidad = 1,
                            imagen = producto.imagen // 👈 agregado
                        )
                        carritoDao.agregarAlCarrito(nuevoItem)
                    }

                    runOnUiThread {
                        Toast.makeText(
                            this@MenuActivity,
                            "${producto.nombre} agregado al carrito",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            contenedorPlatos.addView(cardView)
        }
    }
}
