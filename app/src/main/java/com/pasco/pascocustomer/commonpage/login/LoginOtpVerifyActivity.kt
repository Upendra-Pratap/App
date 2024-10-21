package com.pasco.pascocustomer.commonpage.login

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.DriverDashboard.Ui.DriverDashboardActivity
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.loginmodel.LoginBody
import com.pasco.pascocustomer.commonpage.login.loginmodel.LoginModelView
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.VehicleDetailsActivity
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityLoginOtpVerifyBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginOtpVerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginOtpVerifyBinding
    private lateinit var mAuth: FirebaseAuth
    private var strPhoneNo = ""
    private var loginValue = ""
    var verificationId = ""
    private var userType = ""
    private var countryCode = ""
    private var token = ""
    private val editTextList = mutableListOf<EditText>()
    private val loginModel: LoginModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var countDownTimer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()
        val deviceModel = Build.MODEL
        verificationId = intent.getStringExtra("verificationId").toString()
        strPhoneNo = intent.getStringExtra("phoneNumber").toString()
        loginValue = intent.getStringExtra("loginValue").toString()
        countryCode = intent.getStringExtra("countryCode").toString()
        binding.phoneNumber.text = "$countryCode $strPhoneNo"

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
        binding.continueBtn.setOnClickListener {
            val verificationCode =
                "${binding.box5.text}${binding.box1.text}${binding.box2.text}${binding.box3.text}${binding.box4.text}${binding.box6.text}"
            val credential: PhoneAuthCredential =
                PhoneAuthProvider.getCredential(verificationId, verificationCode)
            signInWithPhoneAuthCredential(credential, deviceModel)

        }
        editTextList.addAll(
            listOf(
                binding.box5, binding.box1, binding.box2, binding.box3, binding.box4, binding.box6
            )
        )

        for (i in 0 until editTextList.size - 1) {
            editTextList[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
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

        loginObserver()
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
        credential: PhoneAuthCredential, deviceModel: String
    ) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in successful, go to the next activity or perform desired actions
                if (loginValue == "driver") {
                    loginApi()
                } else {
                    loginApi()
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

    private fun clearOtpFields() {
        for (editText in editTextList) {
            editText.text.clear()
        }
        editTextList[0].requestFocus() // Set focus back to the first EditText
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
        loginModel.progressIndicator.observe(this) {}
        loginModel.mRejectResponse.observe(
            this
        ) {

            val token = it.peekContent().token
            val message = it.peekContent().msg
            val userId = it.peekContent().userId
            userType = it.peekContent().userType.toString()
            val approved = it.peekContent().approved
            PascoApp.encryptedPrefs.token = token?.refresh ?: ""
            PascoApp.encryptedPrefs.bearerToken = "Bearer ${token?.access ?: ""}"
            PascoApp.encryptedPrefs.userId = userId.toString()
            PascoApp.encryptedPrefs.userType = userType
            PascoApp.encryptedPrefs.driverApprovedId = approved?.toString()!!
            PascoApp.encryptedPrefs.profileUpdate = it.peekContent().profile.toString()
            PascoApp.encryptedPrefs.isFirstTime = false

            if (approved == 2 && userType == "driver") {
                Log.e("AAAAA", "aaaaaaa....")
                val intent = Intent(this@LoginOtpVerifyActivity, VehicleDetailsActivity::class.java)
                startActivity(intent)
            } else if (loginValue == "driver" && userType == "driver") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                val intent =
                    Intent(this@LoginOtpVerifyActivity, DriverDashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else if (loginValue == "user" && userType == "user") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginOtpVerifyActivity, UserDashboardActivity::class.java)
                startActivity(intent)
                finish()
            }


        }
        loginModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@LoginOtpVerifyActivity, it)
            // errorDialogs()
        }
    }

    private fun openPopUp() {
        val builder =
            AlertDialog.Builder(this@LoginOtpVerifyActivity, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.register_confirmation, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val okButtonAR = dialogView.findViewById<TextView>(R.id.okButtonAR)
        dialog.show()
        okButtonAR.setOnClickListener {
            dialog.dismiss()
        }
    }
}