package com.pasco.pascocustomer.userFragment.home.sliderpage

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SliderHomeResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<Datum>? = null

    inner class Datum : Serializable {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("usertype")
        @Expose
        var usertype: String? = null

        @SerializedName("slideimage")
        @Expose
        var slideimage: String? = null
    }


}