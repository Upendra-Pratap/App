package com.pasco.pascocustomer.Driver.UpdateLocation.Ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.pasco.pascocustomer.Driver.DriverDashboard.Ui.DriverDashboardActivity
import com.pasco.pascocustomer.Driver.UpdateLocation.UpdateLocationViewModel
import com.pasco.pascocustomer.Driver.UpdateLocation.UpdationLocationBody
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.databinding.ActivityUpdateLocationBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class UpdateLocationActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMapClickListener {
    private lateinit var binding: ActivityUpdateLocationBinding
    private var googleMap: GoogleMap? = null
    private lateinit var autoCompleteFragment: AutocompleteSupportFragment
    private val locationList = mutableListOf<LatLng>()
    private var address: String? = null
    private var city: String? = null
    private var pickUplatitude = 0.0
    private var pickUplongitude = 0.0
    var formattedLatitudeSelect: String = ""
    var formattedLongitudeSelect: String = ""
    private val updateLocationViewModel: UpdateLocationViewModel by viewModels()
    private lateinit var activity: Activity
    private var userId=""
    private var bookingId=""
    private lateinit var updateLocationBody: UpdationLocationBody
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        userId = PascoApp.encryptedPrefs.userId
        bookingId = intent.getStringExtra("reqId").toString()


        //call observer
        updateLocationObserver()

        binding.backArrowUpLoc.setOnClickListener {
            finish()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Places.initialize(applicationContext, getString(R.string.google_key))
        autoCompleteFragment =
            supportFragmentManager.findFragmentById(R.id.place_autocompleteUpdateActAct) as AutocompleteSupportFragment
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
                    this@UpdateLocationActivity,
                    "Error: ${status.statusMessage} (${status.statusCode})",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onPlaceSelected(place: Place) {
                if (place?.address != null) {
                    val latLng = place.latLng!!

                    // Add the location to the list
                    locationList.add(latLng)

                    // Set the clicked location in the existing AutoCompleteTextView
                    binding.txtUserAddressUpLoc.setText(place.address)

                    zoomOnMap(latLng)
                    //updateMapMarkers()
                    updateMarkers(latLng)
                } else {
                    Toast.makeText(
                        this@UpdateLocationActivity,
                        "Error: Unable to get location details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        val mapFragment = fragmentManager.findFragmentById(R.id.mapUpLoc) as MapFragment
        mapFragment.getMapAsync(this)


        binding.txtSelectLocationUpLoc.setOnClickListener(View.OnClickListener { view: View? ->

            if (myPlace == null) {
                myPlace = binding.txtUserAddressUpLoc!!.text.toString().trim { it <= ' ' }
            } else {
                myPlace = binding.txtUserAddressUpLoc!!.text.toString().trim { it <= ' ' }
            }

            Log.e("onItemSelected", "myPlace  $myPlace")
            finish()
        })

        binding.imgCurrentUpLoc.setOnClickListener {
            showCurrentLocation()
        }
        binding.txtSelectLocationUpLoc.setOnClickListener(View.OnClickListener { view: View? ->

            if (formattedLatitudeSelect.isNullOrBlank()) {
                Toast.makeText(this@UpdateLocationActivity, "Latitude is empty. Please select a valid latitude.", Toast.LENGTH_SHORT).show()
            } else if (formattedLongitudeSelect.isNullOrBlank()) {
                Toast.makeText(this@UpdateLocationActivity, "Longitude is empty. Please select a valid longitude.", Toast.LENGTH_SHORT).show()
            } else {
                // Your code for handling non-empty latitude and longitude
                // call update Api
                //call api
                updateLocationDetails()

            }

        })
    }

    private fun updateLocationObserver() {

        updateLocationViewModel.mUpdateLocationResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status.equals("False")) {
                Toast.makeText(this@UpdateLocationActivity, "$message", Toast.LENGTH_LONG).show()
            } else {

                Toast.makeText(this@UpdateLocationActivity, "$message", Toast.LENGTH_LONG).show()
                val intent = Intent(this@UpdateLocationActivity, DriverDashboardActivity::class.java)
                startActivity(intent)
            }
        }
        updateLocationViewModel.errorResponse.observe(this@UpdateLocationActivity) {
            ErrorUtil.handlerGeneralError(this@UpdateLocationActivity, it)
            // errorDialogs()
        }
    }

    private fun updateLocationDetails() {
        updateLocationBody = UpdationLocationBody(
            city.toString(),
            address.toString(),
            formattedLatitudeSelect,
            formattedLongitudeSelect)
        updateLocationViewModel.updateLocationDriver(
            activity,
            updateLocationBody)
    }
    private fun updateMarkers(latLng: LatLng) {
        // Check if the GoogleMap object is not null
        googleMap?.let {
            // Remove previous markers if needed
            it.clear()
            // Add a marker at the specified LatLng
            it.addMarker(MarkerOptions().position(latLng).title("Selected Place"))
            // Optionally, you can animate the camera to focus on the marker
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        googleMap?.setOnMapClickListener(this)
        showCurrentLocation()
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

    private fun zoomOnMap(latLng: LatLng) {
        val newLatLongZoom = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        googleMap?.animateCamera(newLatLongZoom)

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

    @SuppressLint("SetTextI18n")
    private fun getAddressFromLocation(location: LatLng) {
        val latitude = location.latitude
        val longitude = location.longitude

        pickUplatitude = latitude
        pickUplongitude = longitude
        formattedLatitudeSelect= String.format("%.5f", pickUplatitude)
        formattedLongitudeSelect = String.format("%.5f", pickUplongitude)

        Log.e("TAGG", "getAddressFromLocation: formattedLatitudeSelect=$formattedLatitudeSelect," +
                " formattedLongitudeSelect=$formattedLongitudeSelect")

        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )

            if (addresses != null && addresses.isNotEmpty()) {
                address = addresses[0].getAddressLine(0) ?: "Address not available"
                //  binding.txtUserAddress.text = "Address: $address"
                city = addresses[0].locality ?: "City not available"
                binding?.txtUserAddressUpLoc?.setText(address)
            } else {
                binding?.txtUserAddressUpLoc?.setText("Address not found")
            }

        } catch (e: IOException) {
            e.printStackTrace()
            binding?.txtUserAddressUpLoc?.setText("Error getting address")

        }
    }

    companion object {
        var REQUEST_LOCATION_PERMISSION: Int =1
        var myPlace: String=""
    }

    override fun onMapClick(p0: LatLng) {
        googleMap?.clear() // Clear existing markers
        googleMap?.addMarker(MarkerOptions().position(p0).title("Selected Location"))
        getAddressFromLocation(p0)
    }
}