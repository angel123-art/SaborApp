package pe.edu.instituto.espinoza.saborapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import pe.edu.instituto.espinoza.saborapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarLogin()
    }

    private fun configurarLogin() {
        binding.btnIngresar.setOnClickListener {
            val usuario = binding.etUsuario.text?.toString()?.trim().orEmpty()
            val contrasena = binding.etContrasena.text?.toString()?.trim().orEmpty()

            binding.tilUsuario.error = null
            binding.tilContrasena.error = null

            var formularioValido = true

            if (usuario.isEmpty()) {
                binding.tilUsuario.error = getString(R.string.error_usuario_vacio)
                formularioValido = false
            }

            if (contrasena.isEmpty()) {
                binding.tilContrasena.error = getString(R.string.error_contrasena_vacia)
                formularioValido = false
            }

            if (!formularioValido) {
                return@setOnClickListener
            }

            // CA2: credenciales admin / 1234
            if (usuario == "admin" && contrasena == "1234") {
                val intent = Intent(this, MenuActivity::class.java).apply {
                    putExtra("ID_USUARIO", 1)
                    putExtra("NOMBRE_USUARIO", "Administrador")
                    putExtra("ROL", "ADMIN")
                }
                startActivity(intent)
                finish()
            } else {
                // CA3: credenciales incorrectas
                Toast.makeText(this, getString(R.string.error_credenciales), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
