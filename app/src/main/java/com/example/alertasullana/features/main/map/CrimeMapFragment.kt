package com.example.alertasullana.features.main.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.alertasullana.databinding.FragmentCrimeMapBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CrimeMapFragment : Fragment() {
    private var _binding : FragmentCrimeMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val crimeMapViewModel : CrimeMapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCrimeMapBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        initUI()
    }

    private fun initUI() {
        TODO("Not yet implemented")
    }
}