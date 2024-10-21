package com.pasco.pascocustomer.customer.activity.driverdetails

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.adapter.AllBiddsDetailsAdapter
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model.BiddsDtailsModelView
import com.pasco.pascocustomer.customer.activity.driverdetails.modelview.DriverDetailsModelView
import com.pasco.pascocustomer.databinding.ActivityDriverDetailsBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverDetailsBinding
    private val detailsModel: DriverDetailsModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDriverDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageBackReqRide.setOnClickListener { finish() }
        id = intent.getStringExtra("id").toString()
        getDriverDetails()
        driverDetailsObserver()
    }

    private fun getDriverDetails() {
        detailsModel.getDriverDetails(id, this, progressDialog)
    }

    @SuppressLint("SetTextI18n")
    private fun driverDetailsObserver() {
        detailsModel.progressIndicator.observe(this@DriverDetailsActivity) {
        }
        detailsModel.mRejectResponse.observe(this@DriverDetailsActivity) {
            val message = it.peekContent().msg
            val success = it.peekContent().status

            binding.userName.text  = it.peekContent().data?.driver
            binding.emailTxtA.text = it.peekContent().data?.email
            binding.contactTxtA.text = it.peekContent().data?.phoneNumber
            binding.currentCityTxt.text = it.peekContent().data?.currentCity

            val url = it.peekContent().data?.image
            Glide.with(this).load(BuildConfig.IMAGE_KEY+url).into(binding.profileImg)

        }
        detailsModel.errorResponse.observe(this@DriverDetailsActivity) {
            ErrorUtil.handlerGeneralError(this@DriverDetailsActivity, it)
            //errorDialogs()
        }
    }
}