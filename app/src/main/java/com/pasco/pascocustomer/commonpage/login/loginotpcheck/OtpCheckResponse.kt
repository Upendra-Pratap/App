package com.pasco.pascocustomer.commonpage.login.loginotpcheck

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class OtpCheckResponse {
    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("Login")
    @Expose
    var login: Int? = null

}