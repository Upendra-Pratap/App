package com.pasco.pascocustomer.customer.activity.vehicledetailactivity.adddetailsmodel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ServicesResponse:Serializable{

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<ServicesResponseData>? = null

    inner class ServicesResponseData:Serializable{

        @SerializedName("shipmentid")
        @Expose
        var shipmentid: Int? = null

        @SerializedName("shipmentname")
        @Expose
        var shipmentname: String? = null

        @SerializedName("shipmentimage")
        @Expose
        var shipmentimage: String? = null

        @SerializedName("shipmentdescription")
        @Expose
        var shipmentdescription: String? = null
    }
}