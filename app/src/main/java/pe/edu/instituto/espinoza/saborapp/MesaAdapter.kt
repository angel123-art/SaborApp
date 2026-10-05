package pe.edu.instituto.espinoza.saborapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pe.edu.instituto.espinoza.saborapp.model.Mesa

class MesaAdapter(
    private var mesas: MutableList<Mesa>
) : RecyclerView.Adapter<MesaAdapter.MesaViewHolder>() {

    class MesaViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvNumeroMesa: TextView =
            itemView.findViewById(R.id.tvNumeroMesa)

        val tvCapacidadMesa: TextView =
            itemView.findViewById(R.id.tvCapacidadMesa)

        val tvEstadoMesa: TextView =
            itemView.findViewById(R.id.tvEstadoMesa)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MesaViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_mesa,
                parent,
                false
            )

        return MesaViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MesaViewHolder,
        position: Int
    ) {

        val mesa = mesas[position]

        holder.tvNumeroMesa.text =
            "Mesa ${mesa.numero}"

        holder.tvCapacidadMesa.text =
            "👥 ${mesa.capacidad} personas"

        holder.tvEstadoMesa.text =
            if (mesa.estado == "LIBRE") {
                "🟢 LIBRE"
            } else {
                "🔴 OCUPADA"
            }
    }

    override fun getItemCount(): Int {
        return mesas.size
    }

    fun actualizarLista(
        nuevaLista: MutableList<Mesa>
    ) {
        mesas = nuevaLista
        notifyDataSetChanged()
    }
}