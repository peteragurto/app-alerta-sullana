package com.example.alertasullana.ui.principal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alertasullana.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMapClickListener {

    private lateinit var map: GoogleMap

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // Inicializa el fragmento del mapa
        createFragment()
        // Establece el color de fondo del fragmento
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBackgroundDark))
        return view
    }

    // Función para inicializar el fragmento del mapa
    private fun createFragment() {
        val mapFragment : SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Callback cuando el mapa está listo para su uso
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Crea y muestra un marcador en una ubicación específica
        createMarker()
        // Activa la funcionalidad de ubicación en tiempo real
        enableLocation()
        // Configura escuchadores de eventos en el mapa
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMapClickListener(this)
        // Aplica un estilo personalizado al mapa
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.estilo_oscuro))
    }

    // Crea un marcador en una ubicación específica
    private fun createMarker() {
        val coordinates = LatLng(-4.909004, -80.694229)
        val marker: MarkerOptions = MarkerOptions().position(coordinates).title("Sullana")
        map.addMarker(marker)
        // Centra la cámara en la ubicación del marcador con un zoom específico
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            2000, null
        )
    }

    // Verifica si se han concedido los permisos de ubicación
    private fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    // Activa la funcionalidad de ubicación en tiempo real
    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        if (!::map.isInitialized) return
        // Verifica si se han concedido los permisos de ubicación
        if (isLocationPermissionGranted()) {
            // Habilita la capa de ubicación en tiempo real del mapa
            map.isMyLocationEnabled = true
        } else {
            // Si no se han concedido permisos, solicita al usuario que los conceda
            requestLocationPermission()
        }
    }

    // Solicita permisos de ubicación al usuario
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Muestra un mensaje explicativo al usuario sobre la necesidad de permisos
            Toast.makeText(requireContext(), "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            // Si no se deben mostrar explicaciones, solicita permisos directamente
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    // Callback para manejar la respuesta del usuario a la solicitud de permisos
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                // Verifica si el usuario concedió los permisos
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Si se concedieron, habilita la capa de ubicación en tiempo real del mapa
                    map.isMyLocationEnabled = true
                } else {
                    // Si se denegaron, muestra un mensaje al usuario
                    Toast.makeText(
                        requireContext(),
                        "Para activar la localización ve a ajustes y acepta los permisos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Callback para manejar la resolución al reanudar la aplicación
    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (!::map.isInitialized) return
        // Verifica si los permisos de ubicación están activados
        if (!isLocationPermissionGranted()) {
            // Si no están activados, desactiva la capa de ubicación en tiempo real del mapa
            map.isMyLocationEnabled = false
            // Muestra un mensaje al usuario
            Toast.makeText(
                requireContext(),
                "Activa tu ubicación en ajustes",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Verifica si la ubicación está activada al reanudar la aplicación
    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // Callback para manejar el clic en el botón de ubicación en tiempo real
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(requireContext(), "Viajando a tu ubicación actual", Toast.LENGTH_SHORT).show()
        return false
    }

    // Callback para manejar el clic en el mapa
    override fun onMapClick(p0: LatLng) {
        Toast.makeText(requireContext(), "Estas en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }
}
