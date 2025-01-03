package com.pasco.pascocustomer.location

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityLocationsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class LocationsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityLocationsBinding
    private var googleMap: GoogleMap? = null
    private lateinit var autoCompleteFragment: AutocompleteSupportFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        var REQUEST_LOCATION_PERMISSION: Int = 1
        var myPlace: String = ""
    }
    private var pickYourLocation = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.setBtn.setOnClickListener {
            myPlace = if (myPlace == null) {
                binding.setLocationTxt!!.text.toString().trim { it <= ' ' }
            } else {
                binding.setLocationTxt!!.text.toString().trim { it <= ' ' }
            }

            finish()
        }

        pickYourLocation = intent.getStringExtra("pickYourLocation").toString()

        if (pickYourLocation == "pickYourLocation") {
            binding.locationTxt.text = "Pic Start Points"
        } else {
            binding.locationTxt.text = "Pic Destination Points"
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Places.initialize(applicationContext, getString(R.string.google_key))
        autoCompleteFragment = supportFragmentManager.findFragmentById(R.id.autoCompleteFrgment) as AutocompleteSupportFragment
        autoCompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )

        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
                Toast.makeText(
                    this@LocationsActivity,
                    "Error: ${status.statusMessage} (${status.statusCode})",
                    Toast.LENGTH_SHORT
                ).show()

                Log.e(
                    "LocationActivitys",
                    "Place selection error: ${status.statusMessage} (${status.statusCode})"
                )
            }

            override fun onPlaceSelected(place: Place) {
                if (place?.address != null) {
                    val latLng = place.latLng!!
                    binding.setLocationTxt.text = place.address
                    zoomOnMap(latLng)
                } else {
                    Toast.makeText(
                        this@LocationsActivity,
                        "Error: Unable to get location details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun zoomOnMap(latLng: LatLng) {
        val newLatLongZoom = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        googleMap?.animateCamera(newLatLongZoom)

    }


    private fun showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        googleMap?.addMarker(
                            MarkerOptions().position(currentLatLng).title("Current Location")
                        )
                        zoomOnMap(currentLatLng)

                        getAddressFromLocation(currentLatLng)
                    } else {
                        Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show current location
                showCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        showCurrentLocation()
        googleMap!!.setOnMapClickListener { latLng ->
            // Clear previous markers
            googleMap!!.clear()


            // Add a marker at the clicked position
            val markerOptions = MarkerOptions().position(latLng)
            googleMap!!.addMarker(markerOptions)

            // Move camera to the clicked position
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))

            // Get the address from the clicked position
            val address = getAddressFromLatLng(latLng.latitude, latLng.longitude)
            binding.setLocationTxt.text = "$address"
        }
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    return address.getAddressLine(0) ?: "No address found"
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Error retrieving address"
    }


    private fun getAddressFromLocation(location: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )

            if (addresses != null && addresses.isNotEmpty()) {
                val address: String = addresses[0].getAddressLine(0) ?: "Address not available"
                binding.setLocationTxt.text = "$address"
            } else {
                binding.setLocationTxt.text = "Address not found"
            }

        } catch (e: IOException) {
            e.printStackTrace()
            binding.setLocationTxt.text = "Error getting address"
        }
    }
}