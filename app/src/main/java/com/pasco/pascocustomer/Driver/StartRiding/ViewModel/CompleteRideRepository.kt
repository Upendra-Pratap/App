package com.pasco.pascocustomer.Driver.StartRiding.ViewModel

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import javax.inject.Inject

class CompleteRideRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun getCompleteDriverRide(
        Id:String

    ): Observable<CompleteRideResponse> {
        return apiService.getCompletedRide(
            Id,
            PascoApp.encryptedPrefs.bearerToken
        )
    }
}