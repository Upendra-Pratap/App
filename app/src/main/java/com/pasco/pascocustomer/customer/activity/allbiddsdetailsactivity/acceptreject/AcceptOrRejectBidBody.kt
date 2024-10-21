package com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.acceptreject

import com.google.gson.annotations.SerializedName

class AcceptOrRejectBidBody (
    @SerializedName("payment_amount") var payment_amount: String,
    @SerializedName("payment_type") var payment_type: String
)