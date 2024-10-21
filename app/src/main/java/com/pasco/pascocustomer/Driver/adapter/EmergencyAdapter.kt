package com.pasco.pascocustomer.Driver.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.Driver.EmergencyResponse.ViewModel.EmergencyCResponse
import com.pasco.pascocustomer.R

class EmergencyAdapter(
    private val context: Context,
    private val emergencyNumbers: List<EmergencyCResponse.EmergencyResponseData>
) : RecyclerView.Adapter<EmergencyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_emergencyno, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emegencyResponse = emergencyNumbers[position]
        holder.SrNoTxtDetailsEmer.text = (position + 1).toString()
        val phoneNo = holder.PhoneNoDynamicEp.text.toString()
        holder.countryDynamicEp.text=emegencyResponse.country.toString()

        holder.callLinear.setOnClickListener {
            try {
                if (Build.VERSION.SDK_INT > 22) {
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.CALL_PHONE), 101)
                        return@setOnClickListener
                    }
                }
                val callIntent = Intent(Intent.ACTION_CALL)
                val phone = phoneNo
                callIntent.data = Uri.parse("tel:$phone")
                context.startActivity(callIntent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return emergencyNumbers.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var SrNoTxtDetailsEmer: TextView = itemView.findViewById(R.id.SrNoTxtDetailsEmer)
        var countryDynamicEp: TextView = itemView.findViewById(R.id.countryDynamicEp)
        var callLinear: LinearLayout = itemView.findViewById(R.id.callLinear)
        var PhoneNoDynamicEp: TextView = itemView.findViewById(R.id.PhoneNoDynamicEp)
    }
}
