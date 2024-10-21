package com.pasco.pascocustomer.Profile.PutViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class ProfileResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

}