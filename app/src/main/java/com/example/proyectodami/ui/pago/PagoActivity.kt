package com.example.proyectodami.ui.pago

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectodami.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PagoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago)

        val total = intent.getDoubleExtra("total_pedido", 0.0)

        val tvResumen = findViewById<TextView>(R.id.tvResumenPago)
        val btnFinalizar = findViewById<Button>(R.id.btnFinalizarPago)

        tvResumen.text = "Total a pagar: S/ %.2f".format(total)

        btnFinalizar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val db = com.example.proyectodami.data.database.AppDatabase.getDatabase(this@PagoActivity)
                db.carritoDao().vaciarCarrito()

                runOnUiThread {
                    val intent = Intent(this@PagoActivity, ConfirmacionActivity::class.java)
                    intent.putExtra("total_pedido", total)
                    startActivity(intent)
                    finish()
                }
            }
        }

    }
}
