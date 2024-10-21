package com.pasco.pascocustomer.customer.activity.hometabactivity.cargoavailable

import com.google.gson.annotations.SerializedName

class CargoAvailableBody (
    @SerializedName("cargo") var cargo: String,
    @SerializedName("pickup_location") var pickup_location: String,
    @SerializedName("drop_location") var drop_location: String,
    @SerializedName("pickup_city") var pickup_city: String,
    @SerializedName("drop_city") var drop_city: String
)