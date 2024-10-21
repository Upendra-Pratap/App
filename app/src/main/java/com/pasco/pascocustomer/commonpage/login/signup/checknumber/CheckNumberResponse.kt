package com.pasco.pascocustomer.commonpage.login.signup.checknumber

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class CheckNumberResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("exists")
    @Expose
    var exists: Int? = null
}