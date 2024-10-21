package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.Driver.RideRequestResponse
import com.pasco.pascocustomer.R

class CouponUsedAdapter(
    private val context:Context,
    private val acceptRide: List<RideRequestResponse>): RecyclerView.Adapter<CouponUsedAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CouponUsedAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_coupon_usedby, parent, false)
        return CouponUsedAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CouponUsedAdapter.ViewHolder, position: Int) {
        holder.SrNoTxtDetailsLCouponUsedBy.text =  (position + 1).toString()
    }

    override fun getItemCount(): Int {
        return 10
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var SrNoTxtDetailsLCouponUsedBy = itemView.findViewById<TextView>(R.id.SrNoTxtDetailsLCouponUsedBy)
    }


}