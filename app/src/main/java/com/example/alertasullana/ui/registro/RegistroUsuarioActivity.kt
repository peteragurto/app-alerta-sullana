package com.example.alertasullana.ui.registro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alertasullana.R
import com.example.alertasullana.data.network.ConnectivityChecker
import com.example.alertasullana.ui.principal.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class RegistroUsuarioActivity : AppCompatActivity() {
    private lateinit var materialButton: MaterialButton
    private lateinit var client:GoogleSignInClient
    private val connectivityChecker: ConnectivityChecker by lazy { ConnectivityChecker(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el diseño de la actividad a partir del archivo XML en res/layout/activity_registro_usuario.xml
        setContentView(R.layout.activity_registro_usuario)
        materialButton = findViewById(R.id.button_login_google)

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        client = GoogleSignIn.getClient(this, options)

        materialButton.setOnClickListener {
            if (connectivityChecker.isConnectedToInternet()) {
                val intent = client.signInIntent
                startActivityForResult(intent, 10001)
            } else {
                Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Función llamada cuando se completa la actividad de inicio de sesión de Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10001) {
            if (connectivityChecker.isConnectedToInternet()) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val i = Intent(this, MainActivity::class.java)
                            startActivity(i)
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null){
            val i  = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }

}