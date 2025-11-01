package com.example.proyectodami.ui.menu

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
                Toast.makeText(this, "${producto.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
            }

            contenedorPlatos.addView(cardView)
        }
    }
}
