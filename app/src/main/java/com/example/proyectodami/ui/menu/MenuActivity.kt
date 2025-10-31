package com.example.proyectodami.ui.menu

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.ImageView
import com.example.proyectodami.R

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val contenedorPlatos = findViewById<LinearLayout>(R.id.contenedorPlatos)

        // Datos simulados (por ahora)
        val listaPlatos = listOf(
            Plato("Lomo Saltado", "Clásico plato peruano con carne, papas y arroz.", "S/ 25.00", R.drawable.ic_launcher_background),
            Plato("Ceviche", "Pescado fresco marinado en limón.", "S/ 22.00", R.drawable.ic_launcher_background),
            Plato("Arroz Chaufa", "Arroz frito estilo oriental con pollo.", "S/ 20.00", R.drawable.ic_launcher_background),
            Plato("Tallarines Verdes", "Pasta con salsa de albahaca y queso.", "S/ 18.00", R.drawable.ic_launcher_background)
        )

        for (plato in listaPlatos) {
            val cardView = layoutInflater.inflate(R.layout.item_plato, contenedorPlatos, false) as CardView

            val imgPlato = cardView.findViewById<ImageView>(R.id.imgPlato)
            val tvNombre = cardView.findViewById<TextView>(R.id.tvNombrePlato)
            val tvDescripcion = cardView.findViewById<TextView>(R.id.tvDescripcionPlato)
            val tvPrecio = cardView.findViewById<TextView>(R.id.tvPrecioPlato)
            val btnAgregar = cardView.findViewById<Button>(R.id.btnAgregarPlato)

            imgPlato.setImageResource(plato.imagen)
            tvNombre.text = plato.nombre
            tvDescripcion.text = plato.descripcion
            tvPrecio.text = plato.precio

            btnAgregar.setOnClickListener {
                Toast.makeText(this, "${plato.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
            }

            contenedorPlatos.addView(cardView)
        }
    }
}

data class Plato(
    val nombre: String,
    val descripcion: String,
    val precio: String,
    val imagen: Int
)
