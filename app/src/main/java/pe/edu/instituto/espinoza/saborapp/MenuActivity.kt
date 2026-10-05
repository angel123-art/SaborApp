package pe.edu.instituto.espinoza.saborapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MenuActivity : AppCompatActivity() {

    // =====================================================
    // USUARIO LOGUEADO
    // =====================================================

    private var idUsuario: Int = 1
    private var nombreUsuario: String = ""
    private var rolUsuario: String = "MOZO"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_menu)


        // =================================================
        // AJUSTAR BARRAS DEL SISTEMA
        // =================================================

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }


        // =================================================
        // RECIBIR DATOS DEL LOGIN
        // =================================================

        idUsuario =
            intent.getIntExtra(
                "ID_USUARIO",
                1
            )

        nombreUsuario =
            intent.getStringExtra(
                "NOMBRE_USUARIO"
            ).orEmpty()

        rolUsuario =
            intent.getStringExtra(
                "ROL"
            ) ?: "MOZO"


        // =================================================
        // PLATOS
        // =================================================

        val btnPlatos =
            findViewById<MaterialButton>(
                R.id.btnPlatos
            )

        btnPlatos.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    PlatosActivity::class.java
                )
            )
        }


        // =================================================
        // MESAS
        // =================================================

        val btnMesas =
            findViewById<MaterialButton>(
                R.id.btnMesas
            )

        btnMesas.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MesasActivity::class.java
                )
            )
        }


        // =================================================
        // PEDIDOS
        // =================================================

        val btnPedidos =
            findViewById<MaterialButton>(
                R.id.btnPedidos
            )

        btnPedidos.setOnClickListener {

            val intent =
                Intent(
                    this,
                    PedidosActivity::class.java
                )

            intent.putExtra(
                "ID_USUARIO",
                idUsuario
            )

            intent.putExtra(
                "NOMBRE_USUARIO",
                nombreUsuario
            )

            intent.putExtra(
                "ROL",
                rolUsuario
            )

            startActivity(intent)
        }


        // =================================================
        // REPORTES
        // =================================================

        val btnReportes =
            findViewById<MaterialButton>(
                R.id.btnReportes
            )

        btnReportes.setOnClickListener {

            val intent =
                Intent(
                    this,
                    ReportesActivity::class.java
                )

            intent.putExtra(
                "ID_USUARIO",
                idUsuario
            )

            intent.putExtra(
                "NOMBRE_USUARIO",
                nombreUsuario
            )

            intent.putExtra(
                "ROL",
                rolUsuario
            )

            startActivity(intent)
        }


        // =================================================
        // SALIR
        // =================================================

        val btnSalir =
            findViewById<MaterialButton>(
                R.id.btnSalir
            )

        btnSalir.setOnClickListener {

            mostrarDialogoSalir()
        }
    }


    // =====================================================
    // CONFIRMAR SALIDA
    // =====================================================

    private fun mostrarDialogoSalir() {

        MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar sesión")
            .setMessage(
                "¿Deseas cerrar la sesión de $nombreUsuario?"
            )
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton(
                "Salir"
            ) { _, _ ->

                cerrarSesion()
            }
            .show()
    }


    // =====================================================
    // CERRAR SESIÓN
    // =====================================================

    private fun cerrarSesion() {

        val intent =
            Intent(
                this,
                LoginActivity::class.java
            )

        // Evita volver al menú con el botón atrás
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        finish()
    }
}