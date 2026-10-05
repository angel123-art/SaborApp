package pe.edu.instituto.espinoza.saborapp

import android.os.Bundle
import android.widget.TextView

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.google.android.material.button.MaterialButton

import pe.edu.instituto.espinoza.saborapp.data.DBHelper


class ReportesActivity : AppCompatActivity() {

    // =====================================================
    // BASE DE DATOS
    // =====================================================

    private lateinit var dbHelper: DBHelper


    // =====================================================
    // USUARIO
    // =====================================================

    private var idUsuario: Int = 1

    private var nombreUsuario: String = ""

    private var rolUsuario: String = "MOZO"


    // =====================================================
    // VISTAS
    // =====================================================

    private lateinit var tvTituloReportes: TextView

    private lateinit var tvPedidosCerrados: TextView

    private lateinit var tvVentaDia: TextView

    private lateinit var tvTopPlatos: TextView

    private lateinit var tvVentasMesas: TextView

    private lateinit var btnRegresarReportes: MaterialButton


    // =====================================================
    // ON CREATE
    // =====================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        enableEdgeToEdge()


        setContentView(
            R.layout.activity_reportes
        )


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
        // BASE DE DATOS
        // =================================================

        dbHelper =
            DBHelper(this)


        // =================================================
        // RECIBIR USUARIO
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
        // VISTAS
        // =================================================

        tvTituloReportes =
            findViewById(
                R.id.tvTituloReportes
            )


        tvPedidosCerrados =
            findViewById(
                R.id.tvPedidosCerrados
            )


        tvVentaDia =
            findViewById(
                R.id.tvVentaDia
            )


        tvTopPlatos =
            findViewById(
                R.id.tvTopPlatos
            )


        tvVentasMesas =
            findViewById(
                R.id.tvVentasMesas
            )


        btnRegresarReportes =
            findViewById(
                R.id.btnRegresarReportes
            )


        // =================================================
        // TÍTULO
        // =================================================

        if (rolUsuario == "ADMIN") {

            tvTituloReportes.text =
                "Reportes de ventas"

        } else {

            tvTituloReportes.text =
                "Mis reportes de ventas"
        }


        // =================================================
        // CARGAR REPORTES
        // =================================================

        cargarReportes()


        // =================================================
        // REGRESAR
        // =================================================

        btnRegresarReportes.setOnClickListener {

            finish()
        }
    }


    // =====================================================
    // CARGAR REPORTES
    // =====================================================

    private fun cargarReportes() {

        // =================================================
        // ADMIN
        // =================================================

        if (
            rolUsuario.equals(
                "ADMIN",
                ignoreCase = true
            )
        ) {

            cargarReportesAdmin()

        } else {

            // =================================================
            // MOZO
            // =================================================

            cargarReportesMozo()
        }
    }


    // =====================================================
    // REPORTES ADMIN
    // =====================================================

    private fun cargarReportesAdmin() {

        val venta =
            dbHelper.ventaDelDia()


        val cantidadPedidos =
            venta.first


        val totalVentas =
            venta.second


        tvPedidosCerrados.text =
            "Pedidos cerrados: $cantidadPedidos"


        tvVentaDia.text =
            "Total vendido: S/ %.2f"
                .format(
                    totalVentas
                )


        // =================================================
        // TOP PLATOS
        // =================================================

        val topPlatos =
            dbHelper.topPlatos()


        if (
            topPlatos.isEmpty()
        ) {

            tvTopPlatos.text =
                "Sin ventas hoy"

        } else {

            tvTopPlatos.text =
                topPlatos.joinToString(
                    "\n"
                )
        }


        // =================================================
        // VENTAS POR MESA
        // =================================================

        val ventasMesas =
            dbHelper.ventaPorMesa()


        if (
            ventasMesas.isEmpty()
        ) {

            tvVentasMesas.text =
                "Sin ventas hoy"

        } else {

            tvVentasMesas.text =
                ventasMesas.joinToString(
                    "\n"
                )
        }
    }


    // =====================================================
    // REPORTES MOZO
    // =====================================================

    private fun cargarReportesMozo() {

        val venta =
            dbHelper.ventaDelDiaPorUsuario(
                idUsuario
            )


        val cantidadPedidos =
            venta.first


        val totalVentas =
            venta.second


        tvPedidosCerrados.text =
            "Pedidos cerrados: $cantidadPedidos"


        tvVentaDia.text =
            "Total vendido: S/ %.2f"
                .format(
                    totalVentas
                )


        // =================================================
        // TOP PLATOS DEL MOZO
        // =================================================

        val topPlatos =
            dbHelper.topPlatosPorUsuario(
                idUsuario
            )


        if (
            topPlatos.isEmpty()
        ) {

            tvTopPlatos.text =
                "Sin ventas hoy"

        } else {

            tvTopPlatos.text =
                topPlatos.joinToString(
                    "\n"
                )
        }


        // =================================================
        // VENTAS POR MESA DEL MOZO
        // =================================================

        val ventasMesas =
            dbHelper.ventaPorMesaPorUsuario(
                idUsuario
            )


        if (
            ventasMesas.isEmpty()
        ) {

            tvVentasMesas.text =
                "Sin ventas hoy"

        } else {

            tvVentasMesas.text =
                ventasMesas.joinToString(
                    "\n"
                )
        }
    }
}