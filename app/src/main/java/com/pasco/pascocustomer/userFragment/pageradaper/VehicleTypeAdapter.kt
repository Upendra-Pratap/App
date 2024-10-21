package com.pasco.pascocustomer.userFragment.pageradaper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.hometabactivity.AllTabPayActivity
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.adddetailsmodel.ServicesResponse
import com.pasco.pascocustomer.databinding.HomeTransportLayoutBinding

class VehicleTypeAdapter(
    private val context: Context,
    private val emirateList: List<ServicesResponse.ServicesResponseData>
) : RecyclerView.Adapter<VehicleTypeAdapter.ViewHolder>() {
    var interestName = ""

    class ViewHolder(view: HomeTransportLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val vehicleTypeName: TextView = view.vehicleTypeName
        val dashImg: ImageView = view.dashImg


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: HomeTransportLayoutBinding =
            HomeTransportLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        var typeName = emirateList[position].shipmentname
        holder.vehicleTypeName.text = typeName

        val url = emirateList[position].shipmentimage

        Glide.with(context).load(BuildConfig.IMAGE_KEY + url).placeholder(R.drawable.home_bg)
            .into(holder.dashImg)

        holder.itemView.setOnClickListener {
            val vehicleId = emirateList[position].shipmentid
            val shipmentName = emirateList[position].shipmentname
            val intent = Intent(context, AllTabPayActivity::class.java)
            intent.putExtra("vehicleId",vehicleId!!)
            intent.putExtra("shipmentName",shipmentName)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return emirateList?.size ?: 0
    }


}