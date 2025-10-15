package com.example.yachayfood

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputName = findViewById<EditText>(R.id.inputName)
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)
        val label = findViewById<TextView>(R.id.label)

        buttonSubmit.setOnClickListener {
            val name = inputName.text.toString()
            if (name.isNotBlank()) {
                Toast.makeText(this, "Hola $name", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor ingrese su nombre", Toast.LENGTH_SHORT).show()
            }
        }
    }
}