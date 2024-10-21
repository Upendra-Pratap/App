package com.pasco.pascocustomer.Driver.UpdateLocation

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class UpdateLocationResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

}