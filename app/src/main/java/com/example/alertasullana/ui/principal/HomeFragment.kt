package com.example.alertasullana.ui.principal

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alertasullana.R
import com.example.alertasullana.data.services.CameraResultListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    //Instancia de mapa de Google
    private lateinit var map: GoogleMap
    //Fused Location instancia
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //Instancia de ImagenCapturaListener
    private var cameraResultListener: CameraResultListener? = null
    //Variable para saber si se ha solicitado el permiso de ubicación al menos una vez
    private var locationPermissionRequestedOnce = false

    companion object {
        //Código para permisos de ubicación
        private val LOCATION_PERMISSION_REQUEST = 1
        const val REQUEST_CODE_LOCATION = 0
        //Códigos para permisos de la cámara
        private const val PERMISSION_REQUEST_CODE = 100
        private const val REQUEST_IMAGE_CAPTURE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Obtén la referencia a la actividad y configura el listener
        activity?.let {
            if (it is CameraResultListener) {
                cameraResultListener = it
            }
        }

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // Inicializa el fragmento del mapa
        createFragment()
        // Establece el color de fondo del fragmento
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBackgroundDark))

        // Botón flotante para abrir la cámara
        val fab: FloatingActionButton = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            // Solicitar permisos de cámara
            openCamera()
        }

        // Botón para mostrar la ubicación actual del usuario
        val locationButton: FloatingActionButton = view.findViewById(R.id.fab_ubicacion)
        locationButton.setOnClickListener {
            checkLocationAndGps()
        }

        return view
    }
    //==========================================================================================================

    //FUNCIONES PARA EL MAPA
    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------
    // Función para inicializar el fragmento del mapa
    private fun createFragment() {
        // Inicializar fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        // Inicializar el fragmento del mapa
        val mapFragment : SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Callback cuando el mapa está listo para su uso
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Aplica un estilo personalizado al mapa
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.estilo_oscuro))

        // Configura la ubicación inicial (Piura, Perú)
        val piuraLatLng = LatLng(-5.1945, -80.6328)
        // Mueve la cámara del mapa a la ubicación inicial con un nivel de zoom específico
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(piuraLatLng, 8f))

        // Desactivar el botón de ubicación en tiempo real
        map.uiSettings.isMyLocationButtonEnabled = false

        // Activa la funcionalidad de ubicación en tiempo real
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            requestLocationPermission()
        }
        // Configura escuchadores de eventos en el mapa
        map.setOnMapClickListener(this)
    }

    // Verifica si los permisos de ubicación están concedidos y si el GPS está activado
    private fun checkLocationAndGps() {
        if (areLocationPermissionsGranted() && isGpsEnabled()) {
            showCurrentLocation()
        } else {
            requestLocationAndGps()
        }
    }

    // Verifica si los permisos de ubicación están concedidos
    private fun areLocationPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Verifica si el GPS está activado
    private fun isGpsEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // Solicita los permisos de ubicación y habilita el GPS
    private fun requestLocationAndGps() {
        if (!areLocationPermissionsGranted()) {
            requestLocationPermission()
        }
        // Si los permisos de ubicación están concedidos, pero el GPS está desactivado, mostrar un diálogo para activarlo
        if (!isGpsEnabled()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Activar GPS")
                .setMessage("Para utilizar la función de ubicación, por favor, activa el GPS.")
                .setPositiveButton("Aceptar") { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, REQUEST_CODE_LOCATION)
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    // Muestra la ubicación actual del usuario en el mapa
    private fun showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Si los permisos de ubicación están concedidos, obtén la ubicación actual del usuario
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    // Si la ubicación no es nula, mueve la cámara del mapa a la ubicación actual del usuario
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
                } ?: run {
                    // Manejar el caso en el que la ubicación es nula
                    Toast.makeText(
                        requireContext(),
                        "No se pudo obtener la ubicación actual",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    // Solicita los permisos de ubicación al usuario
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                // Si el usuario ha rechazado los permisos al menos una vez, mostrar un diálogo explicando por qué se necesitan
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Mostrar diálogo explicando por qué se necesita el permiso
            AlertDialog.Builder(requireActivity())
                .setTitle("Permiso de ubicación necesario")
                .setMessage("Esta aplicación necesita el permiso de ubicación para mostrar tu ubicación en el mapa.")
                .setPositiveButton("Aceptar") { _, _ ->
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST
                    )
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()

        } else {
            // Si no se ha mostrado la solicitud al menos una vez, mostrarla
            if (!locationPermissionRequestedOnce) {
                locationPermissionRequestedOnce = true
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST
                )
            } else {
                // Verificar si los permisos ya están concedidos
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Los permisos ya están concedidos, mostrar un mensaje al usuario indicando que la aplicación no funcionará sin permisos de ubicación
                    // Puedes personalizar este mensaje según tus necesidades
                    Toast.makeText(
                        requireContext(),
                        "Esta aplicación requiere permisos de ubicación y GPS activado para funcionar correctamente.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    // Mostrar el diálogo para abrir la configuración de la aplicación
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Activa la ubicación")
                        .setMessage("Para habilitar los permisos de ubicación, ve a Configuración -> Aplicaciones -> Alerta Sullana -> Permisos.")
                        .setPositiveButton("Abrir configuración") { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        .setNegativeButton("Cancelar") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            }
        }
    }


    // Habilita la capa de ubicación en tiempo real del mapa
    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            // Mueve la cámara del mapa a la ubicación actual del usuario
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13f))
                }
            }
        }
    }


    // Callback cuando el usuario hace clic en el mapa
    override fun onMapClick(p0: LatLng) {
        if (areLocationPermissionsGranted() && isGpsEnabled()) {
            // Si los permisos de ubicación están concedidos y el GPS está activado, mostrar un mensaje al usuario
            Toast.makeText(
                requireContext(),
                "Presione el botón derecho superior para ir a su ubicación en tiempo real.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            // Si no, solicitar los permisos o habilitar el GPS según sea necesario
            requestLocationPermission()
        }
    }

    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------
    //FUNCIONES PARA EL MAPA


    //FUNCIONES PARA LA CÁMARA
    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------

    //Abrir la cámara
    fun openCamera() {
        if (checkPermission()) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        } else {
            requestPermission()
        }
    }

    //Verificar permisos de la cámara
    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    //Solicitar permisos de la cámara al usuario
    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    // Callback para manejar el resultado de la cámara
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            cameraResultListener?.onCameraResult(imageBitmap)
        }
    }

    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------
    //FUNCIONES PARA LA CÁMARA

    //FUNCIONES PARA LOS PERMISOS
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> handleLocationPermissionsResult(grantResults)
            PERMISSION_REQUEST_CODE -> handleCameraPermissionsResult(grantResults)
            // Agrega más casos según sea necesario para otras solicitudes de permisos
        }
    }

    // Callback para manejar el resultado de los permisos de ubicación
    @SuppressLint("MissingPermission")
    private fun handleLocationPermissionsResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Si se concedieron, habilita la capa de ubicación en tiempo real del mapa
            enableMyLocation()
        } else {
            // Si se denegaron, muestra un mensaje al usuario
            Toast.makeText(
                requireContext(),
                "Para activar la localización ve a ajustes y acepta los permisos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Callback para manejar el resultado de la cámara
    private fun handleCameraPermissionsResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Permiso concedido", Toast.LENGTH_SHORT).show()
            // Ahora, puedes continuar con la lógica relacionada con la cámara si es necesario
            if (checkPermission()) {
                openCamera()
            }
        } else {
            Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

}
