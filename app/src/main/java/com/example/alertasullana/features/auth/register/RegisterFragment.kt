package com.example.alertasullana.features.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.alertasullana.R
import com.example.alertasullana.databinding.FragmentRegisterBinding
import com.example.alertasullana.features.auth.state.AuthState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        lifecycleScope.launch {
            registerViewModel.authRegisterStateFlow.collect { authState ->
                when (authState) {
                    is AuthState.Success -> {
                        navController.navigate(R.id.action_registerFragment_to_mainActivity)
                    }
                    is AuthState.Error -> {
                        // Manejo de error si es necesario
                        val errorMessage = authState.message
                        Toast.makeText(context, "Error al registrarse: "+errorMessage, Toast.LENGTH_SHORT).show()
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
        binding.btnRegistrarse.setOnClickListener {
            val email = binding.etCorreoR.text.toString().trim()
            val password = binding.etPasswordR.text.toString().trim()
            registerViewModel.registerWithEmailAndPassword(email, password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
