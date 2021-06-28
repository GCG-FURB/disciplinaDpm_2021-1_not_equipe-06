package com.furb.br.nathan.tourguider

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.furb.br.nathan.tourguider.objects.Place
import com.furb.br.nathan.tourguider.objects.Route
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.serialization.json.Json
import java.nio.charset.Charset

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var bottomSheet: View
    private lateinit var descricaoEditText: EditText
    private lateinit var tituloEditText: EditText
    private lateinit var addImageButton: ImageButton

    private var lastLocation: Location? = null

    private val placeList: ArrayList<Place> = ArrayList()
    private val markerPlaceDictionary: HashMap<Marker, Place> = HashMap()
    private lateinit var selectedMarker: Marker
    private var currentLine: Polyline? = null

    private var updateCamera: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        bottomSheet = findViewById(R.id.bottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheet.visibility = View.INVISIBLE
        bottomSheetBehavior.isHideable = true

        descricaoEditText = findViewById(R.id.descricao_edit_text)
        descricaoEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                markerPlaceDictionary[selectedMarker]?.description = s.toString()
            }
        })

        tituloEditText = findViewById(R.id.titulo_edit_text)
        tituloEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                markerPlaceDictionary[selectedMarker]?.name = s.toString()
            }
        })

        val removeButton = findViewById<Button>(R.id.remover_marker_button)
        removeButton.setOnClickListener { removeMarker(selectedMarker) }
        val concluidoButton = findViewById<Button>(R.id.concluir_edicao_button)
        concluidoButton.setOnClickListener { bottomSheet.visibility = View.INVISIBLE }
        val saveButton = findViewById<FloatingActionButton>(R.id.save_route)
        saveButton.setOnClickListener { onSave() }
        addImageButton = findViewById(R.id.take_imagem_button)
        addImageButton.setOnClickListener { takeCurrentPicture() }

        startLocationUpdate()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // https://medium.com/@hasangi/capture-image-or-choose-from-gallery-photos-implementation-for-android-a5ca59bc6883
    private fun takeCurrentPicture() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your profile picture")

        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, 0)
                }
                options[item] == "Choose from Gallery" -> {
                    val pickPhoto =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, 1)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (!getLocationPermission()) return

        googleMap.setOnMapClickListener { placeMarker(it) }
        googleMap.setOnMarkerClickListener { handleMarkerClick(it) }
        handleMap(mMap)
    }

    private fun handleMarkerClick(marker: Marker): Boolean {
        selectedMarker = marker
        bottomSheet.visibility = View.VISIBLE

        val place = markerPlaceDictionary[marker]!!
        tituloEditText.setText(place.name)
        descricaoEditText.setText(place.description)
        setImageButton(place)


        return true
    }

    private fun setImageButton(place: Place) {
        if (place.image != null) {
            addImageButton.setImageBitmap(place.image)
            return
        }

        val bitmap = BitmapFactory.decodeResource(
            resources,
            android.R.drawable.ic_input_add
        )

        addImageButton.setImageBitmap(bitmap)

    }

    private fun removeMarker(marker: Marker) {
        val place = markerPlaceDictionary[marker]!!
        marker.remove()
        markerPlaceDictionary.remove(marker)

        placeList.remove(place)
        adjustOrder(place.order)
        bottomSheet.visibility = View.INVISIBLE

        renderLines()
    }

    private fun adjustOrder(order: Int) {
        placeList.forEach { if (it.order > order) it.order-- }
        placeList.sortBy { order }
    }

    private fun getLocationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return false
        }

        return true
    }

    private fun placeMarker(location: LatLng) {
        val markerOptions = MarkerOptions()
        markerOptions.position(location)
        val marker = mMap.addMarker(markerOptions)

        createPlace(marker, location)
        renderLines()
        handleMarkerClick(marker)
    }

    private fun createPlace(marker: Marker, position: LatLng) {
        val order = placeList.size + 1
        val place = Place(position.latitude, position.longitude, order, name = "Marcador $order")
        placeList.add(place)
        markerPlaceDictionary[marker] = place
    }

    private fun renderLines() {
        currentLine?.remove()
        val polylineOptions = PolylineOptions()
        polylineOptions.addAll(placeList.map { LatLng(it.latitude, it.longitude) })
        currentLine = mMap.addPolyline(polylineOptions)
    }

    @Throws(SecurityException::class)
    private fun handleMap(map: GoogleMap) {
        map.isMyLocationEnabled = true
        val location = lastLocation?.let { LatLng(it.latitude, it.longitude) }
        if (location != null)
            map.moveCamera(CameraUpdateFactory.newLatLng(location))
        renderLines()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            handleMap(mMap)
        }
    }

    // Código copiado de: https://stackoverflow.com/questions/61032986/fusedlocations-doesnt-send-location-updates-when-map-ismylocationenabled-fals
    @Throws(SecurityException::class)
    fun startLocationUpdate() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                lastLocation = it
            }

        val locationRequest = LocationRequest()
        locationRequest.fastestInterval = 1000
        locationRequest.smallestDisplacement = 0f

        fusedLocationClient.requestLocationUpdates(locationRequest, object :
            LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                if (!updateCamera) return

                val latLng = LatLng(p0!!.lastLocation.latitude, p0.lastLocation.longitude)
                val cameraPosition = CameraPosition.builder().target(latLng).zoom(17f).build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                updateCamera = false
            }
        }, Looper.getMainLooper())

    }

    // https://medium.com/@hasangi/capture-image-or-choose-from-gallery-photos-implementation-for-android-a5ca59bc6883
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                    addImageButton.setImageBitmap(selectedImage)
                    markerPlaceDictionary[selectedMarker]?.image = selectedImage
                }
                1 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage: Uri? = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    if (selectedImage != null) {
                        val cursor: Cursor? = contentResolver.query(
                            selectedImage,
                            filePathColumn, null, null, null
                        )
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath: String = cursor.getString(columnIndex)
                            val selectedBitmap = BitmapFactory.decodeFile(picturePath)
                            addImageButton.setImageBitmap(selectedBitmap)
                            markerPlaceDictionary[selectedMarker]?.image = selectedBitmap
                            cursor.close()
                        }
                    }
                }
            }
        }
    }

    private fun onSave() {
        if (placeList.isNotEmpty()) {
            sendRoute()
        }

        setResult(RESULT_OK)
        finish()
    }

    // https://kotlinlang.org/docs/serialization.html#example-json-serialization
    // https://riis.com/blog/sending-requests-using-android-volley/
    private fun sendRoute() {
        val route = Route(placeList)
        val json = Json.encodeToString(Route.serializer(), route)
        val url = "https://60d94dcaeec56d00174776a6.mockapi.io/Route"

        val queue = Volley.newRequestQueue(this)

        // Request a string response from the provided URL.
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener {
                },
                Response.ErrorListener {
                }
            ) {
                override fun getBody(): ByteArray {
                    return json.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringRequest)
    }
}