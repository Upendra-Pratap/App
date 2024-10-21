package com.pasco.pascocustomer.Driver.ApprovalStatus.Ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasco.pascocustomer.Driver.ApprovalStatus.ViewModel.ApprovalStatusResponse
import com.pasco.pascocustomer.Driver.ApprovalStatus.ViewModel.ApprovalStatusViewModel
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.VehicleDetailsActivity
import com.pasco.pascocustomer.databinding.ActivityApprovalStatusBinding
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.Driver.adapter.ApprovalStatusAdapter


import java.util.ArrayList
@AndroidEntryPoint
class ApprovalStatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApprovalStatusBinding
    private var approveData: List<ApprovalStatusResponse.ApprovalStatusData> = ArrayList()
    private val approvalStatusViewModel: ApprovalStatusViewModel by viewModels()
    private lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApprovalStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        binding.backImageApps.setOnClickListener {
            finish()
        }

        binding.updateDocuments.setOnClickListener {
            val intent = Intent(this@ApprovalStatusActivity, VehicleDetailsActivity::class.java)
            startActivity(intent)
        }

        //Booking All Ride Observer
        approvedStatusObserver()

        //call approved status api
        approvedStatus()


    }

        private fun approvedStatus() {
            approvalStatusViewModel.getCheckApproveBooking(
                activity
            )
        }

    private fun approvedStatusObserver() {
        approvalStatusViewModel.mCheckApproveResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            approveData = response.peekContent().data ?: emptyList()
            if (approveData?.isEmpty()!!) {
                Toast.makeText(this@ApprovalStatusActivity, "No Data Found", Toast.LENGTH_SHORT).show()
            }

            if (response.peekContent().status.equals("False")) {
                Toast.makeText(this, "failed: $message", Toast.LENGTH_LONG).show()
            } else {
                for (i in 0 until approveData.size) {
                    // Access each item in the list using approvedData[i]
                    val currentItem = approveData[i]

                    // Access the ID property of the current item
                    val currentItemId = currentItem.id.toString()
                   PascoApp.encryptedPrefs.approvedId = currentItemId

                    //  Toast.makeText(this, "fdfffa"+currentItemId, Toast.LENGTH_SHORT).show()
                    val approvalStatus = currentItem.approvalStatus
                }
                // Uncomment the RecyclerView setup
                binding.recyclerCheckStatus.isVerticalScrollBarEnabled = true
                binding.recyclerCheckStatus.isVerticalFadingEdgeEnabled = true
                binding.recyclerCheckStatus.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.recyclerCheckStatus.adapter = ApprovalStatusAdapter(this, approveData)
            }

        }
    }
}

