package com.pasco.pascocustomer.Driver.UpdateLocation

import io.reactivex.Observable
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import retrofit2.http.Body
import javax.inject.Inject

class UpdateLocationRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun updateLocationRepository(
        @Body body: UpdationLocationBody


        ): Observable<UpdateLocationResponse> {
        return apiService.updateGeolocation(
            PascoApp.encryptedPrefs.bearerToken,
        body)
    }
}