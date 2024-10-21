package com.pasco.pascocustomer.Driver.adapter
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CancelledTripResponse.CancelledData
import com.pasco.pascocustomer.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class CancelledTripHistoryAdapter(
    private val context: Context,
    private val cancelTripHistory: List<CancelledData>
) :
    RecyclerView.Adapter<CancelledTripHistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recycler_cancelled_trip, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cancelTripHis = cancelTripHistory[position]
        val price = "$" + cancelTripHis.bidPrice
        val dBookingStatus = cancelTripHis.bookingStatus
        val dateTime = cancelTripHis.availabilityDatetime
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)
        val url = cancelTripHis.userImage
        Glide.with(context).load(BuildConfig.IMAGE_KEY + url)
            .placeholder(R.drawable.home_bg).into(holder.driverProfileCthCan)
        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault()
            val formattedDateTime = outputDateFormat.format(parsedDate)
            holder.dateTimeDriHisCan.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        holder.clientNameDriHisCan.text = cancelTripHis.user
        holder.totalCostDriverHisCan.text = price
        val durationInMinutes = cancelTripHis.duration!!
        val hours = durationInMinutes / 60
        val minutes = durationInMinutes % 60
        val durationString = "$hours hours $minutes min"
        holder.arrivalTimeDriverHisCan.text = durationString
        holder.pickUpDetailsDriHisCan.text = cancelTripHis.pickupLocation
        holder.DropDetailsDriHisCan.text = cancelTripHis.dropLocation
        holder.bookingstatusCan.text = "Cancelled"
        holder.cancelReasonTextView.text = cancelTripHis.cancelreason.toString()
    }

    override fun getItemCount(): Int {
        return cancelTripHistory.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var clientNameDriHisCan: TextView
        var totalCostDriverHisCan: TextView
        var arrivalTimeDriverHisCan: TextView
        var pickUpDetailsDriHisCan: TextView
        var DropDetailsDriHisCan: TextView
        var dateTimeDriHisCan: TextView
        var bookingstatusCan: TextView
        var cancelReasonTextView: TextView
        var driverProfileCthCan: ImageView

        init {
            clientNameDriHisCan = itemView.findViewById(R.id.clientNameDriHisCan)
            totalCostDriverHisCan = itemView.findViewById(R.id.totalCostDriverHisCan)
            arrivalTimeDriverHisCan = itemView.findViewById(R.id.arrivalTimeDriverHisCan)
            pickUpDetailsDriHisCan = itemView.findViewById(R.id.pickUpDetailsDriHisCan)
            DropDetailsDriHisCan = itemView.findViewById(R.id.DropDetailsDriHisCan)
            dateTimeDriHisCan = itemView.findViewById(R.id.dateTimeDriHisCan)
            driverProfileCthCan = itemView.findViewById(R.id.driverProfileCthCan)
            bookingstatusCan = itemView.findViewById(R.id.bookingstatusCan)
            cancelReasonTextView = itemView.findViewById(R.id.cancelReasonTextView)
        }
    }
}