package com.pasco.pascocustomer.customer.activity.notificaion.modelview

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class NotificationResponse {
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

        @SerializedName("recipient")
        @Expose
        var recipient: String? = null

        @SerializedName("sender")
        @Expose
        var sender: String? = null

        @SerializedName("notification_title")
        @Expose
        var notificationTitle: String? = null

        @SerializedName("notification_description")
        @Expose
        var notificationDescription: String? = null

        @SerializedName("reader")
        @Expose
        var reader: Boolean? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

    }
}