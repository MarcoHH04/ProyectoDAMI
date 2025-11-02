package com.example.proyectodami.ui.carrito

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.proyectodami.R
import com.example.proyectodami.data.database.AppDatabase
import com.example.proyectodami.data.entity.CarritoItem
import com.example.proyectodami.ui.pago.PagoActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CarritoActivity : AppCompatActivity() {

    private lateinit var contenedorCarrito: LinearLayout
    private lateinit var tvTotal: TextView
    private lateinit var btnVaciar: Button
    private lateinit var btnConfirmar: Button

    private val db by lazy { AppDatabase.getDatabase(this) }
    private val carritoDao by lazy { db.carritoDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        contenedorCarrito = findViewById(R.id.contenedorCarrito)
        tvTotal = findViewById(R.id.tvTotal)
        btnVaciar = findViewById(R.id.btnVaciarCarrito)
        btnConfirmar = findViewById(R.id.btnConfirmar)

        cargarCarrito()

        btnVaciar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                carritoDao.vaciarCarrito()
                runOnUiThread {
                    Toast.makeText(this@CarritoActivity, "Carrito vaciado", Toast.LENGTH_SHORT).show()
                    cargarCarrito()
                }
            }
        }

        btnConfirmar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val total = carritoDao.obtenerTotal() ?: 0.0
                runOnUiThread {
                    val intent = Intent(this@CarritoActivity, PagoActivity::class.java)
                    intent.putExtra("total_pedido", total)
                    startActivity(intent)
                }
            }
        }
    }

    private fun cargarCarrito() {
        CoroutineScope(Dispatchers.IO).launch {
            val lista = carritoDao.obtenerCarrito()
            val total = carritoDao.obtenerTotal() ?: 0.0

            runOnUiThread {
                contenedorCarrito.removeAllViews()
                tvTotal.text = "Total: S/ %.2f".format(total)

                if (lista.isEmpty()) {
                    val vacio = TextView(this@CarritoActivity).apply {
                        text = "Tu carrito está vacío"
                        textSize = 18f
                        setTextColor(getColor(R.color.black))
                        gravity = android.view.Gravity.CENTER
                        setPadding(8, 64, 8, 64)
                    }
                    contenedorCarrito.addView(vacio)
                } else {
                    for (item in lista) {
                        val card = layoutInflater.inflate(
                            R.layout.item_carrito, contenedorCarrito, false
                        ) as CardView

                        val img = card.findViewById<ImageView>(R.id.imgCarrito)
                        val tvNombre = card.findViewById<TextView>(R.id.tvNombreCarrito)
                        val tvPrecio = card.findViewById<TextView>(R.id.tvPrecioCarrito)
                        val tvCantidad = card.findViewById<TextView>(R.id.tvCantidad)
                        val btnMas = card.findViewById<Button>(R.id.btnMas)
                        val btnMenos = card.findViewById<Button>(R.id.btnMenos)
                        val btnEliminar = card.findViewById<Button>(R.id.btnEliminar)

                        tvNombre.text = item.nombre
                        tvPrecio.text = "S/ %.2f".format(item.precio)
                        tvCantidad.text = item.cantidad.toString()

                        // 🔹 Nuevo método para obtener imagen desde el nombre (String)
                        val resId = resources.getIdentifier(item.imagen, "drawable", packageName)
                        if (resId != 0) {
                            img.setImageResource(resId)
                        } else {
                            img.setImageResource(R.drawable.ic_placeholder) // una imagen genérica
                        }

                        btnMas.setOnClickListener {
                            actualizarCantidad(item, item.cantidad + 1)
                        }

                        btnMenos.setOnClickListener {
                            if (item.cantidad > 1) {
                                actualizarCantidad(item, item.cantidad - 1)
                            } else {
                                eliminarItem(item)
                            }
                        }

                        btnEliminar.setOnClickListener {
                            eliminarItem(item)
                        }

                        contenedorCarrito.addView(card)
                    }
                }
            }
        }
    }

    private fun actualizarCantidad(item: CarritoItem, nuevaCantidad: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            carritoDao.actualizarItem(item.copy(cantidad = nuevaCantidad))
            runOnUiThread { cargarCarrito() }
        }
    }

    private fun eliminarItem(item: CarritoItem) {
        CoroutineScope(Dispatchers.IO).launch {
            carritoDao.eliminarItem(item)
            runOnUiThread {
                Toast.makeText(this@CarritoActivity, "${item.nombre} eliminado", Toast.LENGTH_SHORT).show()
                cargarCarrito()
            }
        }
    }
}
