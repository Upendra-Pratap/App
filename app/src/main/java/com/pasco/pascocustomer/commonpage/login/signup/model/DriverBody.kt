package com.pasco.pascocustomer.commonpage.login.signup.model

import com.google.gson.annotations.SerializedName

class DriverBody(
    @SerializedName("full_name") var full_name: String,
    @SerializedName("email") var email: String,
    @SerializedName("phone_number") var phone_number: String,
    @SerializedName("current_city") var current_city: String,
    @SerializedName("current_location") var current_location: String,
    @SerializedName("current_latitude") var current_latitude: String,
    @SerializedName("current_longitude") var current_longitude: String,
    @SerializedName("user_type") var user_type: String,
    @SerializedName("phone_verify") var phone_verify: String,
    @SerializedName("phone_token") var phone_token: String
)