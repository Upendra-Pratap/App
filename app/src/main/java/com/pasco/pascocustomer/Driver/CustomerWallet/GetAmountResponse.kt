package com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetAmountResponse:Serializable {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: GetAmountResponseData? = null

    inner class GetAmountResponseData:Serializable
    {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("user")
        @Expose
        var user: String? = null

        @SerializedName("wallet_amount")
        @Expose
        var walletAmount: Float? = null

    }

}