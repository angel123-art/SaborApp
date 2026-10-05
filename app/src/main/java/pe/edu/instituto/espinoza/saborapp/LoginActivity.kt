package pe.edu.instituto.espinoza.saborapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import pe.edu.instituto.espinoza.saborapp.data.DBHelper
import pe.edu.instituto.espinoza.saborapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar base de datos
        dbHelper = DBHelper(this)

        // Asegurar que exista el administrador
        dbHelper.asegurarUsuarioAdmin()

        configurarLogin()
        configurarRegistro()
    }

    // =====================================================
    // LOGIN
    // =====================================================

    private fun configurarLogin() {

        binding.btnIngresar.setOnClickListener {

            // Obtener datos
            val usuario = binding.etUsuario.text
                ?.toString()
                ?.trim()
                .orEmpty()

            val contrasena = binding.etContrasena.text
                ?.toString()
                ?.trim()
                .orEmpty()

            // Limpiar errores
            binding.tilUsuario.error = null
            binding.tilContrasena.error = null

            var formularioValido = true

            // Validar usuario
            if (usuario.isEmpty()) {

                binding.tilUsuario.error =
                    getString(R.string.error_usuario_vacio)

                formularioValido = false
            }

            // Validar contraseña
            if (contrasena.isEmpty()) {

                binding.tilContrasena.error =
                    getString(R.string.error_contrasena_vacia)

                formularioValido = false
            }

            // Si hay errores, detener
            if (!formularioValido) {
                return@setOnClickListener
            }

            // =================================================
            // VALIDAR USUARIO EN SQLITE
            // =================================================

            val usuarioValido = dbHelper.validarUsuario(
                usuario,
                contrasena
            )

            if (usuarioValido) {

                // Obtener información del usuario
                val datosUsuario =
                    dbHelper.obtenerDatosUsuario(usuario)

                if (datosUsuario != null) {

                    val idUsuario = datosUsuario.first
                    val nombreUsuario = datosUsuario.second
                    val rolUsuario = datosUsuario.third

                    // Abrir menú
                    val intent = Intent(
                        this,
                        MenuActivity::class.java
                    )

                    // Enviar datos del usuario
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

                    // No volver al Login
                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "No se pudieron obtener los datos del usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {

                // Credenciales incorrectas
                Toast.makeText(
                    this,
                    getString(R.string.error_credenciales),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // =====================================================
    // REGISTRAR USUARIO
    // =====================================================

    private fun configurarRegistro() {

        binding.btnRegistrar.setOnClickListener {

            val intent = Intent(
                this,
                RegistrarUsuarioActivity::class.java
            )

            startActivity(intent)
        }
    }
}