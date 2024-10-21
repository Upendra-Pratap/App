package com.pasco.pascocustomer.Driver.StartRiding.ViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class UpDriverStatusResponse {

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null
}