package com.pasco.pascocustomer.Driver

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityBookingHistoryDetailsBinding
@AndroidEntryPoint
class BookingHistoryDetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityBookingHistoryDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingHistoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrowImgBhdDetails.setOnClickListener {
            finish()
        }

        binding.exmarkStartingPoint.setOnClickListener {
            openStartpopUp()
        }
        binding.exmarkDelivery.setOnClickListener {
            openDeliverypopUp()
        }
    }

    private fun openDeliverypopUp() {
        val builder = AlertDialog.Builder(this, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.end_point_details_popup, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val clearCrossDelPoint = dialogView.findViewById<ImageView>(R.id.clearCrossDelPoint)
        dialog.show()
        clearCrossDelPoint.setOnClickListener { dialog.dismiss() }
    }

    private fun openStartpopUp() {
        val builder = AlertDialog.Builder(this, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.startingpoint_exclamark_popop, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val clearCross = dialogView.findViewById<ImageView>(R.id.clearCross)
        dialog.show()
        clearCross.setOnClickListener {
            dialog.dismiss()
        }

    }
}