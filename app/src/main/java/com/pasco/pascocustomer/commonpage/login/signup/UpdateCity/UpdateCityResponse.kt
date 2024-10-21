package com.pasco.pascocustomer.commonpage.login.signup.UpdateCity

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class UpdateCityResponse:Serializable {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<updateCityList>? = null

    inner class updateCityList:Serializable{
        @SerializedName("countrycode")
        @Expose
        var countrycode: String? = null

        @SerializedName("countryname")
        @Expose
        var countryname: String? = null

        @SerializedName("cityname")
        @Expose
        var cityname: String? = null

    }

}