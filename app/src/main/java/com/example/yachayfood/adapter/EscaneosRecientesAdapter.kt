package com.example.yachayfood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yachayfood.R
import com.example.yachayfood.models.ProductoEntity

class EscaneosRecientesAdapter(
    private var productos: List<ProductoEntity>,
    private val onItemClick: (ProductoEntity) -> Unit // ahora pasa ProductoEntity
) : RecyclerView.Adapter<EscaneosRecientesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProducto: ImageView = view.findViewById(R.id.ImgImagenProductoReciente)
        val textNombre: TextView = view.findViewById(R.id.TextTituloProductoReciente)

        init {
            view.setOnClickListener {
                //TODO: Cambiar lo deprecado
                onItemClick(productos[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_escaneo_reciente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]
        holder.textNombre.text = producto.nombre ?: "Sin nombre"

        Glide.with(holder.itemView.context)
            .load(producto.imagenUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.imgProducto)
    }

    override fun getItemCount(): Int = productos.size

    fun actualizarLista(nuevaLista: List<ProductoEntity>) {
        productos = nuevaLista
        notifyDataSetChanged()
    }
}
