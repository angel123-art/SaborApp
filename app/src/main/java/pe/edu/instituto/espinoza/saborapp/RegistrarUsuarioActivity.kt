package pe.edu.instituto.espinoza.saborapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import pe.edu.instituto.espinoza.saborapp.data.DBHelper

class RegistrarUsuarioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        dbHelper = DBHelper(this)

        val etNombre = findViewById<TextInputEditText>(R.id.etNombreRegistro)
        val etUsuario = findViewById<TextInputEditText>(R.id.etUsuarioRegistro)
        val etClave = findViewById<TextInputEditText>(R.id.etClaveRegistro)

        val spRol = findViewById<AutoCompleteTextView>(R.id.spRol)

        val btnGuardar = findViewById<MaterialButton>(R.id.btnGuardarUsuario)
        val btnRegresar = findViewById<MaterialButton>(R.id.btnRegresarRegistro)

        // Roles disponibles
        val roles = arrayOf("MOZO", "ADMIN")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            roles
        )

        spRol.setAdapter(adapter)
        spRol.setText("MOZO", false)

        // Guardar usuario
        btnGuardar.setOnClickListener {

            val nombre = etNombre.text.toString().trim()
            val usuario = etUsuario.text.toString().trim()
            val clave = etClave.text.toString().trim()
            val rol = spRol.text.toString().trim()

            // Validaciones
            if (nombre.isEmpty()) {
                etNombre.error = "Ingrese el nombre"
                etNombre.requestFocus()
                return@setOnClickListener
            }

            if (usuario.isEmpty()) {
                etUsuario.error = "Ingrese el usuario"
                etUsuario.requestFocus()
                return@setOnClickListener
            }

            if (clave.isEmpty()) {
                etClave.error = "Ingrese la contraseña"
                etClave.requestFocus()
                return@setOnClickListener
            }

            if (clave.length < 4) {
                etClave.error = "La contraseña debe tener al menos 4 caracteres"
                etClave.requestFocus()
                return@setOnClickListener
            }

            // Verificar usuario duplicado
            if (dbHelper.existeUsuario(usuario)) {
                etUsuario.error = "El usuario ya existe"
                etUsuario.requestFocus()

                Toast.makeText(
                    this,
                    "El usuario ya está registrado",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Registrar usuario
            val resultado = dbHelper.registrarUsuario(
                nombre = nombre,
                usuario = usuario,
                clave = clave,
                rol = rol
            )

            if (resultado != -1L) {

                Toast.makeText(
                    this,
                    "Usuario registrado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                // Regresar al Login
                finish()

            } else {

                Toast.makeText(
                    this,
                    "No se pudo registrar el usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Regresar al Login
        btnRegresar.setOnClickListener {
            finish()
        }
    }
}