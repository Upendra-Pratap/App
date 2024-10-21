package com.pasco.pascocustomer.commonpage.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.DriverDashboard.Ui.DriverDashboardActivity
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.loginmodel.LoginBody
import com.pasco.pascocustomer.commonpage.login.loginmodel.LoginModelView
import com.pasco.pascocustomer.commonpage.login.loginotpcheck.CheckOtpBody
import com.pasco.pascocustomer.commonpage.login.loginotpcheck.OtpCheckModelView
import com.pasco.pascocustomer.commonpage.login.signup.SignUpActivity
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.VehicleDetailsActivity
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityLoginBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var formattedCountryCode = ""
    private var cCodeSignIn = ""
    private var doubleBackToExitPressedOnce = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var loginValue = ""

    private lateinit var auth: FirebaseAuth
    private var strPhoneNo = ""
    private var userType = ""
    private var token = ""
    private var verificationId: String = ""
    private val otpModel: OtpCheckModelView by viewModels()
    private val loginModel: LoginModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val deviceModel = Build.MODEL

        loginValue = "user"
        auth = FirebaseAuth.getInstance()


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            token = task.result

            // Log the token
            Log.e("MainActivityAA", "FCM Registration Token: $token")

            // Send token to your server if needed
            // sendTokenToServer(token)
        }

        binding.asDriverConst.setOnClickListener {
            binding.asDriverConst.setBackgroundResource(R.drawable.as_client_white_background)
            binding.driverTxt.setTextColor(ContextCompat.getColor(this, R.color.grey_dark))
            binding.clientTxt.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.asClientConst.setBackgroundResource(0)
            loginValue = "driver"
        }

        binding.asClientConst.setOnClickListener {
            binding.asClientConst.setBackgroundResource(R.drawable.as_client_white_background)
            binding.driverTxt.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.clientTxt.setTextColor(ContextCompat.getColor(this, R.color.grey_dark))
            binding.asDriverConst.setBackgroundResource(0)
            loginValue = "user"
        }
        binding.signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("loginValue", loginValue)
            startActivity(intent)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationUpdates()

        if (checkLocationPermission()) {
            requestLocationUpdates()
        } else {
            requestLocationPermission()
        }
        binding.continueBtn.setOnClickListener {
            strPhoneNo = binding.phoneNumber.text.toString()
            cCodeSignIn = binding.signInCountryCode.text.toString()

            if (binding.phoneNumber.text.isEmpty()) {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
            } else if (binding.signInCountryCode.text.isNullOrBlank()) {
                Toast.makeText(
                    applicationContext,
                    "Please enter country code",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!binding.signInCountryCode.text.startsWith("+")) {
                Toast.makeText(
                    applicationContext,
                    "Phone number should include country code prefixed with +",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.phoneNumber.text.isNullOrBlank()) {
                Toast.makeText(
                    applicationContext,
                    "Please enter phone number",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.phoneNumber.text.length < 8) {
                Toast.makeText(
                    applicationContext,
                    "Phone number must be at least 8 digits",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (loginValue == "driver") {
                    otpCheckApi(deviceModel)
                } else {
                    otpCheckApi(deviceModel)
                }
            }

        }
        // Observer
        checkLoginObserver(loginValue)
        loginObserver()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@LoginActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@LoginActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@LoginActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@LoginActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationUpdates() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    showAddress(it)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun showAddress(location: Location) {
        GlobalScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(this@LoginActivity, Locale.getDefault())
            try {
                val addresses: List<Address> = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )!!
                if (addresses.isNotEmpty()) {
                    val addressObj = addresses[0]
                    val countryCode = addressObj.countryCode
                    val countryName = addressObj.countryName

                    // Get the phone country code using libphonenumber
                    val phoneUtil = PhoneNumberUtil.getInstance()
                    val phoneCountryCode = phoneUtil.getCountryCodeForRegion(countryCode)

                    // Log the country code and country name
                    Log.e("Country Code", countryCode ?: "No country code found")
                    Log.e("Country Name", countryName ?: "No country name found")
                    Log.e("Phone Country Code", "+$phoneCountryCode")

                    // Switch to the main thread before updating UI
                    withContext(Dispatchers.Main) {
                        formattedCountryCode = "+$phoneCountryCode"
                        if (formattedCountryCode.isNotEmpty()) {
                            binding.signInCountryCode.setText(formattedCountryCode)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun otpCheckApi(deviceModel: String) {
        Log.e("OtpCheckData", "loginValue " + loginValue)
        val loinBody = CheckOtpBody(
            phone_number = strPhoneNo,
            user_type = loginValue,
            phone_verify = deviceModel

        )
        otpModel.otpCheck(loinBody, this, progressDialog)
    }

    private fun checkLoginObserver(loginValue: String) {
        otpModel.progressIndicator.observe(this) {
            // Implement progress indicator handling if needed
        }
        otpModel.mRejectResponse.observe(this) {
            val otpStatus = it.peekContent().login
            var success = it.peekContent().status
            var message = it.peekContent().msg

            if (success == "True") {
                if (loginValue == "driver") {
                    if (otpStatus == 0) {
                        sendVerificationCode("$cCodeSignIn$strPhoneNo")
                    } else {
                        loginApi()
                    }
                } else {
                    if (otpStatus == 0) {
                        sendVerificationCode("$cCodeSignIn$strPhoneNo")
                    } else {
                        loginApi()
                    }
                }
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }

        }
        otpModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@LoginActivity, it)
            // errorDialogs()
        }
    }

    private fun loginApi() {
        //   val codePhone = strPhoneNo
        val loinBody = LoginBody(
            phone_number = strPhoneNo,
            user_type = loginValue,
            phone_token = token
        )
        loginModel.otpCheck(loinBody, this, progressDialog)
    }

    private fun loginObserver() {
        loginModel.progressIndicator.observe(this) {
        }
        loginModel.mRejectResponse.observe(
            this
        ) {

            val token = it.peekContent().token
            val message = it.peekContent().msg
            val userId = it.peekContent().userId
            userType = it.peekContent().userType.toString()
            val approved = it.peekContent().approved
            PascoApp.encryptedPrefs.driverApprovedId = approved?.toString()!!
            PascoApp.encryptedPrefs.token = token?.refresh ?: ""
            PascoApp.encryptedPrefs.bearerToken = "Bearer ${token?.access ?: ""}"
            PascoApp.encryptedPrefs.userId = userId.toString()
            PascoApp.encryptedPrefs.userType = userType
            PascoApp.encryptedPrefs.profileUpdate = it.peekContent().profile.toString()
            PascoApp.encryptedPrefs.isFirstTime = false

            if (approved == 2 && userType == "driver") {
                Log.e("AAAAA", "aaaaaaa....")
                val intent = Intent(this@LoginActivity, VehicleDetailsActivity::class.java)
                startActivity(intent)
            } else if (loginValue == "driver" && userType == "driver") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, DriverDashboardActivity::class.java)
                startActivity(intent)

            } else if (loginValue == "user" && userType == "user") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, UserDashboardActivity::class.java)
                startActivity(intent)
            }

        }
        loginModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@LoginActivity, it)
            // errorDialogs()
        }
    }


    private fun sendVerificationCode(phoneNumber: String) {

        // showLoader()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Automatically sign in the user when verification is done
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Handle error
                    Log.e("UserMessage", "Verification failed: ${e.message}")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // Save the verification ID
                    this@LoginActivity.verificationId = verificationId

                    val intent = Intent(this@LoginActivity, LoginOtpVerifyActivity::class.java)
                    intent.putExtra("verificationId", verificationId)
                    intent.putExtra("phoneNumber", strPhoneNo)
                    intent.putExtra("loginValue", loginValue)
                    intent.putExtra("countryCode", binding.signInCountryCode.text.toString())
                    startActivity(intent)

                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in successful, go to the next activity or perform desired actions
                    Log.e("UserMessage", "onCreate: Successfully")

                } else {
                    // Sign in failed
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (doubleBackToExitPressedOnce) {
            // Exit the app by clearing all activities
            val intent = Intent(this@LoginActivity, LoginActivity::class.java)
            startActivity(intent)
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(
            { doubleBackToExitPressedOnce = false },
            2000
        ) // Delay for 2 seconds to reset the flag
    }


}