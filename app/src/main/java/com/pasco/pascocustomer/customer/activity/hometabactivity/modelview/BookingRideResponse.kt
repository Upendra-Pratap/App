package com.pasco.pascocustomer.customer.activity.hometabactivity.modelview

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BookingRideResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("available_driver")
    @Expose
    var availableDriver: Int? = null
}