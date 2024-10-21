package com.pasco.pascocustomer.Driver.UpdateLocation

import com.google.gson.annotations.SerializedName

class UpdationLocationBody(
    @SerializedName("current_city") var current_city:String,
    @SerializedName("current_location") var current_location:String,
    @SerializedName("current_latitude") var latitude: String,
    @SerializedName("current_longitude") var longitude: String
)
