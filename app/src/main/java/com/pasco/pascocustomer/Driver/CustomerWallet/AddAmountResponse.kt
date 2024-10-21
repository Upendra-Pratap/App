package com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class AddAmountResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("amount")
    @Expose
    var amount: String? = null
}