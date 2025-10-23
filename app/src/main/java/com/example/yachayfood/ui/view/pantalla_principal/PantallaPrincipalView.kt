package com.example.yachayfood.ui.view.pantalla_principal

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.yachayfood.databinding.ActivityPantallaPrincipalBinding
import com.example.yachayfood.ui.view.escanear_producto.EscanearProductoView

class PantallaPrincipalView : AppCompatActivity() {
    private lateinit var binding: ActivityPantallaPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recycleView = binding.recyclerEscaneos
        recycleView.layoutManager = GridLayoutManager(this, 2)

        binding.CircleContainer.setOnClickListener {
            val intent = Intent(this, EscanearProductoView::class.java)
            startActivity(intent)
        }
    }
    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
           AlertDialog.Builder(this)
            .setMessage("¿Estás seguro que quieres salir de la aplicación?")
            .setPositiveButton("Sí", DialogInterface.OnClickListener { dialog, id ->
                finishAffinity()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })
            .show()
    }
}