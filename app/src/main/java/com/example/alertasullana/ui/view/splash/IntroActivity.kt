package com.example.alertasullana.ui.view.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alertasullana.R
import com.google.android.material.button.MaterialButton

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // Obtiene el botón comenzar
        val btnComenzar = findViewById<MaterialButton>(R.id.boton_comenzar)

        // Define el evento de clic del botón comenzar
        btnComenzar.setOnClickListener {
            // Haz algo cuando el botón se haga clic
        }
    }
}