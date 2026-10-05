package pe.edu.instituto.espinoza.saborapp.model

data class DetallePedido(
    val id: Int? = null,
    val pedidoId: Int,
    val platoId: Int,
    val cantidad: Int,
    val precio: Double
)