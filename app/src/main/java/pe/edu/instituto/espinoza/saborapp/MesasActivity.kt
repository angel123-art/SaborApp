package pe.edu.instituto.espinoza.saborapp

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import pe.edu.instituto.espinoza.saborapp.data.DBHelper
import pe.edu.instituto.espinoza.saborapp.model.Mesa

class MesasActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: MesaAdapter
    private lateinit var recyclerMesas: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_mesas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // ==============================================
        // BASE DE DATOS
        // ==============================================

        dbHelper = DBHelper(this)

        // ==============================================
        // RECYCLERVIEW
        // ==============================================

        recyclerMesas = findViewById(R.id.recyclerMesas)

        adapter = MesaAdapter(mutableListOf())

        recyclerMesas.layoutManager = GridLayoutManager(this, 3)

        recyclerMesas.adapter = adapter

        // ==============================================
        // BOTONES
        // ==============================================

        val btnAgregarMesa =
            findViewById<MaterialButton>(R.id.btnAgregarMesa)

        val btnEditarMesa =
            findViewById<MaterialButton>(R.id.btnEditarMesa)

        val btnEliminarMesa =
            findViewById<MaterialButton>(R.id.btnEliminarMesa)

        val btnRegresarMesas =
            findViewById<MaterialButton>(R.id.btnRegresarMesas)

        // ==============================================
        // AGREGAR
        // ==============================================

        btnAgregarMesa.setOnClickListener {
            mostrarDialogoAgregar()
        }

        // ==============================================
        // EDITAR
        // ==============================================

        btnEditarMesa.setOnClickListener {
            mostrarDialogoEditar()
        }

        // ==============================================
        // ELIMINAR
        // ==============================================

        btnEliminarMesa.setOnClickListener {
            mostrarDialogoEliminar()
        }

        // ==============================================
        // REGRESAR
        // ==============================================

        btnRegresarMesas.setOnClickListener {
            finish()
        }

        // ==============================================
        // CARGAR MESAS
        // ==============================================

        cargarMesas()
    }

    // =====================================================
    // OBTENER Y MOSTRAR MESAS
    // =====================================================

    private fun cargarMesas() {

        val mesas = dbHelper.obtenerMesas()

        adapter.actualizarLista(mesas)
    }

    // =====================================================
    // AGREGAR MESA
    // =====================================================

    private fun mostrarDialogoAgregar() {

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(
            50,
            20,
            50,
            10
        )

        // Número
        val etNumero = EditText(this)

        etNumero.hint = "Número de mesa"

        etNumero.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER

        layout.addView(etNumero)

        // Capacidad
        val etCapacidad = EditText(this)

        etCapacidad.hint = "Capacidad (1 - 12)"

        etCapacidad.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER

        layout.addView(etCapacidad)

        val dialogo = AlertDialog.Builder(this)
            .setTitle("Agregar mesa")
            .setView(layout)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar", null)
            .create()

        dialogo.setOnShowListener {

            dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val numeroTexto =
                        etNumero.text.toString().trim()

                    val capacidadTexto =
                        etCapacidad.text.toString().trim()

                    // Validar número
                    if (numeroTexto.isEmpty()) {

                        etNumero.error =
                            "Ingrese el número de mesa"

                        return@setOnClickListener
                    }

                    // Validar capacidad
                    if (capacidadTexto.isEmpty()) {

                        etCapacidad.error =
                            "Ingrese la capacidad"

                        return@setOnClickListener
                    }

                    val numero =
                        numeroTexto.toIntOrNull()

                    val capacidad =
                        capacidadTexto.toIntOrNull()

                    if (numero == null || numero <= 0) {

                        etNumero.error =
                            "Número inválido"

                        return@setOnClickListener
                    }

                    if (capacidad == null ||
                        capacidad !in 1..12
                    ) {

                        etCapacidad.error =
                            "La capacidad debe estar entre 1 y 12"

                        return@setOnClickListener
                    }

                    // Comprobar si ya existe
                    val mesaExiste =
                        dbHelper.obtenerMesas()
                            .any { it.numero == numero }

                    if (mesaExiste) {

                        Toast.makeText(
                            this,
                            "La mesa ya existe",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@setOnClickListener
                    }

                    // Crear mesa
                    val mesa = Mesa(
                        numero = numero,
                        capacidad = capacidad,
                        estado = "LIBRE"
                    )

                    val resultado =
                        dbHelper.insertarMesa(mesa)

                    if (resultado != -1L) {

                        Toast.makeText(
                            this,
                            "Mesa $numero agregada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        cargarMesas()

                        dialogo.dismiss()

                    } else {

                        Toast.makeText(
                            this,
                            "No se pudo agregar la mesa",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        dialogo.show()
    }

    // =====================================================
    // EDITAR MESA
    // =====================================================

    private fun mostrarDialogoEditar() {

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(
            50,
            20,
            50,
            10
        )

        // Número actual
        val etNumero = EditText(this)

        etNumero.hint = "Número de mesa"

        etNumero.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER

        layout.addView(etNumero)

        // Nueva capacidad
        val etCapacidad = EditText(this)

        etCapacidad.hint = "Nueva capacidad (1 - 12)"

        etCapacidad.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER

        layout.addView(etCapacidad)

        val dialogo = AlertDialog.Builder(this)
            .setTitle("Editar mesa")
            .setView(layout)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Actualizar", null)
            .create()

        dialogo.setOnShowListener {

            dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val numeroTexto =
                        etNumero.text.toString().trim()

                    val capacidadTexto =
                        etCapacidad.text.toString().trim()

                    if (numeroTexto.isEmpty()) {

                        etNumero.error =
                            "Ingrese el número de mesa"

                        return@setOnClickListener
                    }

                    if (capacidadTexto.isEmpty()) {

                        etCapacidad.error =
                            "Ingrese la capacidad"

                        return@setOnClickListener
                    }

                    val numero =
                        numeroTexto.toIntOrNull()

                    val capacidad =
                        capacidadTexto.toIntOrNull()

                    if (numero == null || numero <= 0) {

                        etNumero.error =
                            "Número inválido"

                        return@setOnClickListener
                    }

                    if (capacidad == null ||
                        capacidad !in 1..12
                    ) {

                        etCapacidad.error =
                            "La capacidad debe estar entre 1 y 12"

                        return@setOnClickListener
                    }

                    // Buscar mesa
                    val mesaExistente =
                        dbHelper.obtenerMesas()
                            .find { it.numero == numero }

                    if (mesaExistente == null) {

                        Toast.makeText(
                            this,
                            "No existe la mesa $numero",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@setOnClickListener
                    }

                    val mesaActualizada = Mesa(
                        id = mesaExistente.id,
                        numero = mesaExistente.numero,
                        capacidad = capacidad,
                        estado = mesaExistente.estado
                    )

                    val resultado =
                        dbHelper.actualizarMesa(mesaActualizada)

                    if (resultado > 0) {

                        Toast.makeText(
                            this,
                            "Mesa $numero actualizada",
                            Toast.LENGTH_SHORT
                        ).show()

                        cargarMesas()

                        dialogo.dismiss()

                    } else {

                        Toast.makeText(
                            this,
                            "No se pudo actualizar la mesa",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        dialogo.show()
    }

    // =====================================================
    // ELIMINAR MESA
    // =====================================================

    private fun mostrarDialogoEliminar() {

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(
            50,
            20,
            50,
            10
        )

        val etNumero = EditText(this)

        etNumero.hint = "Número de mesa"

        etNumero.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER

        layout.addView(etNumero)

        AlertDialog.Builder(this)
            .setTitle("Eliminar mesa")
            .setMessage(
                "Ingrese el número de la mesa que desea eliminar."
            )
            .setView(layout)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Continuar") { _, _ ->

                val numeroTexto =
                    etNumero.text.toString().trim()

                val numero =
                    numeroTexto.toIntOrNull()

                if (numero == null || numero <= 0) {

                    Toast.makeText(
                        this,
                        "Número de mesa inválido",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val mesa =
                    dbHelper.obtenerMesas()
                        .find { it.numero == numero }

                if (mesa == null) {

                    Toast.makeText(
                        this,
                        "No existe la mesa $numero",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                // Confirmación final
                AlertDialog.Builder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage(
                        "¿Desea eliminar la Mesa $numero?"
                    )
                    .setNegativeButton(
                        "Cancelar",
                        null
                    )
                    .setPositiveButton(
                        "Eliminar"
                    ) { _, _ ->

                        val resultado =
                            dbHelper.eliminarMesa(
                                mesa.id!!
                            )

                        if (resultado > 0) {

                            Toast.makeText(
                                this,
                                "Mesa $numero eliminada",
                                Toast.LENGTH_SHORT
                            ).show()

                            cargarMesas()

                        } else {

                            Toast.makeText(
                                this,
                                "No se pudo eliminar la mesa",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .show()
            }
            .show()
    }

    // =====================================================
    // ACTUALIZAR AL VOLVER A LA PANTALLA
    // =====================================================

    override fun onResume() {
        super.onResume()

        if (::dbHelper.isInitialized &&
            ::adapter.isInitialized
        ) {
            cargarMesas()
        }
    }
}