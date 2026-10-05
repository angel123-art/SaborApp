package pe.edu.instituto.espinoza.saborapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MenuActivity : AppCompatActivity() {

    private var idUsuario: Int = 1
    private var nombreUsuario: String = ""
    private var rolUsuario: String = "MOZO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idUsuario = intent.getIntExtra("ID_USUARIO", 1)
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO").orEmpty()
        rolUsuario = intent.getStringExtra("ROL") ?: "MOZO"

        val btnPlatos = findViewById<MaterialButton>(R.id.btnPlatos)
        val btnMesas = findViewById<MaterialButton>(R.id.btnMesas)
        val btnPedidos = findViewById<MaterialButton>(R.id.btnPedidos)
        val btnReportes = findViewById<MaterialButton>(R.id.btnReportes)
        val btnSalir = findViewById<MaterialButton>(R.id.btnSalir)

        // CA4: Si es MOZO, la opciÃ³n Reportes no aparece
        if (rolUsuario == "MOZO") {
            btnReportes.visibility = View.GONE
        } else {
            btnReportes.visibility = View.VISIBLE
        }

        btnPlatos.setOnClickListener {
            startActivity(Intent(this, PlatosActivity::class.java))
        }

        btnMesas.setOnClickListener {
            startActivity(Intent(this, MesasActivity::class.java))
        }

        btnPedidos.setOnClickListener {
            val intent = Intent(this, PedidosActivity::class.java).apply {
                putExtra("ID_USUARIO", idUsuario)
                putExtra("NOMBRE_USUARIO", nombreUsuario)
                putExtra("ROL", rolUsuario)
            }
            startActivity(intent)
        }

        btnReportes.setOnClickListener {
            val intent = Intent(this, ReportesActivity::class.java).apply {
                putExtra("ID_USUARIO", idUsuario)
                putExtra("NOMBRE_USUARIO", nombreUsuario)
                putExtra("ROL", rolUsuario)
            }
            startActivity(intent)
        }

        btnSalir.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Cerrar sesiÃ³n")
                .setMessage("Â¿Deseas cerrar sesiÃ³n?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Salir") { _, _ ->
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
                .show()
        }
    }
}
