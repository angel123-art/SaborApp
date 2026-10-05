package pe.edu.instituto.espinoza.saborapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import pe.edu.instituto.espinoza.saborapp.data.DBHelper
import pe.edu.instituto.espinoza.saborapp.model.Pedido

class CerrarCuentaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    private lateinit var spinnerPedidos: Spinner
    private lateinit var tvDetalleCuenta: TextView
    private lateinit var tvTotalCuenta: TextView
    private lateinit var btnCompartirCuenta: MaterialButton
    private lateinit var btnCerrarCuenta: MaterialButton
    private lateinit var btnRegresarCerrar: MaterialButton

    private var pedidos =
        mutableListOf<Pedido>()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(
            R.layout.activity_cerrar_cuenta
        )

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


        // ==========================================
        // BASE DE DATOS
        // ==========================================

        dbHelper = DBHelper(this)


        // ==========================================
        // VISTAS
        // ==========================================

        spinnerPedidos =
            findViewById(
                R.id.spinnerPedidos
            )

        tvDetalleCuenta =
            findViewById(
                R.id.tvDetalleCuenta
            )

        tvTotalCuenta =
            findViewById(
                R.id.tvTotalCuenta
            )

        btnCompartirCuenta =
            findViewById(
                R.id.btnCompartirCuenta
            )

        btnCerrarCuenta =
            findViewById(
                R.id.btnCerrarCuenta
            )

        btnRegresarCerrar =
            findViewById(
                R.id.btnRegresarCerrar
            )


        // ==========================================
        // BOTÓN REGRESAR
        // ==========================================

        btnRegresarCerrar.setOnClickListener {

            finish()
        }


        // ==========================================
        // CARGAR PEDIDOS
        // ==========================================

        cargarPedidos()


        // ==========================================
        // SELECCIONAR PEDIDO
        // ==========================================

        spinnerPedidos.setOnItemSelectedListener(

            object :
                android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {

                    if (
                        position >= 0 &&
                        position < pedidos.size
                    ) {

                        mostrarCuenta(
                            pedidos[position]
                        )
                    }
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                }
            }
        )


        // ==========================================
        // COMPARTIR CUENTA
        // ==========================================

        btnCompartirCuenta.setOnClickListener {

            compartirCuenta()
        }


        // ==========================================
        // CERRAR CUENTA
        // ==========================================

        btnCerrarCuenta.setOnClickListener {

            cerrarCuenta()
        }
    }


    // =====================================================
    // CARGAR PEDIDOS ABIERTOS
    // =====================================================

    private fun cargarPedidos() {

        pedidos =
            dbHelper.obtenerPedidosAbiertos()


        val nombres =
            mutableListOf<String>()


        for (pedido in pedidos) {

            val mesa =
                dbHelper.obtenerMesas().find {

                    it.id == pedido.mesaId
                }


            if (mesa != null) {

                nombres.add(
                    "Pedido #${pedido.id} - Mesa ${mesa.numero} - S/ %.2f"
                        .format(pedido.total)
                )

            } else {

                nombres.add(
                    "Pedido #${pedido.id} - S/ %.2f"
                        .format(pedido.total)
                )
            }
        }


        if (nombres.isEmpty()) {

            nombres.add(
                "No hay cuentas abiertas"
            )

            btnCompartirCuenta.isEnabled = false
            btnCerrarCuenta.isEnabled = false

        } else {

            btnCompartirCuenta.isEnabled = true
            btnCerrarCuenta.isEnabled = true
        }


        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                nombres
            )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerPedidos.adapter = adapter


        if (pedidos.isEmpty()) {

            tvDetalleCuenta.text =
                "No hay pedidos abiertos"

            tvTotalCuenta.text =
                "TOTAL: S/ 0.00"

        } else {

            mostrarCuenta(
                pedidos[0]
            )
        }
    }


    // =====================================================
    // MOSTRAR CUENTA
    // =====================================================

    private fun mostrarCuenta(
        pedido: Pedido
    ) {

        val detalles =
            dbHelper.obtenerDetallesPedido(
                pedido.id!!
            )

        val platos =
            dbHelper.obtenerPlatos()


        val texto =
            StringBuilder()


        texto.append(
            "Pedido #${pedido.id}\n"
        )

        texto.append(
            "Fecha: ${pedido.fecha}\n\n"
        )


        for (detalle in detalles) {

            val plato =
                platos.find {

                    it.id == detalle.platoId
                }


            if (plato != null) {

                val subtotal =
                    detalle.cantidad *
                            detalle.precio


                texto.append(
                    "🍽 ${plato.nombre}\n"
                )

                texto.append(
                    "   Cantidad: ${detalle.cantidad}\n"
                )

                texto.append(
                    "   Precio: S/ %.2f\n"
                        .format(detalle.precio)
                )

                texto.append(
                    "   Subtotal: S/ %.2f\n\n"
                        .format(subtotal)
                )
            }
        }


        tvDetalleCuenta.text =
            texto.toString()


        tvTotalCuenta.text =
            "TOTAL: S/ %.2f"
                .format(pedido.total)
    }


    // =====================================================
    // COMPARTIR CUENTA
    // =====================================================

    private fun compartirCuenta() {

        if (pedidos.isEmpty()) {

            Toast.makeText(
                this,
                "No hay cuentas abiertas",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val posicion =
            spinnerPedidos.selectedItemPosition


        if (
            posicion < 0 ||
            posicion >= pedidos.size
        ) {

            Toast.makeText(
                this,
                "Selecciona una cuenta",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val pedido =
            pedidos[posicion]


        val detalles =
            dbHelper.obtenerDetallesPedido(
                pedido.id!!
            )

        val platos =
            dbHelper.obtenerPlatos()


        // ==========================================
        // OBTENER MESA
        // ==========================================

        val mesa =
            dbHelper.obtenerMesas().find {

                it.id == pedido.mesaId
            }


        val numeroMesa =
            mesa?.numero ?: 0


        // ==========================================
        // CREAR MENSAJE
        // ==========================================

        val mensaje =
            StringBuilder()


        mensaje.append(
            "🍽️ SABOR APP\n"
        )

        mensaje.append(
            "POLLERÍA EL BUEN SABOR\n\n"
        )

        mensaje.append(
            "🧾 CUENTA - MESA $numeroMesa\n"
        )

        mensaje.append(
            "Pedido #${pedido.id}\n\n"
        )


        for (detalle in detalles) {

            val plato =
                platos.find {

                    it.id == detalle.platoId
                }


            if (plato != null) {

                val subtotal =
                    detalle.cantidad *
                            detalle.precio


                mensaje.append(
                    "🍽️ ${plato.nombre}\n"
                )

                mensaje.append(
                    "Cantidad: ${detalle.cantidad}\n"
                )

                mensaje.append(
                    "Precio: S/ %.2f\n"
                        .format(detalle.precio)
                )

                mensaje.append(
                    "Subtotal: S/ %.2f\n\n"
                        .format(subtotal)
                )
            }
        }


        mensaje.append(
            "💰 TOTAL: S/ %.2f\n\n"
                .format(pedido.total)
        )

        mensaje.append(
            "Gracias por su preferencia."
        )


        // ==========================================
        // COMPARTIR CON ANDROID
        // ==========================================

        val intent =
            Intent(Intent.ACTION_SEND).apply {

                type = "text/plain"

                putExtra(
                    Intent.EXTRA_TEXT,
                    mensaje.toString()
                )
            }


        val chooser =
            Intent.createChooser(
                intent,
                "Compartir cuenta"
            )


        try {

            startActivity(chooser)

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "No hay aplicaciones disponibles para compartir",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    // =====================================================
    // CERRAR CUENTA
    // =====================================================

    private fun cerrarCuenta() {

        if (pedidos.isEmpty()) {

            Toast.makeText(
                this,
                "No hay cuentas abiertas",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val posicion =
            spinnerPedidos.selectedItemPosition


        if (
            posicion < 0 ||
            posicion >= pedidos.size
        ) {

            Toast.makeText(
                this,
                "Selecciona una cuenta",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val pedido =
            pedidos[posicion]


        AlertDialog.Builder(this)
            .setTitle("Cerrar cuenta")
            .setMessage(
                "¿Deseas cerrar el pedido #${pedido.id}?\n\n" +
                        "Total: S/ %.2f\n\n" +
                        "La mesa quedará LIBRE y la venta aparecerá en Reportes."
                            .format(pedido.total)
            )
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton(
                "Cerrar cuenta"
            ) { _, _ ->

                val resultado =
                    dbHelper.cerrarPedido(
                        pedido.id!!
                    )


                if (resultado) {

                    Toast.makeText(
                        this,
                        "Cuenta cerrada correctamente",
                        Toast.LENGTH_LONG
                    ).show()


                    cargarPedidos()

                } else {

                    Toast.makeText(
                        this,
                        "No se pudo cerrar la cuenta",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .show()
    }


    override fun onResume() {

        super.onResume()

        if (::dbHelper.isInitialized) {

            cargarPedidos()
        }
    }
}