package com.pasco.pascocustomer.Driver.ApprovalStatus.ViewModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ApprovalStatusResponse: Serializable {

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<ApprovalStatusData>? = null

    inner class ApprovalStatusData : Serializable {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("user")
        @Expose
        var user: String? = null

        @SerializedName("shipmentname")
        @Expose
        var shipmentname: String? = null

        @SerializedName("vehiclename")
        @Expose
        var vehiclename: String? = null

        @SerializedName("countryname")
        @Expose
        var countryname: String? = null

        @SerializedName("cityname")
        @Expose
        var cityname: String? = null

        @SerializedName("servecountry")
        @Expose
        var servecountry: String? = null

        @SerializedName("servecity")
        @Expose
        var servecity: String? = null

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

        @SerializedName("comment")
        @Expose
        var comment: Any? = null

    }
}