package com.pasco.pascocustomer.customer.activity.notificaion

interface NotificationClickListener {
    fun deleteNotification(position: Int, id: Int)
    fun allBids(position: Int, id: Int, pickupLatitude: Double?, pickupLongitude: Double?,)
}