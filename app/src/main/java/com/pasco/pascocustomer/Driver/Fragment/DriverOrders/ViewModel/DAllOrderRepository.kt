package com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel

import io.reactivex.Observable
import com.pasco.pascocustomer.services.ApiServices
import javax.inject.Inject

class DAllOrderRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun getAllOrderRepo(

    ): Observable<DAllOrderResponse> {
        return apiService.getAllOrderDriver(
        )
    }
}