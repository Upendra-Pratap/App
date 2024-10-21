package com.pasco.pascocustomer.Driver.StartRiding.ViewModel

import io.reactivex.Observable
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import javax.inject.Inject

class StartTripRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun addStartTripRepository(
        id: String,
        driver_status: String,


    ): Observable<StartTripResponse> {
        return apiService.startTrip(id,
            PascoApp.encryptedPrefs.bearerToken,driver_status)
    }
}