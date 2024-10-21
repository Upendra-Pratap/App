package com.pasco.pascocustomer.Driver.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.commonpage.login.signup.UpdateCity.UpdateCityResponse
import com.pasco.pascocustomer.customer.activity.SignUpCityName
import java.util.*

class UpdateAddressAdapter(
    private val required: Context,
    private var cityList: List<UpdateCityResponse.updateCityList>,
    private val onItemClick: SignUpCityName,
) :
    RecyclerView.Adapter<UpdateAddressAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.cityName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.city_name, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // holder.userName.text = orderList[position].user
        holder.cityName.text = cityList[position].cityname

        holder.itemView.setOnClickListener {
            val cityName = cityList[position].cityname
            onItemClick.itemCity(position, cityName!!)
        }
    }

    override fun getItemCount(): Int {
        return cityList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(eventLists: List<UpdateCityResponse.updateCityList>) {
        this.cityList = eventLists
        notifyDataSetChanged()
    }


}