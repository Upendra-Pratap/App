package com.pasco.pascocustomer.Driver.customerDetails

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.driverdetails.modelview.DriverDetailsModelView
import com.pasco.pascocustomer.databinding.ActivityCustomerDetailsBinding
import com.pasco.pascocustomer.databinding.ActivityDriverDetailsBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerDetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCustomerDetailsBinding
    private var customerId = ""
    private val customerDetailsViewModel: CustomerDetailsViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageBackReqRideCusD.setOnClickListener {
            finish()
        }
        customerId = intent.getStringExtra("customerId")!!
        getDriverDetails()
        driverDetailsObserver()
    }
    private fun getDriverDetails() {
        customerDetailsViewModel.getDriverDetails(customerId, this, progressDialog)
    }

    @SuppressLint("SetTextI18n")
    private fun driverDetailsObserver() {
        customerDetailsViewModel.progressIndicator.observe(this@CustomerDetailsActivity) {
        }
        customerDetailsViewModel.mCustomerdResponse.observe(this@CustomerDetailsActivity) {
            val message = it.peekContent().msg
            val success = it.peekContent().status

            binding.userNameCusD.text  = it.peekContent().data?.fullName
            binding.emailTxtCusD.text = it.peekContent().data?.email
            binding.contactTxtCusD.text = it.peekContent().data?.phoneNumber
            binding.currentCityTxtCusD.text = it.peekContent().data?.currentCity

            val url = it.peekContent().data?.image
            Glide.with(this).load(BuildConfig.IMAGE_KEY+url).into(binding.profileImgCusD)

        }
        customerDetailsViewModel.errorResponse.observe(this@CustomerDetailsActivity) {
            ErrorUtil.handlerGeneralError(this@CustomerDetailsActivity, it)
            //errorDialogs()
        }
    }
}