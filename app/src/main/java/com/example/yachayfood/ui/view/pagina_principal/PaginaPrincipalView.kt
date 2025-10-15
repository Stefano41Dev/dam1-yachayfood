package com.example.yachayfood.ui.view.pagina_principal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.yachayfood.databinding.PantallaPrincipalBinding

class PaginaPrincipalView : AppCompatActivity(){
    private lateinit var binding: PantallaPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PantallaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recycleView = binding.recyclerEscaneos
        recycleView.layoutManager = GridLayoutManager(this,2)
    }
}