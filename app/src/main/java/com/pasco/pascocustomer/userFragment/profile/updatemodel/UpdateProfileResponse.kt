package com.pasco.pascocustomer.userFragment.profile.updatemodel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class UpdateProfileResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("profile")
    @Expose
    var profile: Int? = null

}