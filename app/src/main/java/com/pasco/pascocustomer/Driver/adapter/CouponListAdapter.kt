package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.Driver.CouponDetails.CouponViewModel.CouponResponse
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.Driver.CouponUsedActivity

class CouponListAdapter(
    private val context: Context,
    private val couponData: List<CouponResponse.CouponDataList>): RecyclerView.Adapter<CouponListAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CouponListAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_coupons, parent, false)
        return CouponListAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CouponListAdapter.ViewHolder, position: Int) {
      holder.SrNoTxtDetailsCl.text =  (position + 1).toString()
        val cpData = couponData[position]

        with(holder) {
          //  countryDynamicCl.text = serialNo.toString()
            poiTypeDynamicCl.text = cpData.poitype.toString()
            providerNameDynamicCl.text = cpData.poiname.toString()
            couponTypeDynamicCl.text = cpData.couponcode.toString()
            LimitStaticDynamicCL.text = cpData.limit.toString()

        }
        holder.seeViewDetails.setOnClickListener {
            val intent = Intent(context,CouponUsedActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
      return couponData.size
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var SrNoTxtDetailsCl = itemView.findViewById<TextView>(R.id.SrNoTxtDetailsCl)
        var poiTypeDynamicCl = itemView.findViewById<TextView>(R.id.poiTypeDynamicCl)
        var providerNameDynamicCl = itemView.findViewById<TextView>(R.id.providerNameDynamicCl)
        var couponTypeDynamicCl = itemView.findViewById<TextView>(R.id.couponTypeDynamicCl)
        var seeViewDetails = itemView.findViewById<ImageView>(R.id.seeViewDetails)
        var LimitStaticDynamicCL = itemView.findViewById<TextView>(R.id.LimitStaticDynamicCl)
    }


}