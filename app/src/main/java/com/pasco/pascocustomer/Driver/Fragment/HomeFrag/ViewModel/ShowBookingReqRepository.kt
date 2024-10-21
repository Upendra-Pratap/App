package com.pasco.pascocustomer.Driver.Fragment.HomeFrag.ViewModel

import io.reactivex.Observable
import com.pasco.pascocustomer.services.ApiServices
import javax.inject.Inject

class ShowBookingReqRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun getShowBookingRequests(

    ): Observable<ShowBookingReqResponse> {
        return apiService.getBookingReq(
        )
    }
}