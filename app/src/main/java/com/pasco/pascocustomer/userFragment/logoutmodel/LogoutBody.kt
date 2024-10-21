package com.pasco.pascocustomer.userFragment.logoutmodel

import com.google.gson.annotations.SerializedName

class LogoutBody(
    @SerializedName("refresh") var refresh: String
)