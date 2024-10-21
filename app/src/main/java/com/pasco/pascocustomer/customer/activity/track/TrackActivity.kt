package com.pasco.pascocustomer.customer.activity.track

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.chat.ChatActivity
import com.pasco.pascocustomer.customer.activity.track.cancelbooking.CancelBookingBody
import com.pasco.pascocustomer.customer.activity.track.cancelbooking.CancelBookingModelView
import com.pasco.pascocustomer.customer.activity.track.trackmodel.TrackLocationBody
import com.pasco.pascocustomer.customer.activity.track.trackmodel.TrackLocationModelView
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityTrackBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Suppress("DEPRECATION")
@AndroidEntryPoint
class TrackActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var pickupLocation: LatLng // Define your pickup location
    private var dropLocation: LatLng? = null// Define your drop location

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityTrackBinding
    private val cancelBookingModelView: CancelBookingModelView by viewModels()
    private val trackModelView: TrackLocationModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private var isDestinationReached = false

    private var pickupLatitude = ""
    private var pickupLongitude = ""
    private var dropLatitude = ""
    private var dropLongitude = ""
    private var bookingId = ""
    private var lat = ""
    private var long = ""
    private var isClick = true

    private var handler: Handler? = null
    private lateinit var runnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageBackReqRide.setOnClickListener { finish() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapsa) as SupportMapFragment
        mapFragment.getMapAsync(this)

        pickupLatitude = intent.getStringExtra("pickupLatitude").toString()
        pickupLongitude = intent.getStringExtra("pickupLongitude").toString()
        bookingId = intent.getStringExtra("bookingId").toString()
        //dropLatitude = intent.getStringExtra("dropLatitude").toString()
        //  dropLongitude = intent.getStringExtra("dropLongitude").toString()

        binding.textViewSeeDetails.setOnClickListener {

            if (isClick) {
                binding.textViewSeeDetails.text = "Hide Details"
                binding.NewConstraintDetails.visibility = View.VISIBLE
                isClick = false
            } else {
                binding.NewConstraintDetails.visibility = View.GONE
                binding.textViewSeeDetails.text = "Show Details"
                isClick = true
            }
        }

        // Los Angeles
        binding.cancelBookingBtn.setOnClickListener {
            showCancelPopup()
        }

        binding.chatBtn.setOnClickListener {
            val intent = Intent(this@TrackActivity, ChatActivity::class.java)
            startActivity(intent)
        }
        handler = Handler(Looper.getMainLooper())


        runnable = object : Runnable {
            override fun run() {
                locationApi()
                locationObserver()
                handler?.postDelayed(this, 2000) // 2000 milliseconds = 2 seconds
            }
        }

        // Start the periodic task
        handler?.post(runnable)



        locationLatApi()
        locationLatObserver()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isTrafficEnabled = true
        // Add markers for pickup and drop locations

        // Enable my location button and request location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                dropLocation?.let { it1 -> updateRoute(LatLng(it.latitude, it.longitude), it1) }
            }
        }
        mMap.isMyLocationEnabled = true

    }


    private fun getLastLocationAndDrawRoute() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    drawRoute(LatLng(location.latitude, location.longitude))
                    Log.e(
                        "location",
                        "location.." + location.latitude + "longitude " + location.longitude
                    )
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to get location: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun checkDestinationReached(userLocation: LatLng, destinationLocation: LatLng) {
        val distanceToDestination = calculateDistance(userLocation, destinationLocation)
        val thresholdDistance = 100 // Define your threshold distance in meters

        if (distanceToDestination <= thresholdDistance && !isDestinationReached) {
            // Destination reached
            isDestinationReached = true
            Toast.makeText(this, "You have reached your destination!", Toast.LENGTH_SHORT).show()
            // Perform any action you want when the destination is reached
        }
    }

    // Calculate distance between two LatLng points using Haversine formula
    private fun calculateDistance(startLatLng: LatLng, endLatLng: LatLng): Float {
        val earthRadius = 6371000 // Radius of the Earth in meters
        val dLat = Math.toRadians((endLatLng.latitude - startLatLng.latitude).toDouble())
        val dLng = Math.toRadians((endLatLng.longitude - startLatLng.longitude).toDouble())
        val a = (sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(startLatLng.latitude)) * cos(Math.toRadians(endLatLng.latitude)) *
                sin(dLng / 2) * sin(dLng / 2))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadius * c).toFloat()
    }

    private fun drawRoute(latLng: LatLng) {
        val apiKey = "AIzaSyA3KVnFOiaKNlhi4hJB8N2pB8tyoe_rRxQ" // Replace with your actual API key
        val context = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()


        val result: DirectionsResult = DirectionsApi.newRequest(context)
            .mode(TravelMode.DRIVING)
            .origin("${latLng.latitude},${latLng.longitude}")
            .destination("${dropLocation?.latitude},${dropLocation?.longitude}")
            .await()

        Log.e(
            "location",
            "location.." + dropLocation?.latitude + "longitude " + dropLocation?.longitude
        )
        // Decode polyline and draw on map
        val points = decodePolyline(result.routes[0].overviewPolyline.encodedPath)

        // Add polyline to map
        mMap.addPolyline(PolylineOptions().addAll(points).color(R.color.earth_yellow))

        //mMap.clear() // Clear previous route

    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
            lng += dlng
            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }


    private fun locationApi() {

        val bookingBody = TrackLocationBody(
            driverbookedid = bookingId
        )
        trackModelView.trackLocation(bookingBody, this)
    }

    private fun locationObserver() {

        trackModelView.mRejectResponse.observe(this) { response ->

            val dataGet = response.peekContent().data

            binding.pickUpLocBidd.text = response.peekContent().data?.pickupLocation
            binding.dropLocBidd.text = response.peekContent().data?.dropLocation
            binding.orderIdStaticTextView.text = response.peekContent().data?.bidPrice.toString()
            /*         val distance = response.peekContent().data?.totalDistance ?: 0.0
            binding.routeTime.text = String.format("%1f", distance)*/

            val kilometers = response.peekContent().data?.totalDistance
            val meters = convertKilometersToMeters(kilometers!!)
            binding.totalDistanceBidd.text = "Kilometers: $kilometers\nMeters: $meters"

            val url = response.peekContent().data!!.image
            Glide.with(this).load(BuildConfig.IMAGE_KEY + url).into(binding.profileImgUserBid)
            Log.e("AAAAAA", "0001")

        }

        trackModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun locationLatApi() {

        val bookingBody = TrackLocationBody(
            driverbookedid = bookingId
        )
        trackModelView.trackLocation(bookingBody, this)
    }

    private fun locationLatObserver() {

        trackModelView.mRejectResponse.observe(this) { response ->

            dropLatitude = response.peekContent().data?.currentLatitude.toString()
            dropLongitude = response.peekContent().data?.currentLongitude.toString()
            pickupLocation = LatLng(pickupLatitude.toDouble(), pickupLongitude.toDouble())
            dropLocation = LatLng(dropLatitude.toDouble(), dropLongitude.toDouble())

            mMap.addMarker(MarkerOptions().position(pickupLocation!!).title("Pickup Location"))
            mMap.addMarker(MarkerOptions().position(dropLocation!!).title("Drop Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation!!, 12f))

            if (response.peekContent().data?.driverStatus == null) {

            } else {
                binding.onTheWayTxt.text = response.peekContent().data?.driverStatus
            }

            // New York City
            getLastLocationAndDrawRoute()

        }

        trackModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun convertKilometersToMeters(kilometers: Double): Double {
        return kilometers * 1000
    }

    private fun convertMetersToKilometers(meters: Double): Double {
        return meters / 1000
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showCancelPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.cancel_booking)

        val bookingCancelBtn = dialog.findViewById<TextView>(R.id.bookingCancelBtn)
        val cancelReasonTxt = dialog.findViewById<EditText>(R.id.cancelReasonTxt)
        val cancelBtn = dialog.findViewById<ImageView>(R.id.cancelBtn)


        cancelBtn.setOnClickListener { dialog.dismiss() }

        bookingCancelBtn.setOnClickListener {
            val cancelReasonTxt = cancelReasonTxt.text.toString()
            if (cancelReasonTxt.isEmpty()) {
                Toast.makeText(applicationContext, "Please enter valid reason", Toast.LENGTH_SHORT)
                    .show()
            } else {
                acceptOrRejectApi(cancelReasonTxt)
            }

        }
        val window = dialog.window
        val lp = window?.attributes
        if (lp != null) {
            lp.width = ActionBar.LayoutParams.MATCH_PARENT
        }
        if (lp != null) {
            lp.height = ActionBar.LayoutParams.WRAP_CONTENT
        }
        if (window != null) {
            window.attributes = lp
        }
        acceptOrRejectObserver()

        dialog.show()
    }

    private fun acceptOrRejectApi(cancelReasonTxt: String) {
        //   val codePhone = strPhoneNo
        val loinBody = CancelBookingBody(
            cancelreason = cancelReasonTxt
        )
        cancelBookingModelView.cancelBooking(bookingId, loinBody, this, progressDialog)
    }

    private fun acceptOrRejectObserver() {
        cancelBookingModelView.progressIndicator.observe(this) {}
        cancelBookingModelView.mRejectResponse.observe(
            this
        ) {
            val msg = it.peekContent().msg
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()

            val intent = Intent(this, UserDashboardActivity::class.java)
            startActivity(intent)
        }
        cancelBookingModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@TrackActivity, it)
            // errorDialogs()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Remove callbacks to prevent memory leaks
        handler?.removeCallbacks(runnable)
    }

    private fun updateRoute(currentLocation: LatLng, destination: LatLng) {
        val context = GeoApiContext.Builder()
            .apiKey("YOUR_GOOGLE_MAPS_API_KEY")
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            val directionsResult: DirectionsResult = DirectionsApi.newRequest(context)
                .origin(
                    com.google.maps.model.LatLng(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                )
                .destination(
                    com.google.maps.model.LatLng(
                        destination.latitude,
                        destination.longitude
                    )
                )
                .await()

            val path = directionsResult.routes[0].overviewPolyline.decodePath().map {
                LatLng(it.lat, it.lng)
            }

            runOnUiThread {
                mMap.clear()
                mMap.addPolyline(PolylineOptions().addAll(path).color(R.color.purple_700))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10f))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true

                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        dropLocation?.let { it1 ->
                            updateRoute(
                                LatLng(it.latitude, it.longitude),
                                it1
                            )
                        }
                    }
                }
            }
        }
    }
    private fun showFeedbackPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.feedback_popup)


        val window = dialog.window
        val lp = window?.attributes
        if (lp != null) {
            lp.width = ActionBar.LayoutParams.MATCH_PARENT
        }
        if (lp != null) {
            lp.height = ActionBar.LayoutParams.WRAP_CONTENT
        }
        if (window != null) {
            window.attributes = lp
        }
        acceptOrRejectObserver()

        dialog.show()
    }
}
