package com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DAllOrderResponse:Serializable {

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<DAllOrderResponseData>? = null

    inner class DAllOrderResponseData:Serializable{

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
        var userImage: String? = null

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
        var pickupLatitude: Float? = null

        @SerializedName("pickup_longitude")
        @Expose
        var pickupLongitude: Float? = null

        @SerializedName("drop_location")
        @Expose
        var dropLocation: String? = null

        @SerializedName("drop_latitude")
        @Expose
        var dropLatitude: Float? = null

        @SerializedName("drop_longitude")
        @Expose
        var dropLongitude: Float? = null

        @SerializedName("total_distance")
        @Expose
        var totalDistance: Float? = null

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
        var basicprice: Float? = null

        @SerializedName("message")
        @Expose
        var message: Any? = null

        @SerializedName("payment_method")
        @Expose
        var paymentMethod: Any? = null

        @SerializedName("upfront_payment")
        @Expose
        var upfrontPayment: Float? = null

        @SerializedName("availability_datetime")
        @Expose
        var availabilityDatetime: String? = null

        @SerializedName("bid_price")
        @Expose
        var bidPrice: Float? = null

        @SerializedName("availabledrop_datetime")
        @Expose
        var availabledropDatetime: Any? = null
    }
}