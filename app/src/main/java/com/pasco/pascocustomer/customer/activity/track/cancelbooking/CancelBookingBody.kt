package com.pasco.pascocustomer.customer.activity.track.cancelbooking

import com.google.gson.annotations.SerializedName

class CancelBookingBody(
    @SerializedName("cancelreason") var cancelreason: String
)