package com.pasco.pascocustomer.Driver.EmergencyResponse.Ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.Driver.EmergencyResponse.ViewModel.EmergencyCResponse
import com.pasco.pascocustomer.Driver.EmergencyResponse.ViewModel.EmergencyCViewModel
import com.pasco.pascocustomer.Driver.adapter.EmergencyAdapter
import com.pasco.pascocustomer.databinding.ActivityEmergencyCallBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import java.util.ArrayList
@AndroidEntryPoint
class EmergencyCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmergencyCallBinding
    private var getEmergencyData: List<EmergencyCResponse.EmergencyResponseData> = ArrayList()
    private lateinit var activity: Activity
    private val emergencyCViewModel: EmergencyCViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        binding.backArrowImgEmergency.setOnClickListener {
            finish()
        }

        //call api
        getEmergency()
        //call Observer
        getEmergencyObserver()

    }

    private fun getEmergencyObserver() {
        emergencyCViewModel.progressIndicator.observe(this, Observer {
            // handle progress indicator changes if needed
        })
        emergencyCViewModel.mGetEmergencyList.observe(this) { response ->
            val message = response.peekContent().msg!!
            getEmergencyData = response.peekContent().data!!

            if (response.peekContent().status.equals("False")) {
                Toast.makeText(applicationContext, "$message", Toast.LENGTH_SHORT)
                    .show()

                binding.recyclerEmerContactList.isVerticalScrollBarEnabled = true
                binding.recyclerEmerContactList.isVerticalFadingEdgeEnabled = true
                binding.recyclerEmerContactList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.recyclerEmerContactList.adapter = EmergencyAdapter(this, getEmergencyData)
            } else {
                binding.recyclerEmerContactList.isVerticalScrollBarEnabled = true
                binding.recyclerEmerContactList.isVerticalFadingEdgeEnabled = true
                binding.recyclerEmerContactList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.recyclerEmerContactList.adapter = EmergencyAdapter(this, getEmergencyData)
            }

            emergencyCViewModel.errorResponse.observe(this) {
                // Handle general errors
                ErrorUtil.handlerGeneralError(this@EmergencyCallActivity, it)
            }
        }
    }

    private fun getEmergency() {
        emergencyCViewModel.getEmeergencyListData(
            progressDialog,activity
        )
    }
}