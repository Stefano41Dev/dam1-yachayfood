package com.example.yachayfood.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yachayfood.R
import com.example.yachayfood.databinding.ItemProductoSeleccionBinding
import com.example.yachayfood.models.ProductoEntity

class ProductoSeleccionadoAdapter(
    private var productos: List<ProductoEntity>,
    private val onProductoSelected: (ProductoEntity, Boolean) -> Unit
): RecyclerView.Adapter<ProductoSeleccionadoAdapter.ViewHolder>() {

    // Mantiene el estado de los productos seleccionados
    private val selectedItems = mutableSetOf<String>()

    inner class ViewHolder(private val binding: ItemProductoSeleccionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(producto: ProductoEntity) {
            binding.txtNombreProducto.text = producto.nombre ?: "Producto sin nombre"
            binding.txtMarca.text = "Marca: ${producto.marca ?: "No especificada"}"

            val clasificacionFinal = producto.clasificacionYachay ?: producto.clasificacion
            val categoriaTexto = getCategoriaFromClasificacion(clasificacionFinal)

            binding.txtClasificacion.text = "Calificación: ${clasificacionFinal ?: "N/A"}"
            binding.txtCategoria.text = "Categoría: $categoriaTexto"

            val colorRes = when (clasificacionFinal?.uppercase()) {
                "C", "D", "E" -> R.color.text_classification_danger
                "AD", "A", "B" -> R.color.text_classification_safe
                else -> R.color.text_light_gray
            }
            val color = ContextCompat.getColor(binding.root.context, colorRes)
            binding.txtClasificacion.setTextColor(color)
            binding.txtCategoria.setTextColor(color)

            Glide.with(binding.root.context)
                .load(producto.imagenUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .centerInside()
                .into(binding.imgProducto)

            // Sincronizar el estado del CheckBox
            binding.checkboxProducto.isChecked = selectedItems.contains(producto.codigo)

            // Manejador de click para el CheckBox
            binding.checkboxProducto.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(producto.codigo)
                } else {
                    selectedItems.remove(producto.codigo)
                }
                onProductoSelected(producto, isChecked)
            }

            // Manejador de click para el item entero (cambia el checkbox)
            binding.root.setOnClickListener {
                binding.checkboxProducto.isChecked = !binding.checkboxProducto.isChecked
            }
        }

        private fun getCategoriaFromClasificacion(clasificacion: String?): String {
            return when (clasificacion?.uppercase()) {
                "AD" -> "Natural y Recomendado"
                "A" -> "Saludable"
                "B" -> "Aceptable"
                "C" -> "Consumo Moderado"
                "D", "E" -> "No Recomendado"
                else -> "No clasificado"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductoSeleccionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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