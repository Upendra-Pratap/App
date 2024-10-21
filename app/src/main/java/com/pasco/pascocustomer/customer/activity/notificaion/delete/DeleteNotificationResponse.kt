package com.pasco.pascocustomer.customer.activity.notificaion.delete

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeleteNotificationResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null
}