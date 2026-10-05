package pe.edu.instituto.espinoza.saborapp.model

data class Mesa(
    val id: Int? = null,
    val numero: Int,
    val capacidad: Int,
    val estado: String = "LIBRE"
)