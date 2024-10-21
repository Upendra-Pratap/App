package com.pasco.pascocustomer.commonpage.login.loginmodel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class LoginResponse {
    @SerializedName("token")
    @Expose
    var token: Token? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null

    @SerializedName("user_type")
    @Expose
    var userType: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("full_name")
    @Expose
    var fullName: String? = null

    @SerializedName("profile")
    @Expose
    var profile: Int? = null

    @SerializedName("approved")
    @Expose
    var approved: Int? = null



    inner class Token : java.io.Serializable {
        @SerializedName("refresh")
        @Expose
        var refresh: String? = null

        @SerializedName("access")
        @Expose
        var access: String? = null
    }
}