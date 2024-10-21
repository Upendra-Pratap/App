package com.pasco.pascocustomer.Driver.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.DAllOrderResponse
import com.pasco.pascocustomer.Driver.StartRiding.Ui.DriverStartRidingActivity
import com.pasco.pascocustomer.R
import java.text.ParseException
import java.text.SimpleDateFormat

import java.util.Locale
import java.util.TimeZone

class DriverHistoryAdapter (private val context: Context, private val driverCurrentStatus:List<DAllOrderResponse.DAllOrderResponseData>)  :
    RecyclerView.Adapter<DriverHistoryAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverHistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_driver_history,parent,false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DriverHistoryAdapter.ViewHolder, position: Int) {
        val driverOrderHis = driverCurrentStatus[position]
        val price = "$${driverOrderHis.bidPrice}"
        val dateTime = driverOrderHis.pickupDatetime.toString()
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)
            holder.orderDateTimedO.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val biddingStatus = driverOrderHis.bookingStatus.toString()
        with(holder) {
            userNameDriO.text = driverOrderHis.user.toString()
            orderIDDriOrd.text = truncateBookingNumber(driverOrderHis.bookingNumber.toString())
            orderPriceTDriO.text = price
            ordersStatusCurrent.text = biddingStatus
        }
        holder.orderIDDriOrd.setOnClickListener {
            showFullAddressDialog(driverOrderHis.bookingNumber.toString())
        }
        holder.orderPriceTDriO.setOnClickListener {
            val intent = Intent(context, DriverStartRidingActivity::class.java).apply {
                putExtra("pickupLoc", driverOrderHis.pickupLocation.toString())
                putExtra("dropLoc", driverOrderHis.dropLocation.toString())
                putExtra("latitudePickUp", driverOrderHis.pickupLatitude.toString())
                putExtra("longitudePickUp", driverOrderHis.pickupLongitude.toString())
                putExtra("latitudeDrop", driverOrderHis.dropLatitude.toString())
                putExtra("longitudeDrop", driverOrderHis.dropLongitude.toString())
                putExtra("BookId", driverOrderHis.id.toString())
            }
            context.startActivity(intent)
        }
    }
    fun truncateBookingNumber(bookingNumber: String, maxLength: Int = 8): String {
        return if (bookingNumber.length > maxLength) {
            "${bookingNumber.substring(0, maxLength)}..."
        } else {
            bookingNumber
        }
    }

    fun showFullAddressDialog(fullBookingNumber: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Order ID")
        alertDialogBuilder.setMessage(fullBookingNumber)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun getItemCount(): Int {
        return driverCurrentStatus.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameDriO = itemView.findViewById<TextView>(R.id.userNameDriO)
        val orderIDDriOrd = itemView.findViewById<TextView>(R.id.orderIDDriOrd)
        val orderPriceTDriO = itemView.findViewById<TextView>(R.id.orderPriceTDriO)
        val orderDateTimedO = itemView.findViewById<TextView>(R.id.orderDateTimedO)
        val ordersStatusCurrent = itemView.findViewById<TextView>(R.id.ordersStatusCurrent)


    }
}