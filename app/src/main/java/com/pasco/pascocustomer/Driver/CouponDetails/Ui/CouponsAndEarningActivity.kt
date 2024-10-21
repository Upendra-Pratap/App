package com.pasco.pascocustomer.Driver.CouponDetails.Ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.Driver.CouponDetails.CouponViewModel.CouponResponse
import com.pasco.pascocustomer.Driver.CouponDetails.CouponViewModel.CouponViewModel
import com.pasco.pascocustomer.Driver.adapter.CouponListAdapter
import com.pasco.pascocustomer.databinding.ActivityCouponsAndEarningBinding

import java.util.ArrayList
@AndroidEntryPoint
class CouponsAndEarningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCouponsAndEarningBinding
    private var checkCouponList: List<CouponResponse.CouponDataList> = ArrayList()
    private lateinit var activity:Activity
    private val couponViewModel: CouponViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCouponsAndEarningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrowImgCouponsBonus.setOnClickListener {
            finish()
        }
        activity = this

        //call api
        couponDetailList()
        //call observer
        couponObserver()

        binding.recyclerCouponList.isVerticalScrollBarEnabled = true
        binding.recyclerCouponList.isVerticalFadingEdgeEnabled = true
        binding.recyclerCouponList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerCouponList.adapter = CouponListAdapter(this, checkCouponList)
    }

    private fun couponObserver() {
        couponViewModel.mGetCouponList.observe(this) { response ->
            val message = response.peekContent().msg!!
            checkCouponList = response.peekContent().data ?: emptyList()
            if (checkCouponList?.isEmpty()!!) {
                Toast.makeText(this@CouponsAndEarningActivity, "No Data Found", Toast.LENGTH_SHORT).show()
                binding.recyclerCouponList.isVerticalScrollBarEnabled = true
                binding.recyclerCouponList.isVerticalFadingEdgeEnabled = true
                binding.recyclerCouponList.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.recyclerCouponList.adapter = CouponListAdapter(this, checkCouponList)
            }

            if (response.peekContent().status.equals("False")) {
                Toast.makeText(this, "failed: $message", Toast.LENGTH_LONG).show()
            } else {
                // Uncomment the RecyclerView setup
                binding.recyclerCouponList.isVerticalScrollBarEnabled = true
                binding.recyclerCouponList.isVerticalFadingEdgeEnabled = true
                binding.recyclerCouponList.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.recyclerCouponList.adapter = CouponListAdapter(this, checkCouponList)
            }

        }
    }

    private fun couponDetailList() {
        couponViewModel.getCouponListData(
            progressDialog,
            activity
        )
    }
}