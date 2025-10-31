package com.example.proyectodami.ui.register

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectodami.R
import com.example.proyectodami.data.database.AppDatabase
import com.example.proyectodami.data.entities.Usuario
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = AppDatabase.Companion.getDatabase(this)

        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val tietNombre = findViewById<EditText>(R.id.tietNombre)
        val tietApellido = findViewById<EditText>(R.id.tietApellido)
        val tietDNI = findViewById<EditText>(R.id.tietDNI)
        val tietCelular = findViewById<EditText>(R.id.tietCelular)
        val tietCorreo = findViewById<EditText>(R.id.tietCorreo)
        val tietContrasena = findViewById<EditText>(R.id.tietContrasena)
        val tietConfirmar = findViewById<EditText>(R.id.tietConfirmarContrasena)

        btnRegistrar.setOnClickListener {
            val nombre = tietNombre.text.toString().trim()
            val apellido = tietApellido.text.toString().trim()
            val dni = tietDNI.text.toString().trim()
            val celular = tietCelular.text.toString().trim()
            val correo = tietCorreo.text.toString().trim()
            val contrasena = tietContrasena.text.toString().trim()
            val confirmar = tietConfirmar.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || celular.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val usuarioExistente = db.usuarioDao().obtenerPorCorreo(correo)
                if (usuarioExistente != null) {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Ya existe un usuario con ese correo", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val nuevoUsuario = Usuario(
                        nombre = nombre,
                        apellido = apellido,
                        dni = dni,
                        celular = celular,
                        correo = correo,
                        contrasena = contrasena
                    )
                    db.usuarioDao().registrarUsuario(nuevoUsuario)
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}