package com.pasco.pascocustomer.customer.activity.hometabactivity.additionalservice

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class AdditionalServiceResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    inner class Datum : Serializable {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("additional_type")
        @Expose
        var additionalType: String? = null

        @SerializedName("additional_amount")
        @Expose
        var additionalAmount: Double? = null

    }
}