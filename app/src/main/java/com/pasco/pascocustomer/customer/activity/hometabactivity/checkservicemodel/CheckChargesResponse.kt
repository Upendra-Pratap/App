package com.pasco.pascocustomer.customer.activity.hometabactivity.checkservicemodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CheckChargesResponse :Serializable{
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("servicename")
    @Expose
    var servicename: String? = null

    @SerializedName("cargoname")
    @Expose
    var cargoname: String? = null

    @SerializedName("cargoimage")
    @Expose
    var cargoimage: String? = null

    @SerializedName("distance")
    @Expose
    var distance: Double? = null

    @SerializedName("price")
    @Expose
    var price: Double? = null

    @SerializedName("pickup_location")
    @Expose
    var pickupLocation: String? = null

    @SerializedName("drop_location")
    @Expose
    var dropLocation: String? = null

    @SerializedName("duration")
    @Expose
    var duration: Duration? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null
    inner class Duration: Serializable
    {

        @SerializedName("hours")
        @Expose
        var hours: Int? = null

        @SerializedName("minutes")
        @Expose
        var minutes: Int? = null

    }

}