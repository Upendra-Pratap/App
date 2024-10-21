package com.pasco.pascocustomer.userFragment.profile.modelview

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class GetProfileResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null
    inner class Data : java.io.Serializable
    {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("full_name")
        @Expose
        var fullName: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("phone_number")
        @Expose
        var phoneNumber: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("user_type")
        @Expose
        var userType: String? = null

        @SerializedName("current_city")
        @Expose
        var currentCity: String? = null

    }


}