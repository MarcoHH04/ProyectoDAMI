package com.example.proyectodami.ui.pago

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectodami.R
import com.example.proyectodami.ui.home.HomeActivity

class ConfirmacionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacion)

        val tvMensaje = findViewById<TextView>(R.id.tvMensajeConfirmacion)
        val btnVolver = findViewById<Button>(R.id.btnVolverHome)

        val total = intent.getDoubleExtra("total_pedido", 0.0)

        tvMensaje.text = "¡Gracias por tu compra!\nTu pedido por S/ %.2f ha sido registrado con éxito.".format(total)

        btnVolver.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
