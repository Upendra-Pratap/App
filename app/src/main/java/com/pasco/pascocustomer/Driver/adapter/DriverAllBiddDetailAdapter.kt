package com.pasco.pascocustomer.activity.Driver.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.AcceptRideDetails.Ui.AcceptRideActivity
import com.pasco.pascocustomer.Driver.Fragment.DriverAllBiddsDetail.ViewModel.GetDriverBidDetailsDataResponse
import com.pasco.pascocustomer.Driver.StartRiding.Ui.DriverStartRidingActivity
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.GetRouteUpdateResponse
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.GetRouteUpdateViewModel
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.StartTripViewModel
import com.pasco.pascocustomer.Driver.customerDetails.CustomerDetailsActivity
import de.hdodenhof.circleimageview.CircleImageView
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.utils.ErrorUtil
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
class DriverAllBiddDetailAdapter(
    private val context: Context,
    private val getDriverData: List<GetDriverBidDetailsDataResponse.DriverAllBidData>,
    private var BookIddd: String = ""
) : RecyclerView.Adapter<DriverAllBiddDetailAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val driverSeeUserProfile: CircleImageView = itemView.findViewById(R.id.driverSeeUserProfile)
        val pickUpDetailsORD: TextView = itemView.findViewById(R.id.pickUpDetailsORD)
        val DropDetailsORD: TextView = itemView.findViewById(R.id.DropDetailsORD)
        val distanceDORD: TextView = itemView.findViewById(R.id.distanceDORD)
        val totalPricestaticDORD: TextView = itemView.findViewById(R.id.totalPricestaticDORD)
        val maxPriceDORD: TextView = itemView.findViewById(R.id.maxPriceDORD)
        val orderIdDynamicDORD: TextView = itemView.findViewById(R.id.orderIdDynamicDORD)
        val clientNameOrdR: TextView = itemView.findViewById(R.id.clientNameOrdR)
        val orderDetailDR: TextView = itemView.findViewById(R.id.orderDetailDR)
        val cPriceDORD: TextView = itemView.findViewById(R.id.cPriceDORD)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_order_details, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return getDriverData.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookingReq = getDriverData[position]
        BookIddd = bookingReq.id.toString()
        val price = "$${bookingReq.basicprice}"
        val bPrice = "$${bookingReq.bidPrice}"
        val commissionPrice = "$${bookingReq.commissionPrice}"
        val dateTime = bookingReq.pickupDatetime.toString()
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        val baseUrl = "http://69.49.235.253:8090"
        val imagePath = bookingReq.userImage.orEmpty()

        val imageUrl = "$baseUrl$imagePath"
        Glide.with(context)
            .load(imageUrl)
            .into(holder.driverSeeUserProfile)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)
            holder.orderDetailDR.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val status = bookingReq.customerStatus.toString()
        Log.e("status", "onBindViewHolder: $status")

        with(holder) {
            val pickupCity = bookingReq.pickupLocation.toString()
            val dropCity = bookingReq.dropLocation.toString()
            pickUpDetailsORD.text = pickupCity
            DropDetailsORD.text = dropCity
            val formattedDistance = String.format("%.1f", bookingReq.totalDistance)
            distanceDORD.text = "$formattedDistance km"

            totalPricestaticDORD.text = price
            maxPriceDORD.text = bPrice
            cPriceDORD.text = commissionPrice
            clientNameOrdR.text = bookingReq.user
            orderIdDynamicDORD.text = truncateBookingNumber(bookingReq.bookingNumber.toString())
            driverSeeUserProfile.setOnClickListener {
                val id = bookingReq.id.toString()
                val intent = Intent(context, CustomerDetailsActivity::class.java)
                intent.putExtra("customerId", id)
                context.startActivity(intent)
            }

            orderIdDynamicDORD.setOnClickListener {
                showFullAddressDialog(bookingReq.bookingNumber.toString())
            }
        }
    }

    private fun truncateBookingNumber(bookingNumber: String, maxLength: Int = 8): String {
        return if (bookingNumber.length > maxLength) {
            "${bookingNumber.substring(0, maxLength)}..."
        } else {
            bookingNumber
        }
    }

    private fun showFullAddressDialog(fullBookingNumber: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Order ID")
        alertDialogBuilder.setMessage(fullBookingNumber)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
