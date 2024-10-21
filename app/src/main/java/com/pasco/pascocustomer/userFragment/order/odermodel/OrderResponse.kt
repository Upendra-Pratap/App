package com.pasco.pascocustomer.userFragment.order.odermodel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class OrderResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    inner class Datum : Serializable {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("booking_number")
        @Expose
        var bookingNumber: String? = null

        @SerializedName("user")
        @Expose
        var user: String? = null

        @SerializedName("shipmentname")
        @Expose
        var shipmentname: String? = null

        @SerializedName("vehiclename")
        @Expose
        var vehiclename: String? = null

        @SerializedName("cargo_quantity")
        @Expose
        var cargoQuantity: Int? = null

        @SerializedName("pickup_location")
        @Expose
        var pickupLocation: String? = null

        @SerializedName("pickup_latitude")
        @Expose
        var pickupLatitude: Double? = null

        @SerializedName("pickup_longitude")
        @Expose
        var pickupLongitude: Double? = null

        @SerializedName("drop_location")
        @Expose
        var dropLocation: String? = null

        @SerializedName("drop_latitude")
        @Expose
        var dropLatitude: Double? = null

        @SerializedName("drop_longitude")
        @Expose
        var dropLongitude: Double? = null

        @SerializedName("total_distance")
        @Expose
        var totalDistance: Double? = null

        @SerializedName("duration")
        @Expose
        var duration: Int? = null

        @SerializedName("basicprice")
        @Expose
        var basicprice: Double? = null

        @SerializedName("commision_price")
        @Expose
        var commisionPrice: Double? = null

        @SerializedName("pickup_datetime")
        @Expose
        var pickupDatetime: String? = null

        @SerializedName("message")
        @Expose
        var message: Any? = null

        @SerializedName("payment_method")
        @Expose
        var paymentMethod: Any? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null
    }

}