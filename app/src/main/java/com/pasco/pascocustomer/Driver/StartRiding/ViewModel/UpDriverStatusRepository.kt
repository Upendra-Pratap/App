package com.pasco.pascocustomer.Driver.StartRiding.ViewModel

import io.reactivex.Observable
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import javax.inject.Inject

class UpDriverStatusRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun updateDriverSRepository(
        id: String,
        driver_status: String
    ): Observable<UpDriverStatusResponse> {
        return apiService.getUpdateDriverStatus(
            id,
            PascoApp.encryptedPrefs.bearerToken,driver_status
        )
    }
}