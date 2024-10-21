package com.pasco.pascocustomer.customer.activity.updatevehdetails

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PutVDetailsResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null
}