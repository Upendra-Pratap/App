package com.pasco.pascocustomer.commonpage.login.signup.clientmodel

import com.google.gson.annotations.SerializedName

class ClientSignupBody (
    @SerializedName("phone_number") var phone_number: String,
    @SerializedName("user_type") var user_type: String,
    @SerializedName("phone_verify") var phone_verify: String,
    @SerializedName("phone_token") var phone_token: String
)