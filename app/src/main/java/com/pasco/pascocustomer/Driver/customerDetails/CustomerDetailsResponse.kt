package com.pasco.pascocustomer.Driver.customerDetails

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CustomerDetailsResponse:Serializable{
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: CustomerDetailsData? = null

    inner class CustomerDetailsData:Serializable{
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("full_name")
        @Expose
        var fullName: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("current_city")
        @Expose
        var currentCity: String? = null

        @SerializedName("phone_number")
        @Expose
        var phoneNumber: String? = null
    }
}