package com.pasco.pascocustomer.userFragment.logoutmodel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class LogoutResponse {
    @SerializedName("Status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null
}