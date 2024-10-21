package com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AllBiddsDetailResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    inner class Datum : Serializable
    {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("booking_number")
        @Expose
        var bookingNumber: String? = null

        @SerializedName("user")
        @Expose
        var user: String? = null

        @SerializedName("driver")
        @Expose
        var driver: String? = null

        @SerializedName("user_image")
        @Expose
        var userImage: Any? = null

        @SerializedName("driver_image")
        @Expose
        var driverImage: String? = null

        @SerializedName("shipmentname")
        @Expose
        var shipmentname: String? = null

        @SerializedName("vehiclename")
        @Expose
        var vehiclename: String? = null

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

        @SerializedName("customer_status")
        @Expose
        var customerStatus: String? = null

        @SerializedName("payment_status")
        @Expose
        var paymentStatus: String? = null

        @SerializedName("booking_status")
        @Expose
        var bookingStatus: String? = null

        @SerializedName("pickup_datetime")
        @Expose
        var pickupDatetime: String? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

        @SerializedName("basicprice")
        @Expose
        var basicprice: Double? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("payment_method")
        @Expose
        var paymentMethod: String? = null

        @SerializedName("upfront_payment")
        @Expose
        var upfrontPayment: Double? = null

        @SerializedName("availability_datetime")
        @Expose
        var availabilityDatetime: String? = null

        @SerializedName("bid_price")
        @Expose
        var bidPrice: Double? = null

        @SerializedName("availabledrop_datetime")
        @Expose
        var availabledropDatetime: String? = null
    }
}