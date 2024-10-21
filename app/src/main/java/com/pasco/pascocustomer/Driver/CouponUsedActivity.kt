package com.pasco.pascocustomer.Driver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasco.pascocustomer.Driver.adapter.CouponUsedAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.databinding.ActivityCouponUsedBinding

import java.util.ArrayList
@AndroidEntryPoint
class CouponUsedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCouponUsedBinding
    private var couponUsedByList: List<RideRequestResponse> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCouponUsedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrowImgCouponsUBY.setOnClickListener {
            finish()
        }

        binding.recyclerCouponUsedByList.isVerticalScrollBarEnabled = true
        binding.recyclerCouponUsedByList.isVerticalFadingEdgeEnabled = true
        binding.recyclerCouponUsedByList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerCouponUsedByList.adapter = CouponUsedAdapter(this, couponUsedByList)
    }
}