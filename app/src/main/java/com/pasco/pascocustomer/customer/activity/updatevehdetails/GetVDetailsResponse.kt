package com.pasco.pascocustomer.customer.activity.updatevehdetails

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetVDetailsResponse:Serializable{

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: GetVData? = null

    inner class GetVData:Serializable{
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("user")
        @Expose
        var user: String? = null

        @SerializedName("shipmentname")
        @Expose
        var shipmentname: String? = null

        @SerializedName("shipmentid")
        @Expose
        var shipmentid: Int? = null

        @SerializedName("vehiclename")
        @Expose
        var vehiclename: String? = null

        @SerializedName("cargo")
        @Expose
        var cargo: Int? = null

        @SerializedName("vehiclenumber")
        @Expose
        var vehiclenumber: String? = null

        @SerializedName("vehicle_photo")
        @Expose
        var vehiclePhoto: String? = null

        @SerializedName("document")
        @Expose
        var document: String? = null

        @SerializedName("driving_license")
        @Expose
        var drivingLicense: String? = null

        @SerializedName("approval_status")
        @Expose
        var approvalStatus: String? = null
    }

}