package com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.acceptreject

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class AcceptOrRejectResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null
}