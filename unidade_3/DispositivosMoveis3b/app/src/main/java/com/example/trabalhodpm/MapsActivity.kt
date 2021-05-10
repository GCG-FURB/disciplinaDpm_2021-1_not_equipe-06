package com.example.trabalhodpm

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.security.Permission
import java.security.Permissions
import java.security.Security

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var lastLocation: Location? = null
    private var isSatellite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        startLocationUpdate()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (!getLocationPermission()) return

        handleMap(mMap)
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
            return false;
        }

        return true;
    }

    @Throws(SecurityException::class)
    private fun handleMap(map: GoogleMap) {
        map.isMyLocationEnabled = true
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        val location = lastLocation?.let { LatLng(it.latitude, it.longitude) } ?: sydney
        map.moveCamera(CameraUpdateFactory.newLatLng(location))
        map.mapType = if (isSatellite) GoogleMap.MAP_TYPE_SATELLITE else GoogleMap.MAP_TYPE_NORMAL
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

    // Codigo copiado de: https://stackoverflow.com/questions/61032986/fusedlocations-doesnt-send-location-updates-when-map-ismylocationenabled-fals
    @Throws(SecurityException::class)
    fun startLocationUpdate() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                lastLocation = location
            }

        val locationRequest = LocationRequest()
        locationRequest.fastestInterval = 1000
        locationRequest.smallestDisplacement = 0f
        fusedLocationClient.requestLocationUpdates(locationRequest, object :
            LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                val latLng = LatLng(p0!!.lastLocation.latitude, p0.lastLocation.longitude)
                val cameraPosition = CameraPosition.builder().target(latLng).zoom(17f).build()
               // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }, Looper.getMainLooper())
    }

    fun alterarTipoMapa(view: View) {
        isSatellite = !isSatellite
        handleMap(mMap)
    }
}