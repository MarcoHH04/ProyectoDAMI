package com.example.proyectodami

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvRegistro = findViewById<TextView>(R.id.tvRegistro)
        val db = AppDatabase.getDatabase(this)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tietCorreo = findViewById<EditText>(R.id.tietCorreo)
        val tietClave = findViewById<EditText>(R.id.tietClave)

        tvRegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val correo = tietCorreo.text.toString().trim()
            val clave = tietClave.text.toString().trim()

            if (correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val usuario = db.usuarioDao().validarLogin(correo, clave)
                runOnUiThread {
                    if (usuario != null) {
                        Toast.makeText(
                            this@MainActivity,
                            "Bienvenido ${usuario.nombre}",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        intent.putExtra("nombreUsuario", usuario.nombre)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Correo o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}