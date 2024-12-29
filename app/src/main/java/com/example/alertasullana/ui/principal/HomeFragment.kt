package com.example.alertasullana.ui.principal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.location.LocationManager
import android.media.Image
import android.media.ImageReader
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.alertasullana.ui.MainActivity
import com.example.alertasullana.R
import com.example.alertasullana.data.network.ConnectivityChecker
import com.example.alertasullana.data.services.CameraResultListener
import com.example.alertasullana.ui.viewmodel.MarcadorViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener, ConnectivityChecker.ConnectivityChangeListener {

    //Variables para la cámara
    private lateinit var textureView: TextureView
    //Instancia de mapa de Google
    private lateinit var map: GoogleMap

    private lateinit var fab: FloatingActionButton
    //Fused Location instancia
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //Instancia de ImagenCapturaListener
    private var cameraResultListener: CameraResultListener? = null
    //Variable para saber si se ha solicitado el permiso de ubicación al menos una vez
    private var locationPermissionRequestedOnce = false
    //Instancia de ConnectivityChecker
    private val connectivityChecker: ConnectivityChecker by lazy { ConnectivityChecker(requireContext()) }
    //Instancia de MarcadorViewModel
    private lateinit var marcadorViewModel: MarcadorViewModel

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            // Cámara abierta, puedes proceder a tomar una foto aquí
            // Cámara abierta, puedes proceder a tomar una foto aquí
            val imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 2)
            val outputSurfaces = ArrayList<Surface>(2)
            outputSurfaces.add(imageReader.surface)
            outputSurfaces.add(Surface(textureView.surfaceTexture))

            val captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(imageReader.surface)
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

            // Aquí puedes configurar otros parámetros de la captura de la imagen si es necesario.
            val readerListener = object : ImageReader.OnImageAvailableListener {
                override fun onImageAvailable(reader: ImageReader?) {
                    var image: Image? = null
                    try {
                        image = reader?.acquireLatestImage()
                        val buffer = image?.planes?.get(0)?.buffer
                        val bytes = ByteArray(buffer?.capacity() ?: 0)
                        buffer?.get(bytes)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        // Aquí tienes tu Bitmap. Puedes pasarlo a HacerReporteFragment a través de un método en tu interfaz CameraResultListener.
                        // Aquí tienes tu Bitmap. Puedes pasarlo a HacerReporteFragment a través de un método en tu interfaz CameraResultListener.
                        (activity as MainActivity).onCameraResult(bitmap)
                    } finally {
                        image?.close()
                    }
                }
            }
            // Aquí puedes configurar otros parámetros de la captura de la imagen si es necesario.
            imageReader.setOnImageAvailableListener(readerListener, null)
            val captureListener = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                    super.onCaptureCompleted(session, request, result)
                    // Aquí puedes manejar el resultado de la captura de la imagen si es necesario.
                }
            }
            // Aquí puedes configurar otros parámetros de la captura de la imagen si es necesario.
            cameraDevice.createCaptureSession(outputSurfaces, object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, null)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }
                override fun onConfigureFailed(session: CameraCaptureSession) {
                    // Aquí puedes manejar el caso en que la configuración de la sesión de captura falla.
                }
            }, null)
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            // Cámara desconectada
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            // Ocurrió un error
        }
    }
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

        textureView = view?.findViewById(R.id.textureView) ?: TextureView(requireContext())
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                // Botón flotante para abrir la cámara
                fab = view?.findViewById(R.id.fab) ?: FloatingActionButton(requireContext())
                fab.setOnClickListener {
                    // Verificar la conectividad antes de realizar acciones que requieran Internet
                    if (connectivityChecker.isConnectedToInternet()) {
                        // Acciones que requieren Internet
                        if(areLocationPermissionsGranted()&&isGpsEnabled()){
                            // Si los permisos de ubicación están concedidos y el GPS está activado, abrir la cámara
                            if (areNotificationsEnabled()) {
                                // Continuar con la lógica de la cámara
                                openCamera()
                            } else {
                                // Mostrar un diálogo al usuario
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Habilitar notificaciones")
                                    .setMessage("Para recibir notificaciones, por favor, habilita las notificaciones para esta aplicación.")
                                    .setPositiveButton("Ir a configuración") { _, _ ->
                                        val intent = Intent().apply {
                                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                                            putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                                        }
                                        startActivity(intent)
                                    }
                                    .setNegativeButton("Cancelar") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .create()
                                    .show()
                            }
                        }else{
                            // Si no, solicitar los permisos o habilitar el GPS según sea necesario
                            requestLocationAndGps()
                        }
                    } else {
                        // Mostrar un mensaje al usuario indicando la falta de conexión
                        Toast.makeText(requireContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                // Ignorar
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                // Ignorar
            }
        }


        // Verificar la conectividad antes de realizar acciones que requieran Internet
        if (connectivityChecker.isConnectedToInternet()) {
            // Acciones que requieren Internet
            // Inicializa el fragmento del mapa
            createFragment()
            // Establece el color de fondo del fragmento
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBackgroundDark))
        } else {
            // Mostrar un mensaje al usuario indicando la falta de conexión
            Toast.makeText(requireContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }
        // Inicializar el ViewModel
        marcadorViewModel = ViewModelProvider(this).get(MarcadorViewModel::class.java)

        // Observar los marcadores y actualizar el mapa cuando cambien
        marcadorViewModel.marcadores.observe(viewLifecycleOwner, { marcadores ->
            // Actualizar el mapa con los nuevos marcadores
            // (debes implementar la lógica para actualizar tu mapa aquí)
            actualizarMapaConMarcadores(marcadores)
        })

        // Cargar los reportes (esto debería disparar automáticamente la actualización de los marcadores)
        marcadorViewModel.cargarReportes()




        // Botón para mostrar la ubicación actual del usuario
        val locationButton: FloatingActionButton = view.findViewById(R.id.fab_ubicacion)
        locationButton.setOnClickListener {
            if (connectivityChecker.isConnectedToInternet()) {
                // Acciones que requieren Internet
                if (!isMapFragmentInitialized()) {
                    // Si el fragmento del mapa no está inicializado, inicialízalo
                    createFragment()
                }
                // Verificar permisos de ubicación y GPS
                checkLocationAndGps()
            } else {
                // Mostrar un mensaje al usuario indicando la falta de conexión
                Toast.makeText(requireContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
    //==========================================================================================================

    //FUNCIONES PARA EL MAPA DESDE MAPSHEET, MOSTRAR MARCADOR
    private fun checkAndCenterMarker() {
        // Obtener la latitud y la longitud del Bundle
        val latitud = arguments?.getDouble("latitud")
        val longitud = arguments?.getDouble("longitud")

        // Si la latitud y la longitud no son nulas, centrar el mapa en la ubicación del delito
        if (latitud != null && longitud != null) {
            val ubicacionDelito = LatLng(latitud, longitud)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionDelito, 15f))
        }
    }

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
    // Agrega esta función para verificar si el fragmento del mapa ya está inicializado
    private fun isMapFragmentInitialized(): Boolean {
        return ::map.isInitialized
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

        // Llama a la función para comprobar y centrar el marcador
        checkAndCenterMarker()
    }

    private fun actualizarMapaConMarcadores(marcadores: List<MarkerOptions>) {
        Log.d("HomeFragment", "Actualizando mapa con ${marcadores.size} marcadores")

        if (::map.isInitialized) {
            // Borrar los marcadores existentes en el mapa
            map.clear()

            // Agregar los nuevos marcadores al mapa
            for (marcador in marcadores) {
                map.addMarker(marcador)
            }
        }
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
                if (location != null) {
                    // Verificar si las coordenadas del delito están presentes en los argumentos del fragmento
                    val latitudDelito = arguments?.getDouble("latitud")
                    val longitudDelito = arguments?.getDouble("longitud")

                    // Si las coordenadas del delito no están presentes, mover la cámara a la ubicación actual del usuario
                    if (latitudDelito == null || longitudDelito == null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
                    }
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
            // Verificar si las coordenadas del delito están presentes en los argumentos del fragmento
            val latitudDelito = arguments?.getDouble("latitud")
            val longitudDelito = arguments?.getDouble("longitud")

            // Si las coordenadas del delito no están presentes, habilitar la capa de ubicación en tiempo real
            if (latitudDelito == null || longitudDelito == null) {
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

    override fun onConnectivityChanged(isConnected: Boolean) {
        if (isConnected) {
            // Si hay conexión, inicializa el fragmento del mapa y realiza otras acciones necesarias
            createFragment()
            view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBackgroundDark))
        } else {
            // Si no hay conexión, muestra un mensaje al usuario
            Toast.makeText(requireContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
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
        val manager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = manager.cameraIdList[0]
            val permission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermission()
                return
            }
            manager.openCamera(cameraId, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
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
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            cameraResultListener?.onCameraResult(imageBitmap)
        }
    }*/

    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------
    //FUNCIONES PARA LA CÁMARA

    private fun areNotificationsEnabled(): Boolean {
        val notificationManager = NotificationManagerCompat.from(requireContext())
        return notificationManager.areNotificationsEnabled()
    }

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
