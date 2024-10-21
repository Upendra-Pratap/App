package com.pasco.pascocustomer.Driver.Fragment.DriverAllBiddsDetail.Ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.Fragment.DriverAllBiddsDetail.ViewModel.GetDriverBidDetailsDataResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverAllBiddsDetail.ViewModel.GetDriverBidDetailsDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.activity.Driver.adapter.DriverAllBiddDetailAdapter
import com.pasco.pascocustomer.databinding.ActivityDriverAllBiddsBinding
import com.pasco.pascocustomer.utils.ErrorUtil

@AndroidEntryPoint
class DriverAllBiddsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverAllBiddsBinding
    private var getDriverData: List<GetDriverBidDetailsDataResponse.DriverAllBidData> = ArrayList()
    private val getDriverBidDetailsDataViewModel: GetDriverBidDetailsDataViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this@DriverAllBiddsActivity) }
    private lateinit var activity: Activity
    private var BookingID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverAllBiddsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        binding.backArrowBiddingDetailsORD.setOnClickListener {
            finish()
        }
        BookingID = intent.getStringExtra("id").toString()
        getDriverDataApi()
        getDriverDataObserver()
    }

    private fun getDriverDataApi() {
        getDriverBidDetailsDataViewModel.getDriverBidingData(
            progressDialog,
            activity,
            BookingID
        )
    }

    private fun getDriverDataObserver() {
        getDriverBidDetailsDataViewModel.progressIndicator.observe(this@DriverAllBiddsActivity) {
            // Handle progress indicator changes if needed
        }

        getDriverBidDetailsDataViewModel.mgetDBiddDataResponse.observe(this@DriverAllBiddsActivity) { response ->
            val message = response.peekContent().msg!!
            getDriverData = response.peekContent().data ?: emptyList()

            binding.recyclerBiddingDetailsORD.apply {
                isVerticalScrollBarEnabled = true
                isVerticalFadingEdgeEnabled = true
                layoutManager = LinearLayoutManager(
                    this@DriverAllBiddsActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = DriverAllBiddDetailAdapter(
                    this@DriverAllBiddsActivity,
                    getDriverData
                )
            }

            if (response.peekContent().status == "False") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        getDriverBidDetailsDataViewModel.errorResponse.observe(this@DriverAllBiddsActivity) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    override fun onResume() {
        super.onResume()
        // Call API to refresh data
        getDriverDataApi()
    }
}
