package com.pasco.pascocustomer.customer.activity.driverdetails.modelview

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DriverDetailsResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("driver")
        @Expose
        var driver: String? = null

        @SerializedName("servicename")
        @Expose
        var servicename: String? = null

        @SerializedName("cargoname")
        @Expose
        var cargoname: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("current_location")
        @Expose
        var currentLocation: Any? = null

        @SerializedName("current_city")
        @Expose
        var currentCity: String? = null

        @SerializedName("phone_number")
        @Expose
        var phoneNumber: String? = null

    }

}