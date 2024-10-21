package com.pasco.pascocustomer.userFragment.order.acceptedadapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model.AllBiddsDetailResponse
import com.pasco.pascocustomer.customer.activity.track.TrackActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AcceptedAdapter(
    private val required: Context,
    private var orderList: List<AllBiddsDetailResponse.Datum>
) :
    RecyclerView.Adapter<AcceptedAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val oderIdTxt: TextView = itemView.findViewById(R.id.oderIdTxt)
        val dateTime: TextView = itemView.findViewById(R.id.dateTime)
        val totalPriceTxt: TextView = itemView.findViewById(R.id.totalPriceTxt)
        val statusTxt: TextView = itemView.findViewById(R.id.statusTxt)
        val showDetailsBtn: ImageView = itemView.findViewById(R.id.showDetailsBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.accepted_recyler_layout, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // holder.userName.text = orderList[position].user
        holder.userName.text = orderList[position].user


        holder.oderIdTxt.text = truncateBookingNumber(orderList[position].bookingNumber.toString())
        holder.oderIdTxt.setOnClickListener {
            showFullAddressDialog(orderList[position].bookingNumber.toString())
        }

        val price = orderList[position].basicprice
        holder.totalPriceTxt.text = "$ $price"

        holder.statusTxt.text = orderList[position].bookingStatus

        val dateTime = orderList[position].pickupDatetime
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)

            holder.dateTime.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        holder.showDetailsBtn.setOnClickListener {
            val intent = Intent(required, TrackActivity::class.java)
            val pickupLatitude = orderList[position].pickupLatitude
            val pickupLongitude = orderList[position].pickupLongitude
            val dropLatitude = orderList[position].dropLatitude
            val dropLongitude = orderList[position].dropLongitude
            val bookingId = orderList[position].id
            intent.putExtra("pickupLatitude", pickupLatitude.toString())
            intent.putExtra("pickupLongitude", pickupLongitude.toString())
            intent.putExtra("dropLatitude", dropLatitude.toString())
            intent.putExtra("dropLongitude", dropLongitude.toString())
            intent.putExtra("bookingId", bookingId.toString())
            required.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    private fun truncateBookingNumber(bookingNumber: String, maxLength: Int = 8): String {
        return if (bookingNumber.length > maxLength) {
            "${bookingNumber.substring(0, maxLength)}..."
        } else {
            bookingNumber
        }
    }

    fun showFullAddressDialog(fullBookingNumber: String) {
        val alertDialogBuilder = AlertDialog.Builder(required)
        alertDialogBuilder.setTitle("Order ID")
        alertDialogBuilder.setMessage(fullBookingNumber)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}