package pe.edu.instituto.espinoza.saborapp

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import pe.edu.instituto.espinoza.saborapp.data.DBHelper
import pe.edu.instituto.espinoza.saborapp.model.Plato

class PlatosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: PlatoAdapter
    private lateinit var spinnerCategoria: Spinner
    private lateinit var recyclerPlatos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_platos)

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
        // RECYCLERVIEW
        // ==========================================

        recyclerPlatos =
            findViewById(R.id.recyclerPlatos)

        adapter =
            PlatoAdapter(
                mutableListOf()
            )

        recyclerPlatos.layoutManager =
            LinearLayoutManager(this)

        recyclerPlatos.adapter =
            adapter

        // ==========================================
        // SPINNER DE CATEGORÍAS
        // ==========================================

        spinnerCategoria =
            findViewById(R.id.spinnerCategoria)

        configurarCategorias()

        // ==========================================
        // BOTONES DE PLATOS
        // ==========================================

        val btnHamburguesa =
            findViewById<MaterialButton>(
                R.id.btnHamburguesa
            )

        val btnPizza =
            findViewById<MaterialButton>(
                R.id.btnPizza
            )

        val btnPasta =
            findViewById<MaterialButton>(
                R.id.btnPasta
            )

        val btnPollo =
            findViewById<MaterialButton>(
                R.id.btnPollo
            )

        // ==========================================
        // CRUD
        // ==========================================

        val btnAgregar =
            findViewById<MaterialButton>(
                R.id.btnAgregarPlato
            )

        val btnEditar =
            findViewById<MaterialButton>(
                R.id.btnEditarPlato
            )

        val btnEliminar =
            findViewById<MaterialButton>(
                R.id.btnEliminarPlato
            )

        val btnRegresar =
            findViewById<MaterialButton>(
                R.id.btnRegresarPlatos
            )

        // ==========================================
        // PLATOS PREDEFINIDOS
        // ==========================================

        btnHamburguesa.setOnClickListener {

            insertarPlato(
                "Hamburguesa",
                "Comida rápida",
                15.00
            )
        }

        btnPizza.setOnClickListener {

            insertarPlato(
                "Pizza",
                "Comida italiana",
                20.00
            )
        }

        btnPasta.setOnClickListener {

            insertarPlato(
                "Pasta",
                "Comida italiana",
                18.00
            )
        }

        btnPollo.setOnClickListener {

            insertarPlato(
                "Pollo",
                "Platos principales",
                22.00
            )
        }

        // ==========================================
        // AGREGAR
        // ==========================================

        btnAgregar.setOnClickListener {

            mostrarDialogoAgregar()
        }

        // ==========================================
        // EDITAR
        // ==========================================

        btnEditar.setOnClickListener {

            mostrarDialogoEditar()
        }

        // ==========================================
        // ELIMINAR
        // ==========================================

        btnEliminar.setOnClickListener {

            mostrarDialogoEliminar()
        }

        // ==========================================
        // REGRESAR
        // ==========================================

        btnRegresar.setOnClickListener {

            finish()
        }

        // ==========================================
        // CARGAR PLATOS
        // ==========================================

        cargarPlatos()
    }


    // ==================================================
    // CONFIGURAR CATEGORÍAS
    // ==================================================

    private fun configurarCategorias() {

        val categorias = arrayOf(
            "Todas",
            "Comida rápida",
            "Comida italiana",
            "Platos principales",
            "Comida peruana",
            "Postres",
            "Bebidas"
        )

        val spinnerAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                categorias
            )

        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerCategoria.adapter =
            spinnerAdapter

        spinnerCategoria.onItemSelectedListener =
            object :
                android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {

                    val categoria =
                        categorias[position]

                    if (categoria == "Todas") {

                        cargarPlatos()

                    } else {

                        cargarPlatosPorCategoria(
                            categoria
                        )
                    }
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                }
            }
    }


    // ==================================================
    // CARGAR TODOS
    // ==================================================

    private fun cargarPlatos() {

        val platos =
            dbHelper.obtenerPlatos()

        adapter.actualizarLista(
            platos
        )
    }


    // ==================================================
    // CARGAR POR CATEGORÍA
    // ==================================================

    private fun cargarPlatosPorCategoria(
        categoria: String
    ) {

        val platos =
            dbHelper.obtenerPlatosPorCategoria(
                categoria
            )

        adapter.actualizarLista(
            platos
        )
    }


    // ==================================================
    // INSERTAR PLATO
    // ==================================================

    private fun insertarPlato(
        nombre: String,
        categoria: String,
        precio: Double
    ) {

        val plato =
            Plato(
                nombre = nombre,
                categoria = categoria,
                precio = precio,
                disponible = true
            )

        val resultado =
            dbHelper.insertarPlato(plato)

        if (resultado != -1L) {

            Toast.makeText(
                this,
                "$nombre agregado correctamente",
                Toast.LENGTH_SHORT
            ).show()

            actualizarListaActual()

        } else {

            Toast.makeText(
                this,
                "No se pudo agregar el plato",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // ==================================================
    // ACTUALIZAR LISTA SEGÚN FILTRO
    // ==================================================

    private fun actualizarListaActual() {

        val categoria =
            spinnerCategoria.selectedItem
                ?.toString()
                ?: "Todas"

        if (categoria == "Todas") {

            cargarPlatos()

        } else {

            cargarPlatosPorCategoria(
                categoria
            )
        }
    }


    // ==================================================
    // AGREGAR PLATO PERSONALIZADO
    // ==================================================

    private fun mostrarDialogoAgregar() {

        val layout =
            android.widget.LinearLayout(this)

        layout.orientation =
            android.widget.LinearLayout.VERTICAL

        layout.setPadding(
            50,
            20,
            50,
            10
        )

        val txtNombre =
            EditText(this)

        txtNombre.hint =
            "Nombre del plato"

        layout.addView(txtNombre)

        val txtCategoria =
            EditText(this)

        txtCategoria.hint =
            "Categoría"

        layout.addView(txtCategoria)

        val txtPrecio =
            EditText(this)

        txtPrecio.hint =
            "Precio"

        txtPrecio.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        layout.addView(txtPrecio)

        AlertDialog.Builder(this)
            .setTitle("Agregar plato")
            .setView(layout)
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton(
                "Guardar"
            ) { _, _ ->

                val nombre =
                    txtNombre.text
                        .toString()
                        .trim()

                val categoria =
                    txtCategoria.text
                        .toString()
                        .trim()

                val precioTexto =
                    txtPrecio.text
                        .toString()
                        .trim()

                if (
                    nombre.isEmpty() ||
                    categoria.isEmpty() ||
                    precioTexto.isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Completa todos los campos",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val precio =
                    precioTexto.toDoubleOrNull()

                if (precio == null) {

                    Toast.makeText(
                        this,
                        "El precio no es válido",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                insertarPlato(
                    nombre,
                    categoria,
                    precio
                )
            }
            .show()
    }


    // ==================================================
    // EDITAR
    // ==================================================

    private fun mostrarDialogoEditar() {

        val txtId =
            EditText(this)

        txtId.hint =
            "ID del plato"

        txtId.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle("Editar plato")
            .setMessage(
                "Ingresa el ID del plato que deseas editar"
            )
            .setView(txtId)
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton(
                "Continuar"
            ) { _, _ ->

                val id =
                    txtId.text
                        .toString()
                        .toIntOrNull()

                if (id == null) {

                    Toast.makeText(
                        this,
                        "ID no válido",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                mostrarDatosEditar(id)
            }
            .show()
    }


    private fun mostrarDatosEditar(
        id: Int
    ) {

        val layout =
            android.widget.LinearLayout(this)

        layout.orientation =
            android.widget.LinearLayout.VERTICAL

        layout.setPadding(
            50,
            20,
            50,
            10
        )

        val txtNombre =
            EditText(this)

        txtNombre.hint =
            "Nuevo nombre"

        layout.addView(txtNombre)

        val txtCategoria =
            EditText(this)

        txtCategoria.hint =
            "Nueva categoría"

        layout.addView(txtCategoria)

        val txtPrecio =
            EditText(this)

        txtPrecio.hint =
            "Nuevo precio"

        txtPrecio.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        layout.addView(txtPrecio)

        AlertDialog.Builder(this)
            .setTitle(
                "Editar plato #$id"
            )
            .setView(layout)
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton(
                "Actualizar"
            ) { _, _ ->

                val nombre =
                    txtNombre.text
                        .toString()
                        .trim()

                val categoria =
                    txtCategoria.text
                        .toString()
                        .trim()

                val precio =
                    txtPrecio.text
                        .toString()
                        .toDoubleOrNull()

                if (
                    nombre.isEmpty() ||
                    categoria.isEmpty() ||
                    precio == null
                ) {

                    Toast.makeText(
                        this,
                        "Completa correctamente los campos",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val plato =
                    Plato(
                        id = id,
                        nombre = nombre,
                        categoria = categoria,
                        precio = precio,
                        disponible = true
                    )

                val resultado =
                    dbHelper.actualizarPlato(
                        plato
                    )

                if (resultado > 0) {

                    Toast.makeText(
                        this,
                        "Plato actualizado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    actualizarListaActual()

                } else {

                    Toast.makeText(
                        this,
                        "No se encontró el plato",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }


    // ==================================================
    // ELIMINAR
    // ==================================================

    private fun mostrarDialogoEliminar() {

        val txtId =
            EditText(this)

        txtId.hint =
            "ID del plato"

        txtId.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle("Eliminar plato")
            .setMessage(
                "Ingresa el ID del plato que deseas eliminar"
            )
            .setView(txtId)
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton(
                "Eliminar"
            ) { _, _ ->

                val id =
                    txtId.text
                        .toString()
                        .toIntOrNull()

                if (id == null) {

                    Toast.makeText(
                        this,
                        "ID no válido",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val resultado =
                    dbHelper.eliminarPlato(id)

                if (resultado > 0) {

                    Toast.makeText(
                        this,
                        "Plato eliminado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    actualizarListaActual()

                } else {

                    Toast.makeText(
                        this,
                        "No se encontró el plato",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }
}