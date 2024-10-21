package com.pasco.pascocustomer.Driver.EmergencyResponse.ViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class EmergencyCResponse:Serializable{

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<EmergencyResponseData>? = null

    inner class EmergencyResponseData :Serializable
    {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("country")
        @Expose
        var country: String? = null

        @SerializedName("emergencynum")
        @Expose
        var emergencynum: String? = null
    }


}