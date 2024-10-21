package com.pasco.pascocustomer.customer.activity.hometabactivity.modelview

import com.google.gson.annotations.SerializedName

class BookingOrderBody(
    @SerializedName("cargo") var cargo: String,
    @SerializedName("cargo_quantity") var cargo_quantity: String,
    @SerializedName("pickup_location") var pickup_location: String,
    @SerializedName("drop_location") var drop_location: String,
    @SerializedName("pickup_city") var pickup_city: String,
    @SerializedName("drop_city") var drop_city: String,
    @SerializedName("pickup_latitude") var pickup_latitude: String,
    @SerializedName("pickup_longitude") var pickup_longitude: String,
    @SerializedName("drop_latitude") var drop_latitude: String,
    @SerializedName("drop_longitude") var drop_longitude: String,
    @SerializedName("pickup_datetime") var pickup_datetime: String,
    @SerializedName("message") var message: String,
    @SerializedName("payment_method") var payment_method: String,
    @SerializedName("additional_service") var additional_service: String
)