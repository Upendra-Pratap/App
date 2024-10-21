package com.pasco.pascocustomer.customer.activity.notificaion.notificationcount

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificationCountResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("count")
    @Expose
    var count: Int? = null
}