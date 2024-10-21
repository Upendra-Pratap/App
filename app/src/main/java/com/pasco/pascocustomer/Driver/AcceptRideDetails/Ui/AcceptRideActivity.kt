package com.pasco.pascocustomer.Driver.AcceptRideDetails.Ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
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
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel.AcceptRideViewModel
import com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel.AddBiddingBody
import com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel.AddBidingViewModel
import com.pasco.pascocustomer.Driver.DriverDashboard.Ui.DriverDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityAcceptRideBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@AndroidEntryPoint
class AcceptRideActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityAcceptRideBinding
    private lateinit var activity: Activity
    private val acceptRideViewModel: AcceptRideViewModel by viewModels()
    private val addBidingViewModel: AddBidingViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var reqId = ""
    private var bookingNumber = ""
    private var dateTimes = ""
    private var pickUpLocBid = ""
    private var dropUpLocBid = ""
    private var totalDistanceLoc = ""
    private var totalTimeLoc = ""
    private var totalPriceLoc = ""
    private var commissionP = ""
    private var bookingID = ""
    private lateinit var pickupLocation: LatLng
    private lateinit var dropLocation: LatLng
    private lateinit var mMap: GoogleMap
    private var currentLatitudePickup: Double = 0.0
    private var currentLongitudePickup: Double = 0.0
    private var currentLatitudeDrop: Double = 0.0
    private var currentLongitudeDrop: Double = 0.0
    private lateinit var addBiddingBody: AddBiddingBody
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isDestinationReached = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcceptRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        reqId = intent.getStringExtra("rideReqId").orEmpty()
        bookingNumber = intent.getStringExtra("bookingNumb").orEmpty()
        currentLatitudePickup = intent.getStringExtra("pickuplatitudea")?.toDoubleOrNull() ?: 0.0
        currentLongitudePickup = intent.getStringExtra("pickuplongitudea")?.toDoubleOrNull() ?: 0.0
        currentLatitudeDrop = intent.getStringExtra("droplatitudea")?.toDoubleOrNull() ?: 0.0
        currentLongitudeDrop = intent.getStringExtra("droplongitudea")?.toDoubleOrNull() ?: 0.0

        Log.d("PickupLocation", "Latitude: $currentLatitudePickup, Longitude: $currentLongitudePickup")
        Log.d("DropLocation", "Latitude: $currentLatitudeDrop, Longitude: $currentLongitudeDrop")


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapAcceptRide) as SupportMapFragment
        mapFragment.getMapAsync(this)

        pickupLocation = LatLng(currentLatitudePickup, currentLongitudePickup)
        dropLocation = LatLng(currentLatitudeDrop, currentLongitudeDrop)


        if (!reqId.isNullOrBlank()) {
            getBidDetailsApi()
        }
        getBidObserver()
        binding.imageBackReqRide.setOnClickListener {
            finish()
        }
        /*  pickupLocation = LatLng(28.6076, 77.3683) // New York City
        dropLocation = LatLng(28.5851, 77.3116) // Los Angeles Los Angeles*/
        //callObserver
        addBidingObserver()
        binding.showPriceEditTextdasdas.setOnClickListener {
            addAvailabilityPopUp()
        }
        binding.imgSsadasd.setOnClickListener {
            addAvailabilityPopUp()
        }
        binding.acceptOrderCButton.setOnClickListener {
            val dateTime = dateTimes
            val bidPrice = binding.showPriceEditText.text.toString()

            if (dateTime.isEmpty()) {
                Toast.makeText(
                    this@AcceptRideActivity,
                    "Please add the availability",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (bidPrice.isEmpty()) {
                Toast.makeText(
                    this@AcceptRideActivity,
                    "Please enter bid price",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!bidPrice.matches("[0-9]+".toRegex())) {
                Toast.makeText(
                    this@AcceptRideActivity,
                    "Please enter a valid bid price",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Call API

                addBiding()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isTrafficEnabled = true
        // Add markers for pickup and drop locations
        mMap.addMarker(MarkerOptions().position(pickupLocation).title("Pickup Location"))
        mMap.addMarker(MarkerOptions().position(dropLocation).title("Drop Location"))

        // Move camera to the initial pickup location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 14f))

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
        mMap.isMyLocationEnabled = true

        // Get and draw route when map is ready
        getLastLocationAndDrawRoute()

        mMap.setOnMyLocationChangeListener { location ->
            val userLocation = LatLng(location.latitude, location.longitude)
            checkDestinationReached(userLocation, dropLocation)
        }
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

        Log.e("location", "location.." + latLng.latitude + "longitude " + latLng.longitude)
        val result: DirectionsResult = DirectionsApi.newRequest(context)
            .mode(TravelMode.DRIVING)
            .origin("${latLng.latitude},${latLng.longitude}")
            .destination("${dropLocation.latitude},${dropLocation.longitude}")
            .await()

        // Decode polyline and draw on map
        if (result.routes.isNotEmpty()) {
            val points = decodePolyline(result.routes[0].overviewPolyline.encodedPath)
            mMap.addPolyline(PolylineOptions().addAll(points).color(android.graphics.Color.BLUE))
        } else {
            Log.e("drawRoute", "No routes found")
            Toast.makeText(this, "No routes found", Toast.LENGTH_SHORT).show()
        }
        // mMap.clear() // Clear previous route

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

    private fun getBidObserver() {
        acceptRideViewModel.mAcceptRideResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "True") {
                val allData = response.peekContent().data
                binding.pickUpLocBidd.text = truncateAddress(allData?.pickupLocation.toString())
                binding.dropLocBidd.text = truncateAddress(allData?.dropLocation.toString())
                binding.pickUpLocBidd.setOnClickListener {
                    showFullAddressDialog(allData?.pickupLocation.toString())
                }
                binding.dropLocBidd.setOnClickListener {
                    showFullAddressDialog(allData?.dropLocation.toString())
                }
                // Format total distance
                val formattedTotalDistance = "%.1f".format(allData?.totalDistance ?: 0.0)
                binding.totalDistanceBidd.text = "$formattedTotalDistance km"
                binding.WholePrice.text = "$" + allData?.basicprice.toString()
                //  binding.orderIdStaticTextView.text = "$" + allData?.price.toString()
                val id = allData?.id
                Log.e("id", "getBidObserver: $id")
                binding.cashDynamic.text = allData?.paymentStatus.toString()
                bookingID = allData?.id.toString()
                totalTimeLoc = allData?.pickupDatetime.toString()
                totalPriceLoc = "$" + allData?.basicprice.toString()
                commissionP = "$" + allData?.commisionPrice.toString()
                binding.orderIdStaticTextView.text = commissionP
                val duration = allData?.duration.toString()
                val durationInSeconds = duration.toIntOrNull() ?: 0
                val formattedDuration = if (durationInSeconds < 60) {
                    "$durationInSeconds sec"
                } else {
                    val hours = durationInSeconds / 3600
                    val minutes = (durationInSeconds % 3600) / 60
                    val seconds = durationInSeconds % 60
                    if (hours > 0) {
                        String.format("%d hr %02d min %02d sec", hours, minutes, seconds)
                    } else {
                        String.format("%d min %02d sec", minutes, seconds)
                    }
                }
                binding.routeTime.text = formattedDuration

            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }

        acceptRideViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    fun showFullAddressDialog(address: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Address")
        alertDialogBuilder.setMessage(address)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun truncateAddress(address: String): String {
        val words = address.split(" ")
        return if (words.size > 3) {
            words.subList(0, 3).joinToString(" ") + "..."
        } else {
            address
        }
    }

    private fun addBidingObserver() {
        addBidingViewModel.mAddBiddibgResponse.observe(this) { response ->
            val message = response.peekContent().msg!!

            if (response.peekContent().status.equals("False")) {
                Toast.makeText(this, "failed: $message", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "$message", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AcceptRideActivity, DriverDashboardActivity::class.java)
                intent.putExtra("bookingID", bookingID)
                intent.putExtra("pickUpLocBidd", pickUpLocBid)
                intent.putExtra("dropUpLocBid", dropUpLocBid)
                intent.putExtra("totalDistanceLoc", totalDistanceLoc)
                intent.putExtra("pickUpLocBid", totalTimeLoc)
                intent.putExtra("totalPriceLoc", totalPriceLoc)
                startActivity(intent)

            }

        }
        addBidingViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun addBiding() {
        val pricee = binding.showPriceEditText.text.toString()
        addBiddingBody = AddBiddingBody(
            dateTimes, pricee
        )
        addBidingViewModel.addBidingData(
            progressDialog,
            activity,
            reqId,
            addBiddingBody
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDateTime(dateTimeString: String?): String {
        if (dateTimeString.isNullOrEmpty()) return ""

        // Parse the datetime string into a ZonedDateTime object
        val dateTime = ZonedDateTime.parse(dateTimeString)

        // Define the desired format
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        // Format the datetime and return as a string
        return dateTime.format(formatter)
    }

    private fun getBidDetailsApi() {
        acceptRideViewModel.getAcceptRideData(
            activity,
            reqId
        )
    }

    //comment123
    @SuppressLint("MissingInflatedId")
    private fun addAvailabilityPopUp() {
        val builder =
            AlertDialog.Builder(this@AcceptRideActivity, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.add_avilability_popup, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val backArrowAddAvPopup = dialogView.findViewById<ImageView>(R.id.backArrowAddAvPopup)
        val startDateTxtPop = dialogView.findViewById<TextView>(R.id.startDateTxtPop)
        val startTimetxtPop = dialogView.findViewById<TextView>(R.id.startTimetxtPop)
        val createSlotsBtnPop = dialogView.findViewById<Button>(R.id.createSlotsBtnPop)
        dialog.show()
        backArrowAddAvPopup.setOnClickListener {
            dialog.dismiss()
        }

        startDateTxtPop.setOnClickListener {
            val calendar = Calendar.getInstance()

            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this@AcceptRideActivity, R.style.MyTimePicker,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val formattedMonth = String.format("%02d", monthOfYear + 1)
                    val formatDay = String.format("%02d", dayOfMonth)
                    val date = "$year-$formattedMonth-$formatDay"

                    startDateTxtPop.text = date
                },
                year, month, day
            )

            datePickerDialog.show()
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }

        startTimetxtPop.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog

            mTimePicker = TimePickerDialog(
                this@AcceptRideActivity, R.style.MyTimePicker,
                TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    val formattedHour = String.format("%02d", selectedHour)
                    val formatMinutes = String.format("%02d", selectedMinute)
                    startTimetxtPop.text = "$formattedHour:$formatMinutes"
                },
                hour, minute, true
            )
            mTimePicker.show()
        }

        createSlotsBtnPop.setOnClickListener {
            if (!startDateTxtPop.text.isEmpty() && !startTimetxtPop.text.isEmpty()) {
                val dateCheck = startDateTxtPop.text.toString()
                val timeCheck = startTimetxtPop.text.toString()
                val dateTime = dateCheck + " " + timeCheck
                dateTimes = dateTime
                if (!dateTimes.isNullOrBlank()) {
                    binding.showPriceEditTextdasdas.text = dateTimes
                }
                // Toast.makeText(this@AcceptRideActivity, "Added Successfully", Toast.LENGTH_SHORT).show()
                Log.d("DateTime", dateTime)
                dialog.dismiss() // Dismiss the dialog explicitly
            } else {
                Toast.makeText(
                    this@AcceptRideActivity,
                    "Please select both start date and start time",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

}
