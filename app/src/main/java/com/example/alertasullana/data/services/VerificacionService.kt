package com.example.alertasullana.data.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.example.alertasullana.ui.registro.RegistroUsuarioActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class VerificacionService: Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }

    //Instancia de Firebase
    private val firebaseAuth = FirebaseAuth.getInstance();
    //Codigo de verificacion
    private var verificationId: String? = null;

    // Método para enviar el código de verificación al número de teléfono
    fun sendVerificationCode(
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Método para verificar el código ingresado por el usuario
    fun verifyCode(code: String) {
        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it, code) }
        if (credential != null) {
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Verificación exitosa", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, RegistroUsuarioActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "La verificación falló. Por favor, inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
