package com.pasco.pascocustomer.commonpage.login.signup.checknumber

import com.google.gson.annotations.SerializedName

class CheckNumberBody(
    @SerializedName("phone_number") var phone_number: String,
    @SerializedName("user_type") var user_type: String
)