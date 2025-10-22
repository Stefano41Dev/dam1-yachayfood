package com.example.yachayfood.ui.view.pagina_principal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.yachayfood.databinding.PantallaPrincipalBinding
import com.example.yachayfood.ui.view.escanear_producto.EscanearProductoView

class PaginaPrincipalView : AppCompatActivity() {
    private lateinit var binding: PantallaPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PantallaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recycleView = binding.recyclerEscaneos
        recycleView.layoutManager = GridLayoutManager(this, 2)

        // 👉 Este click abre la pantalla del escáner
        binding.CircleContainer.setOnClickListener {
            val intent = Intent(this, EscanearProductoView::class.java)
            startActivity(intent)
        }
    }
}