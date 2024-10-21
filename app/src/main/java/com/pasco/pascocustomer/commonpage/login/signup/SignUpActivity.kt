package com.pasco.pascocustomer.commonpage.login.signup

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.adapter.UpdateAddressAdapter
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.LoginActivity
import com.pasco.pascocustomer.commonpage.login.signup.UpdateCity.UpdateCityBody
import com.pasco.pascocustomer.commonpage.login.signup.UpdateCity.UpdateCityResponse
import com.pasco.pascocustomer.commonpage.login.signup.UpdateCity.UpdateCityViewModel
import com.pasco.pascocustomer.commonpage.login.signup.checknumber.CheckNumberBody
import com.pasco.pascocustomer.commonpage.login.signup.checknumber.CheckNumberModelView
import com.pasco.pascocustomer.customer.activity.SignUpCityName
import com.pasco.pascocustomer.databinding.ActivitySignUpBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity(), SignUpCityName {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private var strPhoneNo = ""
    private var strUserPhoneNo = ""
    private var verificationId: String = ""
    private var loginValue = ""
    private var strUserName = ""
    private var strEmail = ""
    private var formattedCountryCode = ""
    private var CountryCode = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var pickUplatitude = 0.0
    private var pickUplongitude = 0.0
    private var formattedLatitudeSelect: String = ""
    private var formattedLongitudeSelect: String = ""
    private var city: String? = null
    private var address: String? = null

    private val checkNumberModelView: CheckNumberModelView by viewModels()
    private val updateCityViewModel: UpdateCityViewModel by viewModels()
    private var updateCityList: List<UpdateCityResponse.updateCityList> = ArrayList()
    private var updateAddressAdapter: UpdateAddressAdapter? = null
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var dialogRecyclerView: RecyclerView? = null
    private var alertDialog: Dialog? = null
    private var selectCityName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        loginValue = intent.getStringExtra("loginValue").toString()
//hello bbb
        if (loginValue == "driver") {
            binding.asDriverSignup.visibility = View.VISIBLE
            binding.asCustomerSignup.visibility = View.GONE
        } else {
            binding.asCustomerSignup.visibility = View.VISIBLE
            binding.asDriverSignup.visibility = View.GONE
        }
//kkk

        Log.e("CountryCodeAA", "code..." + formattedCountryCode)
        binding.signInBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationUpdates()

        if (checkLocationPermission()) {
            requestLocationUpdates()
        } else {
            requestLocationPermission()
        }

        checkNumberObserver()

        binding.driverCode?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                // getCityList()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.addressTxt.setOnClickListener {
            formattedCountryCode = binding.driverCode.text.toString()
            showSearchableDialog()

        }


        //call city list api


        binding.signUpBtn.setOnClickListener {
            strUserName = binding.userName.text.toString()
            strEmail = binding.driverEmail.text.toString()
            address = binding.addressTxt.text.toString()

            strPhoneNo = binding.phoneNumber.text.toString()
            strUserPhoneNo = binding.userPhoneNumber.text.toString()
            CountryCode = binding.clientCountryCode.text.toString()
            CountryCode = binding.driverCode.text.toString()
            
            if (loginValue == "driver") {
                formattedCountryCode = binding.driverCode.text.toString()
                strPhoneNo = binding.phoneNumber.text.toString()
                validationDriver(strPhoneNo)
            } else {
                formattedCountryCode = binding.clientCountryCode.text.toString()
                strUserPhoneNo = binding.userPhoneNumber.text.toString()
                validationUser(strUserPhoneNo)
            }


        }


    }

    //open popup


    private fun showAddress(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude

        pickUplatitude = latitude
        pickUplongitude = longitude
        formattedLatitudeSelect = String.format("%.5f", pickUplatitude)
        formattedLongitudeSelect = String.format("%.5f", pickUplongitude)

        GlobalScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(this@SignUpActivity, Locale.getDefault())
            try {
                val addresses: List<Address> = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )!!
                if (addresses.isNotEmpty()) {
                    val addressObj = addresses[0]
                    address = addressObj.getAddressLine(0)
                    city = addressObj.locality
                    val countryCode = addressObj.countryCode
                    val countryName = addressObj.countryName

                    // Get the phone country code using libphonenumber
                    val phoneUtil = PhoneNumberUtil.getInstance()
                    val phoneCountryCode = phoneUtil.getCountryCodeForRegion(countryCode)

                    // Log the country code and country name
                    Log.e("Country Code", countryCode ?: "No country code found")
                    Log.e("Country Name", countryName ?: "No country name found")
                    Log.e("Phone Country Code", "+$phoneCountryCode")

                    formattedCountryCode = "+$phoneCountryCode"

                    PascoApp.encryptedPrefs.countryCode = formattedCountryCode
                    Log.e("Full Phone Number", formattedCountryCode)
                    if (formattedCountryCode.isNotEmpty()) {
                        // Update the EditTexts with the country code
                        withContext(Dispatchers.Main) {
                            binding.driverCode.setText(formattedCountryCode)
                            binding.clientCountryCode.setText(formattedCountryCode)
                        }
                    }


                    // Update the UI with the city name
                    city?.let { updateUI(it) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun showSearchableDialog() {

        alertDialog = Dialog(this@SignUpActivity)
        alertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog?.setCancelable(true)
        alertDialog?.setContentView(com.pasco.pascocustomer.R.layout.dialog_searchable_spinner)
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog?.getWindow()!!.setLayout(750, 1200);
        // Show dialog

        val searchCountryName =
            alertDialog?.findViewById<androidx.appcompat.widget.SearchView>(com.pasco.pascocustomer.R.id.searchCountryName)
        dialogRecyclerView =
            alertDialog?.findViewById(com.pasco.pascocustomer.R.id.searchableSpinnerRecycleView)!!



        searchCountryName?.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })



        getCityList()
        getCityListObserver(dialogRecyclerView!!)
        alertDialog?.show()
    }

    private fun getCityList() {
        Log.e("formattedCountryCode", "formattedCountryCode..AA" + formattedCountryCode)

        val cityBody = UpdateCityBody(
            countrycode = formattedCountryCode
        )
        updateCityViewModel.cityListData(cityBody, this, progressDialog)
    }

    private fun getCityListObserver(dialogRecyclerView: RecyclerView) {
        updateCityViewModel.progressIndicator.observe(this@SignUpActivity, Observer {
            // Handle progress indicator changes if needed
        })

        updateCityViewModel.mgetCityListResponse.observe(this) { response ->
            val message = response.peekContent().msg
            updateCityList = response.peekContent().data ?: emptyList()

            if (response.peekContent().status == "False") {
                //Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                dialogRecyclerView.isVerticalScrollBarEnabled = true
                dialogRecyclerView.isVerticalFadingEdgeEnabled = true
                dialogRecyclerView.layoutManager =
                    LinearLayoutManager(
                        this@SignUpActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                updateAddressAdapter =
                    UpdateAddressAdapter(this@SignUpActivity, updateCityList, this)
                dialogRecyclerView.adapter = updateAddressAdapter

                updateCityViewModel.errorResponse.observe(this) {
                    ErrorUtil.handlerGeneralError(this, it)
                }
            }
        }
    }


    private fun validationDriver(strPhoneNo: String) {
        with(binding) {
            when {
                userName.text.isNullOrBlank() -> {
                    Toast.makeText(
                        applicationContext,
                        "Please enter name",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                driverEmail.text.isNullOrBlank() -> {
                    Toast.makeText(
                        applicationContext,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                binding.driverCode.text.isNullOrBlank() -> {
                    Toast.makeText(
                        applicationContext,
                        "Please enter country code",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                !binding.driverCode.text.startsWith("+") -> {
                    Toast.makeText(
                        applicationContext,
                        "Country code should start with +",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                phoneNumber.text.isNullOrBlank() -> {
                    Toast.makeText(
                        applicationContext,
                        "Please enter phone number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                phoneNumber.text.length < 8 -> {
                    Toast.makeText(
                        applicationContext,
                        "Phone number must be at least 9 digits",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    checkNumberApi(strPhoneNo)
                }
            }
        }
    }

    private fun validationUser(strUserPhoneNo: String) {
        with(binding) {
            when {
                userPhoneNumber.text.isNullOrBlank() -> {
                    Toast.makeText(
                        applicationContext,
                        "Please enter phone number",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                userPhoneNumber.text.length < 8 -> {
                    Toast.makeText(
                        applicationContext,
                        "Phone number must be at least 8 digits",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                clientCountryCode.text.isNullOrBlank() -> {
                    Toast.makeText(
                        applicationContext,
                        "Please enter country code",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                !clientCountryCode.text.startsWith("+") -> {
                    Toast.makeText(
                        applicationContext,
                        "Phone number should include country code prefixed with +",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    checkNumberApi(strUserPhoneNo)
                }
            }
        }
    }


    private fun updateUI(city: String) {
        // Update the UI on the main thread
        runOnUiThread {
            binding.addressTxt.text = city
        }
    }

    private fun checkNumberApi(strPhoneNo: String) {
        val loinBody = CheckNumberBody(
            phone_number = strPhoneNo,
            user_type = loginValue
        )
        checkNumberModelView.otpCheck(loinBody, this, progressDialog)
    }

    private fun checkNumberObserver() {
        checkNumberModelView.progressIndicator.observe(this) {
        }
        checkNumberModelView.mRejectResponse.observe(
            this
        ) {

            val existNumber = it.peekContent().exists
            val message = it.peekContent().msg

            if (existNumber == 1) {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            } else {
                if (loginValue == "driver") {
                    sendVerificationCode("$formattedCountryCode$strPhoneNo")
                    progressDialog.start("Loading....")
                    Log.e("PhoneNumberaa", "$formattedCountryCode$strPhoneNo")
                } else {
                    strPhoneNo = binding.userPhoneNumber.text.toString()
                    sendVerificationCode("$formattedCountryCode$strUserPhoneNo")
                    Log.e("PhoneNumberaa", "$formattedCountryCode$strUserPhoneNo")
                }
            }


        }
        checkNumberModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@SignUpActivity, it)
            // errorDialogs()
        }
    }

    override fun itemCity(id: Int, cityName: String) {
        selectCityName = cityName
        binding.addressTxt.text = selectCityName
        alertDialog?.dismiss()

        getLatLngFromCityName(selectCityName, applicationContext)
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val lowercaseQuery = query.lowercase(Locale.ROOT)
            val uppercaseQuery = query.uppercase(Locale.ROOT)
            val filterList = ArrayList<UpdateCityResponse.updateCityList>()
            for (i in updateCityList) {
                if (i.cityname?.lowercase(Locale.ROOT)?.contains(lowercaseQuery) == true || i.cityname?.uppercase(Locale.ROOT)?.contains(uppercaseQuery) == true) {
                    filterList.add(i)
                }
            }
            if (filterList.isEmpty()) {
                Toast.makeText(this, "No Data found", Toast.LENGTH_LONG).show()
            } else {
                updateAddressAdapter?.setFilteredList(filterList)
            }
        }
    }


    private fun getLatLngFromCityName(cityName: String, context: Context) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address> =
                geocoder.getFromLocationName(cityName, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                formattedLatitudeSelect = addresses[0].latitude.toString()
                formattedLongitudeSelect = addresses[0].longitude.toString()
                Log.d(
                    "LocationAAAA",
                    "Latitude: $formattedLatitudeSelect, Longitude: $formattedLongitudeSelect"
                )

            } else {
                Toast.makeText(context, "No location found for the city", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Geocoding failed: IOException", Toast.LENGTH_SHORT).show()
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
                    this@SignUpActivity.verificationId = verificationId
                    progressDialog.stop()

                    if (loginValue == "driver") {
                        val intent = Intent(this@SignUpActivity, OtpVerifyActivity::class.java)
                        intent.putExtra("verificationId", verificationId)
                        intent.putExtra("phoneNumber", strPhoneNo)
                        intent.putExtra("phoneCountryCode", CountryCode)
                        intent.putExtra("city", city)
                        intent.putExtra("email", strEmail)
                        intent.putExtra("address", address)
                        intent.putExtra("userName", strUserName)
                        intent.putExtra("loginValue", loginValue)
                        intent.putExtra("formattedLatitudeSelect", formattedLatitudeSelect)
                        intent.putExtra("formattedLongitudeSelect", formattedLongitudeSelect)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@SignUpActivity, OtpVerifyActivity::class.java)
                        intent.putExtra("verificationId", verificationId)
                        intent.putExtra("phoneNumber", strUserPhoneNo)
                        intent.putExtra("phoneCountryCode", CountryCode)
                        intent.putExtra("loginValue", loginValue)
                        startActivity(intent)
                        finish()
                    }
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

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@SignUpActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@SignUpActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@SignUpActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@SignUpActivity,
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
}