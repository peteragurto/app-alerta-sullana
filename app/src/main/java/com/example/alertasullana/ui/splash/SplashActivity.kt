package com.example.alertasullana.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.alertasullana.R
import com.example.alertasullana.data.repository.FirebaseRepository
import com.example.alertasullana.ui.principal.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val firebaseRepository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(
            {
                verificarUsuarioAutenticado()
            }, 1500
        )
    }

    // Método para verificar si hay un usuario autenticado
    private fun verificarUsuarioAutenticado() {
        val usuarioActual = firebaseRepository.obtenerUsuarioActual()

        if (usuarioActual != null) {
            // Usuario autenticado, ir a MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // No hay usuario autenticado, ir a IntroActivity
            startActivity(Intent(this, IntroActivity::class.java))
        }

        finish()
    }
}