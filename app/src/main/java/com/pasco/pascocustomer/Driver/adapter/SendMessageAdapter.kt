package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.Driver.RideRequestResponse
import com.pasco.pascocustomer.R

class SendMessageAdapter(
    private val context: Context,
    private val sendMessageData: List<RideRequestResponse>
) : RecyclerView.Adapter<SendMessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_send_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.chatCMsgLeft.text = "Hi Virat"
    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatCMsgLeft: TextView = itemView.findViewById(R.id.chatCMsgLeft)

    }
}
