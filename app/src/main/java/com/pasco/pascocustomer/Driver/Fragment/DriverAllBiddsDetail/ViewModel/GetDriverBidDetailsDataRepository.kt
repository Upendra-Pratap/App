package com.pasco.pascocustomer.Driver.Fragment.DriverAllBiddsDetail.ViewModel

import io.reactivex.Observable
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import javax.inject.Inject

class GetDriverBidDetailsDataRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun getDriverBDataRepo(
        bookingId:String

    ): Observable<GetDriverBidDetailsDataResponse> {
        return apiService.bookingDriverData(
            bookingId,
            PascoApp.encryptedPrefs.bearerToken
        )
    }
}