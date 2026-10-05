package pe.edu.instituto.espinoza.saborapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pe.edu.instituto.espinoza.saborapp.model.Plato

class PlatoAdapter(
    private var platos: MutableList<Plato>
) : RecyclerView.Adapter<PlatoAdapter.PlatoViewHolder>() {

    class PlatoViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvNombre: TextView =
            itemView.findViewById(R.id.tvNombrePlato)

        val tvCategoria: TextView =
            itemView.findViewById(R.id.tvCategoriaPlato)

        val tvPrecio: TextView =
            itemView.findViewById(R.id.tvPrecioPlato)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlatoViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_plato,
                parent,
                false
            )

        return PlatoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlatoViewHolder,
        position: Int
    ) {

        val plato = platos[position]

        holder.tvNombre.text =
            "🍽  ${plato.nombre}"

        holder.tvCategoria.text =
            plato.categoria

        holder.tvPrecio.text =
            "S/ %.2f".format(plato.precio)
    }

    override fun getItemCount(): Int {
        return platos.size
    }

    fun actualizarLista(
        nuevaLista: MutableList<Plato>
    ) {

        platos = nuevaLista

        notifyDataSetChanged()
    }
}