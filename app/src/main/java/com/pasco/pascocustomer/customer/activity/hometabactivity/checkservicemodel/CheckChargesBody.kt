package com.pasco.pascocustomer.customer.activity.hometabactivity.checkservicemodel

import com.google.gson.annotations.SerializedName

class CheckChargesBody (
    @SerializedName("cargo") var cargo: String,
    @SerializedName("pickup_location") var pickup_location: String,
    @SerializedName("drop_location") var drop_location: String,
    @SerializedName("pickup_latitude") var pickup_latitude: String,
    @SerializedName("pickup_longitude") var pickup_longitude: String,
    @SerializedName("drop_latitude") var drop_latitude: String,
    @SerializedName("drop_longitude") var drop_longitude: String
)