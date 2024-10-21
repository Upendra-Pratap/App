package com.example.transportapp.DriverApp.MarkDuty

import com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel.AddBiddingBody
import com.pasco.pascocustomer.Driver.DriverDashboard.ViewModel.MarkDutyBody
import io.reactivex.Observable
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import retrofit2.http.Body
import javax.inject.Inject

class MarkDutyRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun putMarkRepository(
        @Body body: MarkDutyBody
    ): Observable<MarkDutyResponse> {
        return apiService.putMarkDuty(
            PascoApp.encryptedPrefs.bearerToken,body)
    }
}