package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.Driver.ApprovalStatus.ViewModel.ApprovalStatusResponse
import de.hdodenhof.circleimageview.CircleImageView

class ApprovalStatusAdapter(
    private val context: Context,
    private val approveData: List<ApprovalStatusResponse.ApprovalStatusData>
) : RecyclerView.Adapter<ApprovalStatusAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val circleApproveImg: CircleImageView = itemView.findViewById(R.id.circleApproveImg)
        val vehicleNameApprove: TextView = itemView.findViewById(R.id.vehicleNameApprove)
        val driverNameApprove: TextView = itemView.findViewById(R.id.driverNameApprove)
        val approvedStatus: TextView = itemView.findViewById(R.id.approvedStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_approve_requests, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until approveData.size) {
            val approvedItem = approveData[position]
            val baseUrl = "http://69.49.235.253:8090"
            val approval = approvedItem.vehiclePhoto
            val imageUrl = "$baseUrl$approval"

            // Load the image into an ImageView using Glide
            Glide.with(context)
                .load(imageUrl)
                .into(holder.circleApproveImg)

            val userName = approvedItem.user ?: "Unknown User"
            val vehname = approvedItem.vehiclename ?: "Unknown Vehicle"
            val approvalStatus = approvedItem.approvalStatus ?: "Unknown Status"

            holder.driverNameApprove.text = userName
            holder.vehicleNameApprove.text = vehname
            holder.approvedStatus.text = approvalStatus
        }
    }

    override fun getItemCount(): Int {
        return approveData.size
    }
}
