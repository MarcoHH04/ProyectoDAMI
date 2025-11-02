package com.example.proyectodami

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectodami.data.database.AppDatabase
import com.example.proyectodami.data.entity.Reserva
import com.example.proyectodami.databinding.ActivityReservaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ReservaActivity : AppCompatActivity() {

    private var _binding: ActivityReservaBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        _binding = ActivityReservaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // insets seguro
        ViewCompat.setOnApplyWindowInsetsListener(binding.reserva) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        db = AppDatabase.getDatabase(this)

        // restricciones básicas: personas max 10 por ejemplo, telefono maxLength 9
        binding.tietPersonas.filters = arrayOf(InputFilter.LengthFilter(2))
        // Si usas InputFilter para teléfono:
        binding.tietTelefonoReserva.filters = arrayOf(InputFilter.LengthFilter(9))

        // pickers
        binding.tietFecha.setOnClickListener { showDatePicker() }
        binding.tietFecha.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) showDatePicker() }

        binding.tietHora.setOnClickListener { showTimePicker() }
        binding.tietHora.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) showTimePicker() }

        // botón reservar
        binding.btnReservar.setOnClickListener {
            attemptSaveReservation()
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val dpd = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val mm = (month + 1).toString().padStart(2, '0')
            val dd = dayOfMonth.toString().padStart(2, '0')
            binding.tietFecha.setText("$dd/$mm/$year")
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))

        // impedir fechas pasadas
        dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        val tpd = TimePickerDialog(this, { _, hourOfDay, minute ->
            val hh = hourOfDay.toString().padStart(2, '0')
            val mm = minute.toString().padStart(2, '0')
            binding.tietHora.setText("$hh:$mm")
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
        tpd.show()
    }

    private fun attemptSaveReservation() {
        // reset errores
        binding.tietNombreReserva.error = null
        binding.tietTelefonoReserva.error = null
        binding.tietCorreoReserva.error = null
        binding.tietFecha.error = null
        binding.tietHora.error = null
        binding.tietPersonas.error = null

        val nombre = binding.tietNombreReserva.text?.toString()?.trim() ?: ""
        val telefono = binding.tietTelefonoReserva.text?.toString()?.trim() ?: ""
        val correo = binding.tietCorreoReserva.text?.toString()?.trim() ?: ""
        val fecha = binding.tietFecha.text?.toString()?.trim() ?: ""
        val hora = binding.tietHora.text?.toString()?.trim() ?: ""
        val personasStr = binding.tietPersonas.text?.toString()?.trim() ?: ""
        val observaciones = binding.tietObservaciones.text?.toString()?.trim()?.ifEmpty { null }

        var ok = true

        // Validaciones
        if (nombre.isEmpty()) {
            binding.tietNombreReserva.error = "Ingrese nombre"
            ok = false
        }
        if (telefono.isEmpty()) {
            binding.tietTelefonoReserva.error = "Ingrese teléfono"
            ok = false
        } else if (telefono.length != 9) {
            binding.tietTelefonoReserva.error = "Teléfono debe tener 9 dígitos"
            ok = false
        }
        if (correo.isEmpty()) {
            binding.tietCorreoReserva.error = "Ingrese correo"
            ok = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.tietCorreoReserva.error = "Correo inválido"
            ok = false
        }
        if (fecha.isEmpty()) {
            binding.tietFecha.error = "Selecciona fecha"
            ok = false
        }
        if (hora.isEmpty()) {
            binding.tietHora.error = "Selecciona hora"
            ok = false
        }
        val personas = personasStr.toIntOrNull() ?: -1
        if (personas <= 0) {
            binding.tietPersonas.error = "Ingresa número válido de personas"
            ok = false
        } else if (personas > 20) { // regla de negocio ejemplo
            binding.tietPersonas.error = "Máximo 20 personas por reserva"
            ok = false
        }

        if (!ok) {
            // opcional: mostrar un toast resumen
            Toast.makeText(this, "Corrige los campos resaltados", Toast.LENGTH_SHORT).show()
            return
        }

        // deshabilitar botón para evitar doble pulsación
        binding.btnReservar.isEnabled = false

        // Guardar en Room en background
        lifecycleScope.launch(Dispatchers.IO) {
            val reserva = Reserva(
                nombre = nombre,
                telefono = telefono,
                correo = correo,
                fecha = fecha,
                hora = hora,
                personas = personas,
                observaciones = observaciones
            )
            val id = db.reservaDao().insertarReserva(reserva)

            withContext(Dispatchers.Main) {
                binding.btnReservar.isEnabled = true
                if (id > 0) {
                    Toast.makeText(this@ReservaActivity, "Reserva confirmada", Toast.LENGTH_LONG).show()
                    // opcional: limpiar campos
                    clearForm()
                } else {
                    Toast.makeText(this@ReservaActivity, "Error al guardar reserva", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearForm() {
        binding.tietNombreReserva.text?.clear()
        binding.tietTelefonoReserva.text?.clear()
        binding.tietCorreoReserva.text?.clear()
        binding.tietFecha.text?.clear()
        binding.tietHora.text?.clear()
        binding.tietPersonas.text?.clear()
        binding.tietObservaciones.text?.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
