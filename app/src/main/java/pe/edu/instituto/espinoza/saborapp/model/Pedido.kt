package pe.edu.instituto.espinoza.saborapp.model

data class Pedido(
    val id: Int? = null,
    val mesaId: Int,
    val usuarioId: Int,
    val fecha: String,
    val estado: String = "ABIERTO",
    val total: Double = 0.0
)