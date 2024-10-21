package com.pasco.pascocustomer.reminder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.notificaion.NotificationClickListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import java.util.*

class ReminderAdapter (
    private val context: Context,
    private val onItemClick: NotificationClickListener,
    private val notificationData: List<ReminderResponse.Datum>
) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTxt: TextView = itemView.findViewById(R.id.titleTxt)
        val dateTimeTxt: TextView = itemView.findViewById(R.id.dateTimeTxt)
        val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        val okBtn: TextView = itemView.findViewById(R.id.okBtn)


    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReminderAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.reminder_user_layout, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ReminderAdapter.ViewHolder, position: Int) {
        val notificationItem = notificationData[position]

        var id = notificationItem.reminderid

        holder.titleTxt.text = notificationData[position].title
        holder.dateTimeTxt.text = notificationData[position].reminderdate
        holder.descriptionTxt.text = notificationData[position].description


        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formattedDate = currentDate.format(formatter)


        val date = notificationData[position].reminderdate
        val inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mma")
        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        val parsedDateTime = LocalDateTime.parse(date, inputFormatter)
        val parseDate = parsedDateTime.format(outputFormatter)



        holder.okBtn.setOnClickListener {
            openDeleteDialog(position, id!!)
        }

    }

    @SuppressLint("MissingInflatedId")
    private fun openDeleteDialog(position: Int, id: Int) {
        val builder = AlertDialog.Builder(context, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.delete_notification_popup, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val NoBtnAcceptNDel = dialogView.findViewById<TextView>(R.id.NoBtnAcceptNDel)
        val YesBtnAcceptNdel = dialogView.findViewById<TextView>(R.id.YesBtnAcceptNdel)

        NoBtnAcceptNDel.setOnClickListener {
            dialog.dismiss()
        }
        YesBtnAcceptNdel.setOnClickListener {
            onItemClick.deleteNotification(position, id)
            notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun getItemCount(): Int {
        return notificationData.size
    }
}