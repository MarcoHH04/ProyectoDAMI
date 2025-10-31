package com.example.proyectodami.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectodami.ui.menu.MenuActivity
import com.example.proyectodami.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnMenu = findViewById<Button>(R.id.btnMenu)
        val btnCarrito = findViewById<Button>(R.id.btnCarrito)
        val btnPerfil = findViewById<Button>(R.id.btnPerfil)
        val btnReservas = findViewById<Button>(R.id.btnReservas)
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "Usuario"
        tvBienvenida.text = "¡Bienvenido, $nombreUsuario!"


        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        btnCarrito.setOnClickListener {
            Toast.makeText(this, "Abrir carrito", Toast.LENGTH_SHORT).show()
        }

        btnPerfil.setOnClickListener {
            Toast.makeText(this, "Abrir perfil", Toast.LENGTH_SHORT).show()
        }

        btnReservas.setOnClickListener {
            Toast.makeText(this, "Abrir reservas", Toast.LENGTH_SHORT).show()
        }
    }
}