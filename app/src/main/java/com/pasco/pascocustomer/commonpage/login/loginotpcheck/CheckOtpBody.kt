package com.pasco.pascocustomer.commonpage.login.loginotpcheck

import com.google.gson.annotations.SerializedName

class CheckOtpBody(
    @SerializedName("phone_number") var phone_number: String,
    @SerializedName("user_type") var user_type: String,
    @SerializedName("phone_verify") var phone_verify: String
)