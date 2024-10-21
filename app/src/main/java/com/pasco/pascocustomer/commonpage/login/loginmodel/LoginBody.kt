package com.pasco.pascocustomer.commonpage.login.loginmodel

import com.google.gson.annotations.SerializedName

class LoginBody (
    @SerializedName("phone_number") var phone_number: String,
    @SerializedName("user_type") var user_type: String,
    @SerializedName("phone_token") var phone_token: String
)