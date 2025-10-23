package com.example.yachayfood.ui.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel: ViewModel() {
    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress

    private val _navigateToHome = MutableLiveData(false)
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    private val SPLASH_TIME = 900L
    private val TOTAL_STEPS = 90

    init {
        startProgressAnimation()
    }

    private fun startProgressAnimation() {
        viewModelScope.launch {
            val stepDelay = SPLASH_TIME / TOTAL_STEPS
            for (i in 1..TOTAL_STEPS) {
                val currentProgress = (i * 100) / TOTAL_STEPS
                _progress.value = currentProgress
                delay(stepDelay)
            }
            _navigateToHome.value = true
        }
    }
}