package com.pasco.pascocustomer.Driver.StartRiding.ViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetRouteUpdateResponse : Serializable {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<RouteResponseData>? = null

    inner class RouteResponseData : Serializable {

        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("status")
        @Expose
        var status: String? = null
    }
}