package com.example.yachayfood.ui.view.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.yachayfood.databinding.ActivitySplashInicioBinding
import com.example.yachayfood.ui.view.pantalla_principal.PantallaPrincipalView

class SplashView : AppCompatActivity() {

    private lateinit var binding: ActivitySplashInicioBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.max = 100


        viewModel.progress.observe(this) { currentProgress ->
            binding.progressBar.progress = currentProgress
        }


        viewModel.navigateToHome.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                startActivity(Intent(this@SplashView, PantallaPrincipalView::class.java))
                finish()
            }
        }
    }
}