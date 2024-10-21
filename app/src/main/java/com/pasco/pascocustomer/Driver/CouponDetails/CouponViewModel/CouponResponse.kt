package com.pasco.pascocustomer.Driver.CouponDetails.CouponViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CouponResponse:Serializable{

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<CouponDataList>? = null

    inner class CouponDataList:Serializable{

        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("poiimage")
        @Expose
        var poiimage: String? = null

        @SerializedName("poitype")
        @Expose
        var poitype: String? = null

        @SerializedName("poiname")
        @Expose
        var poiname: String? = null

        @SerializedName("couponcode")
        @Expose
        var couponcode: String? = null

        @SerializedName("startdate")
        @Expose
        var startdate: String? = null

        @SerializedName("enddate")
        @Expose
        var enddate: String? = null

        @SerializedName("couponpercent")
        @Expose
        var couponpercent: Float? = null

        @SerializedName("limit")
        @Expose
        var limit: Int? = null

        @SerializedName("poiaddress")
        @Expose
        var poiaddress: String? = null

        @SerializedName("poicity")
        @Expose
        var poicity: String? = null

        @SerializedName("poilatitude")
        @Expose
        var poilatitude: Float? = null

        @SerializedName("poilongitude")
        @Expose
        var poilongitude: Float? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null
    }
}