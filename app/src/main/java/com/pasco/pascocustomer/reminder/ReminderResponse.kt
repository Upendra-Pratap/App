package com.pasco.pascocustomer.reminder

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class ReminderResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    inner class Datum : java.io.Serializable
    {
        @SerializedName("reminderid")
        @Expose
        var reminderid: Int? = null

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("reminderdate")
        @Expose
        var reminderdate: String? = null

        @SerializedName("remindercheck")
        @Expose
        var remindercheck: Int? = null
    }
}