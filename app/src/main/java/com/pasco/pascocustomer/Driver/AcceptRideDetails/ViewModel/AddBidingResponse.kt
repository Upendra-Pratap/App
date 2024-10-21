package com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class AddBidingResponse {

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

}