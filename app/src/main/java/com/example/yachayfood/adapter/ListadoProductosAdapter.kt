package com.example.yachayfood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yachayfood.databinding.ItemProductoBinding
import com.example.yachayfood.models.ProductoEntity

class ListadoProductosAdapter(
    private var productos: List<ProductoEntity>,
    private val onItemClick: (ProductoEntity) -> Unit
) : RecyclerView.Adapter<ListadoProductosAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemProductoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: ProductoEntity) {
            binding.txtNombreProducto.text = producto.nombre
            binding.txtMarca.text = producto.marca
            binding.txtClasificacion.text = producto.clasificacion
            binding.txtCategoria.text = producto.categorias

            // Cargar imagen (usa Glide o tu librería preferida)
            Glide.with(binding.root.context)
                .load(producto.imagenUrl)
                .placeholder(com.example.yachayfood.R.drawable.ic_launcher_background)
                .centerCrop()
                .into(binding.imgProducto)

            // Click en el ítem o en el botón
            binding.root.setOnClickListener { onItemClick(producto) }
            binding.root.findViewById<android.widget.Button>(com.example.yachayfood.R.id.btnVerInformacion)
                .setOnClickListener { onItemClick(producto) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productos[position])
    }

    override fun getItemCount(): Int = productos.size

    fun actualizarLista(nuevaLista: List<ProductoEntity>) {
        productos = nuevaLista
        notifyDataSetChanged()
    }
}