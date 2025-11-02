package com.example.proyectodami

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectodami.data.database.AppDatabase
import com.example.proyectodami.data.entity.Usuario
import com.example.proyectodami.databinding.ActivityPerfilBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilActivity : AppCompatActivity() {

    private var _binding: ActivityPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private var usuarioActual: Usuario? = null

    companion object {
        const val EXTRA_CORREO = "correo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        _binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aplicar insets de forma segura
        ViewCompat.setOnApplyWindowInsetsListener(binding.perfil) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // DB
        db = AppDatabase.getDatabase(this)

        // Por defecto campos deshabilitados
        setFieldsEditable(false)

        // Cargar usuario: preferimos correo pasado por Intent; si no, cargamos el primero disponible
        val correoIntent = intent.getStringExtra(EXTRA_CORREO)
        lifecycleScope.launch(Dispatchers.IO) {
            usuarioActual = if (!correoIntent.isNullOrBlank()) {
                db.usuarioDao().obtenerPorCorreo(correoIntent)
            } else {
                db.usuarioDao().obtenerPrimero()
            }

            withContext(Dispatchers.Main) {
                if (usuarioActual != null) {
                    showUser(usuarioActual!!)
                } else {
                    Toast.makeText(this@PerfilActivity, "No se encontró usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Botón editar/guardar
        binding.btnEdicion.setOnClickListener {
            if (binding.btnEdicion.text == "Editar Perfil") {
                setFieldsEditable(true)
                binding.btnEdicion.text = "Guardar"
            } else {
                // Intentar guardar
                lifecycleScope.launch(Dispatchers.IO) {
                    val valid = validateInputsAndUpdateUsuario()
                    withContext(Dispatchers.Main) {
                        if (valid) {
                            setFieldsEditable(false)
                            binding.btnEdicion.text = "Editar Perfil"
                            Toast.makeText(this@PerfilActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showUser(u: Usuario) {
        binding.tietNombre.setText(u.nombre)
        binding.tietApellido.setText(u.apellido)
        binding.tietDNI.setText(u.dni)
        binding.tietCelular.setText(u.celular)
        binding.tietCorreo.setText(u.correo)
        // si guardas contraseña no la muestres en claro; por simplicidad no se carga aquí
    }

    private fun setFieldsEditable(editable: Boolean) {
        binding.tietNombre.isEnabled = editable
        binding.tietApellido.isEnabled = editable
        binding.tietDNI.isEnabled = editable
        binding.tietCelular.isEnabled = editable
        binding.tietCorreo.isEnabled = editable
        if (editable) binding.tietNombre.requestFocus()
    }

    /**
     * Lee campos, valida y actualiza usuarioActual en DB.
     * Devuelve true si la actualización fue exitosa.
     */
    private suspend fun validateInputsAndUpdateUsuario(): Boolean = withContext(Dispatchers.IO) {
        val nombre = binding.tietNombre.text?.toString()?.trim() ?: ""
        val apellido = binding.tietApellido.text?.toString()?.trim() ?: ""
        val dni = binding.tietDNI.text?.toString()?.trim() ?: ""
        val celular = binding.tietCelular.text?.toString()?.trim() ?: ""
        val correo = binding.tietCorreo.text?.toString()?.trim() ?: ""

        // Validaciones simples (puedes adaptarlas)
        var ok = true
        withContext(Dispatchers.Main) {
            binding.tietNombre.error = null
            binding.tietApellido.error = null
            binding.tietDNI.error = null
            binding.tietCelular.error = null
            binding.tietCorreo.error = null
        }

        if (nombre.isEmpty()) {
            withContext(Dispatchers.Main) { binding.tietNombre.error = "Ingrese nombre" }
            ok = false
        }
        if (apellido.isEmpty()) {
            withContext(Dispatchers.Main) { binding.tietApellido.error = "Ingrese apellido" }
            ok = false
        }
        if (dni.isNotEmpty() && dni.length != 8) {
            withContext(Dispatchers.Main) { binding.tietDNI.error = "DNI debe tener 8 dígitos" }
            ok = false
        }
        if (celular.isNotEmpty() && celular.length != 9) {
            withContext(Dispatchers.Main) { binding.tietCelular.error = "Celular debe tener 9 dígitos" }
            ok = false
        }
        if (correo.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            withContext(Dispatchers.Main) { binding.tietCorreo.error = "Email inválido" }
            ok = false
        }

        if (!ok) return@withContext false

        // Si usuarioActual es null, podemos crear uno nuevo o fallar. Mejor falla y avisa.
        val u = usuarioActual
        if (u == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@PerfilActivity, "No hay usuario para actualizar", Toast.LENGTH_SHORT).show()
            }
            return@withContext false
        }

        // actualizar campos (mantener id y contraseña)
        val updated = u.copy(
            nombre = nombre,
            apellido = apellido,
            dni = dni,
            celular = celular,
            correo = correo
        )

        // Llamar a DAO @Update
        db.usuarioDao().actualizarUsuario(updated)

        // actualizar referencia en memoria
        usuarioActual = updated
        return@withContext true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
