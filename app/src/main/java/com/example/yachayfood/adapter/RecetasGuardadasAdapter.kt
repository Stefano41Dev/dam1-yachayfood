package com.example.yachayfood.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.yachayfood.R
import com.example.yachayfood.databinding.ItemRecetaGuardadaBinding
import com.example.yachayfood.models.RecetaEntity

class RecetasGuardadasAdapter(
    private var recetas: List<RecetaEntity>,
    private val onItemClick: (RecetaEntity) -> Unit
): RecyclerView.Adapter<RecetasGuardadasAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRecetaGuardadaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(receta: RecetaEntity) {
            binding.txtNombreReceta.text = receta.nombre
            binding.txtClasificacionReceta.text = "Clasificación: ${receta.clasificacionReceta}"

            // Lógica de color para la clasificación
            val colorRes = when (receta.clasificacionReceta) {
                "Nutritiva" -> R.color.text_classification_safe
                "Aceptable" -> R.color.text_classification_warning
                "No Recomendado" -> R.color.text_classification_danger
                else -> R.color.text_light_gray
            }
            val color = ContextCompat.getColor(binding.root.context, colorRes)
            binding.txtClasificacionReceta.setTextColor(color)

            // Icono (opcional, podrías cambiarlo por receta.icono si lo añades)
            binding.imgRecetaIcono.setImageResource(R.drawable.ic_menu_ia_recipe)

            binding.root.setOnClickListener { onItemClick(receta) }
            binding.btnVerReceta.setOnClickListener { onItemClick(receta) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecetaGuardadaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recetas[position])
    }

    override fun getItemCount(): Int = recetas.size

    fun actualizarLista(nuevaLista: List<RecetaEntity>) {
        recetas = nuevaLista
        notifyDataSetChanged()
    }

}