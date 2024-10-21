package com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel

import com.google.gson.annotations.SerializedName

class AddBiddingBody(
    @SerializedName("availability_datetime") var availability_datetime: String,
    @SerializedName("bid_price") var bid_price: String
)