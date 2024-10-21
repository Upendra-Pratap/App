package com.pasco.pascocustomer.Driver.NotesRemainders.ViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class NotesRResponse:Serializable {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<ReminderList>? = null

    inner class ReminderList:Serializable
    {

    }
}