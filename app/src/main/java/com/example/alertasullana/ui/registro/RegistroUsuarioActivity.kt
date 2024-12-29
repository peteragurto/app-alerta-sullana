package com.example.alertasullana.ui.registro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alertasullana.R
import com.example.alertasullana.data.network.ConnectivityChecker
import com.example.alertasullana.ui.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RegistroUsuarioActivity : AppCompatActivity() {
    private lateinit var materialButton: MaterialButton
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


        materialButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    // Función llamada cuando la actividad se inicia
    override fun onStart() {
        super.onStart()
        // Verificar si el usuario ya inició sesión
        if(FirebaseAuth.getInstance().currentUser != null){
            val i  = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }
    // Función para verificar si el usuario ya está registrado en Firestore
    private fun comprobacionUsuario() {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()


        // Verificar si el usuario ya está registrado
        db.collection("usuarios").document(user?.uid ?: "").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document != null && document.exists()) {
                        // El usuario ya está registrado
                        Toast.makeText(this, "Bienvenido de vuelta", Toast.LENGTH_SHORT).show()
                    } else {
                        // Guardar datos en Firestore
                        saveUserDataToFirestore(user?.uid)

                    }
                } else {
                    Toast.makeText(this, "Error al verificar usuario en Firestore", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Función para guardar los datos del usuario en Firestore por primera vez
    private fun saveUserDataToFirestore(uid: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        // Crear un objeto con los datos que deseas almacenar
        val userData = hashMapOf(
            "uid" to uid,
            "nombre" to user?.displayName,
            "correo" to user?.email,
            "fechaRegistro" to FieldValue.serverTimestamp()
        )

        // Agregar datos a Firestore
        db.collection("usuarios").document(uid ?: "").set(userData)
            .addOnSuccessListener {
                // Éxito al guardar en Firestore
                Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Error al guardar en Firestore
                Toast.makeText(this, "Error al registrarse: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}