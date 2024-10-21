package com.example.transportapp.DriverApp.MarkDuty

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class MarkDutyResponse {

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("Duty")
    @Expose
    var duty: Int? = null
}