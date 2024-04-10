package com.example.alertasullana.features.auth.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.alertasullana.R
import com.example.alertasullana.databinding.FragmentLoginBinding
import com.example.alertasullana.features.auth.state.AuthState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController : NavController

    private val loginViewModel: LoginViewModel by viewModels()

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { loginViewModel.signUpWithGoogle(it) }
            } catch (e: ApiException) {
                loginViewModel.setAuthState(AuthState.Error(e.message!!))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        lifecycleScope.launch {
            loginViewModel.authStateFlow.collect { authState ->
                when (authState) {
                    is AuthState.Success -> {
                        //El inicio de sesión es válido
                        Toast.makeText(context, "Iniciando sesión", Toast.LENGTH_SHORT).show()
                        navController.navigate(R.id.action_loginFragment_to_mainActivity)
                    }
                    is AuthState.Error -> {
                        // Manejo de error si es necesario
                        val errorMessage = authState.message
                        Toast.makeText(context, "Error iniciando sesión: "+errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    // Otros estados como Loading
                    else -> {}
                }
            }
        }

        initUI()
    }

    private fun initUI() {
        initListeners()
    }

    private fun initListeners() {
        binding.btnGoToRegister.setOnClickListener { navController.navigate(R.id.action_loginFragment_to_registerFragment) }

        binding.btnLoginEP.setOnClickListener {
            val email = binding.etCorreo.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            loginViewModel.signUpWithEmailAndPassword(email, password)
        }

        binding.btnLoginGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}