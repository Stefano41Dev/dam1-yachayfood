package com.example.yachayfood

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yachayfood.databinding.PantallaPrincipalBinding
import com.example.yachayfood.ui.view.escanear_producto.EscanearProductoView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: PantallaPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PantallaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.CircleContainer.setOnClickListener {
            val intent = Intent(this, EscanearProductoView::class.java)
            startActivity(intent)
        }
    }
}