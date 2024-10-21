package com.pasco.pascocustomer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.pasco.pascocustomer.Driver.DriverDashboard.Ui.DriverDashboardActivity
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.LoginActivity
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var splashAnimation: Animation
    private lateinit var activity: Activity
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash)
        binding.ivLogo.animation = splashAnimation
        activity = this

        try {
            val info = packageManager.getPackageInfo(
                "com.app.food",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }




        if (checkLocationPermission()) {
            requestLocationUpdates()
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        splashAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                if (PascoApp.encryptedPrefs.isFirstTime) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                    //  splashAnimation.repeatMode = 1
                    Log.e("FirstTimeLog", "1")
                } else {
                    val userId = PascoApp.encryptedPrefs.userId
                    val userType = PascoApp.encryptedPrefs.userType

                    Log.e("FirstTimeLog", "2")

                    if (PascoApp.encryptedPrefs.isNotification && userId != "") {
                        if (userType == "driver") {
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent(this@MainActivity, DriverDashboardActivity::class.java)
                                startActivity(intent)
                                finish()
                            }, 1000)
                        } else {
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent =
                                    Intent(this@MainActivity, UserDashboardActivity::class.java)
                                startActivity(intent)
                                finish()
                            }, 1000)
                        }

                    }

                }
            }
            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationUpdates() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lastLocation: Location? = locationResult.lastLocation
                Log.d(
                    "LocationUpdate",
                    "Latitude: ${lastLocation?.latitude}, Longitude: ${lastLocation?.longitude}"
                )

                // Save the current location in SharedPreferences
                saveLocation(lastLocation!!.latitude, lastLocation!!.longitude)
                // You can use lastLocation for further processing
                // For example, update UI, send to server, etc.

                // Stop location updates after getting the first location
                stopLocationUpdates()
            }
        }


        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // Update interval in milliseconds
            fastestInterval = 5000 // Fastest update interval in milliseconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates()
            } else {
                // Handle the case where the user denied the location permission
                Log.e("LocationUpdate", "Location permission denied")
            }
        }
    }

    private fun saveLocation(latitude: Double, longitude: Double) {
        with(sharedPreferences.edit()) {
            putFloat("LATITUDE", latitude.toFloat())
            putFloat("LONGITUDE", longitude.toFloat())
            apply()
        }
        Log.d(
            "LocationSave",
            "Latitude: $latitude, Longitude: $longitude saved in SharedPreferences"
        )
    }
}