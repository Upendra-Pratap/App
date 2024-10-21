package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.Driver.RideRequestResponse
import com.pasco.pascocustomer.R

class LoyaltyBonusAdapter(
    private val context: Context,
    private val acceptRide: List<RideRequestResponse>): RecyclerView.Adapter<LoyaltyBonusAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LoyaltyBonusAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_loyalty_bonus, parent, false)
        return LoyaltyBonusAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoyaltyBonusAdapter.ViewHolder, position: Int) {
        holder.SrNoTxtDetailsL.text =  (position + 1).toString()

    }

    override fun getItemCount(): Int {
        return 10
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var SrNoTxtDetailsL = itemView.findViewById<TextView>(R.id.SrNoTxtDetailsL)
        var seeViewDetailsLoyaltyProgram = itemView.findViewById<ImageView>(R.id.seeViewDetailsLoyaltyProgram)
    }


}