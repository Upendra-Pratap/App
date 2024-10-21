package com.pasco.pascocustomer.commonpage.login.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.signup.clientmodel.ClientModelView
import com.pasco.pascocustomer.commonpage.login.signup.clientmodel.ClientSignupBody
import com.pasco.pascocustomer.commonpage.login.signup.model.DriverBody
import com.pasco.pascocustomer.commonpage.login.signup.model.DriverSignUpModel
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.VehicleDetailsActivity
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityOtpVerifyBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpVerifyActivity : AppCompatActivity() {
    private val editTextList = mutableListOf<EditText>()
    private lateinit var binding: ActivityOtpVerifyBinding
    private lateinit var mAuth: FirebaseAuth
    private var strPhoneNo = ""
    private var city = ""
    private var email = ""
    private var address = ""
    private var userName = ""
    private var loginValue = ""
    private var strPhoneCCode = ""
    private var token = ""
    var verificationId = ""
    private var formattedLatitudeSelect: String = ""
    private var formattedLongitudeSelect: String = ""

    private val driverViewModel: DriverSignUpModel by viewModels()
    private val userViewModel: ClientModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var countDownTimer: CountDownTimer? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        val deviceModel = Build.MODEL
        mAuth = FirebaseAuth.getInstance()

        verificationId = intent.getStringExtra("verificationId").toString()
        strPhoneNo = intent.getStringExtra("phoneNumber").toString()
        strPhoneCCode = intent.getStringExtra("phoneCountryCode").toString()

        city = intent.getStringExtra("city").toString()
        email = intent.getStringExtra("email").toString()
        address = intent.getStringExtra("address").toString()
        userName = intent.getStringExtra("userName").toString()
        loginValue = intent.getStringExtra("loginValue").toString()
        formattedLatitudeSelect = intent.getStringExtra("formattedLatitudeSelect").toString()
        formattedLongitudeSelect = intent.getStringExtra("formattedLongitudeSelect").toString()

        binding.phoneNumber.text = "$strPhoneCCode $strPhoneNo"
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

        Log.e("LogValueAA", "loginValue " + loginValue)

        binding.continueBtn.setOnClickListener {
            val verificationCode =
                "${binding.box5.text}${binding.box1.text}${binding.box2.text}${binding.box3.text}${binding.box4.text}${binding.box6.text}"
            val credential: PhoneAuthCredential =
                PhoneAuthProvider.getCredential(verificationId, verificationCode)
            signInWithPhoneAuthCredential(credential, deviceModel)
            /* val intent = Intent(this, DashboardActivity::class.java)
             startActivity(intent)*/
        }
        editTextList.addAll(
            listOf(
                binding.box5,
                binding.box1,
                binding.box2,
                binding.box3,
                binding.box4,
                binding.box6
            )
        )

        for (i in 0 until editTextList.size - 1) {
            editTextList[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        editTextList[i + 1].requestFocus()
                    }
                }
            })
        }

        binding.box3.setOnEditorActionListener { _, actionId, _ ->
            actionId == EditorInfo.IME_ACTION_DONE
        }


        for (i in editTextList.indices) {
            editTextList[i].setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (editTextList[i].text.isEmpty() && i > 0) {
                        editTextList[i - 1].requestFocus()
                        editTextList[i - 1].setText("")
                    }
                }
                false
            }
        }

        binding.resendBtn.setOnClickListener {
            resendOtp()
        }
        // Observer
        signUpObserver()
        signUpUserObserver()

    }

    private fun startResendOtpTimer() {
        binding.resendBtn.isEnabled = false
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timeDuration.text = "${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                binding.resendBtn.isEnabled = true
                binding.timeDuration.text = "You can resend OTP now"
            }
        }.start()
    }

    private fun resendOtp() {
        // Logic to resend OTP
        startResendOtpTimer() // Restart the timer
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel() // Cancel the timer to prevent memory leaks
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        deviceModel: String
    ) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in successful, go to the next activity or perform desired actions
                    if (loginValue == "driver") {
                        getDriverSignupApi(deviceModel)
                    } else {
                        getUserSignUp(deviceModel)
                        Log.e("LogValueAA", "loginValueUSER " + loginValue)
                    }
                } else {
                    // Sign in failed
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        clearOtpFields()
                    }
                }
            }
    }


    private fun getDriverSignupApi(deviceModel: String) {
        val loinBody = DriverBody(
            full_name = userName,
            email = email,
            phone_number = strPhoneNo,
            current_city = city,
            current_location = address,
            current_latitude = formattedLatitudeSelect,
            current_longitude = formattedLongitudeSelect,
            user_type = loginValue,
            phone_verify = deviceModel,
            phone_token = token
        )
        driverViewModel.driverSignUp(loinBody, this, progressDialog)
    }

    private fun signUpObserver() {
        driverViewModel.progressIndicator.observe(this) {
        }
        driverViewModel.mRejectResponse.observe(
            this
        ) {
            val token = it.peekContent().token
            PascoApp.encryptedPrefs.token = token?.refresh ?: ""
            PascoApp.encryptedPrefs.bearerToken = "Bearer ${token?.access ?: ""}"
            PascoApp.encryptedPrefs.isFirstTime = false
            val intent = Intent(this, VehicleDetailsActivity::class.java)
            startActivity(intent)
            finish()

        }
        driverViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@OtpVerifyActivity, it)
            // errorDialogs()
        }
    }


    private fun getUserSignUp(deviceModel: String) {
        val loinBody = ClientSignupBody(
            phone_number = strPhoneNo,
            user_type = loginValue,
            phone_verify = deviceModel,
            phone_token = token
        )
        userViewModel.clientSignUp(loinBody, this, progressDialog)
    }

    private fun signUpUserObserver() {
        userViewModel.progressIndicator.observe(this) {
        }
        userViewModel.mRejectResponse.observe(
            this
        ) {
            val message = it.peekContent().msg
            Log.e("LogValueAA", "loginValueUSER " + loginValue)

            val token = it.peekContent().token
            PascoApp.encryptedPrefs.token = token?.refresh ?: ""
            PascoApp.encryptedPrefs.profileUpdate = it.peekContent().profile.toString()
            PascoApp.encryptedPrefs.bearerToken = "Bearer ${token?.access ?: ""}"
            val intent = Intent(this, UserDashboardActivity::class.java)
            intent.putExtra("profileUpdate", "SigUp")
            startActivity(intent)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            finish()
        }
        userViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@OtpVerifyActivity, it)
            // errorDialogs()
        }
    }

    private fun clearOtpFields() {
        for (editText in editTextList) {
            editText.text.clear()
        }
        editTextList[0].requestFocus() // Set focus back to the first EditText
    }
}