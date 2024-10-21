package com.pasco.pascocustomer.activity.Driver.AddVehicle.ApprovalRequest

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class ApprovalRequestResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: VehicleData? = null

    inner class VehicleData{

        @SerializedName("user")
        @Expose
        var user: String? = null

        @SerializedName("cargo")
        @Expose
        var cargo: String? = null

        @SerializedName("vehicle_photo")
        @Expose
        var vehiclePhoto: String? = null

        @SerializedName("document")
        @Expose
        var document: String? = null

        @SerializedName("driving_license")
        @Expose
        var drivingLicense: String? = null

        @SerializedName("vehiclenumber")
        @Expose
        var vehiclenumber: String? = null

        @SerializedName("approved")
        @Expose
        var approved: Int? = null
    }


}