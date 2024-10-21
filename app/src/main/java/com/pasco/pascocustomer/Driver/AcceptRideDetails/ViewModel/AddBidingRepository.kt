package com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel

import com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel.AddBidingResponse
import io.reactivex.Observable
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import retrofit2.http.Body
import javax.inject.Inject

class AddBidingRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun addBidingRepository(
        id: String,
        @Body body: AddBiddingBody

    ): Observable<AddBidingResponse> {
        return apiService.addBidDetails(
            id,
            PascoApp.encryptedPrefs.bearerToken,
            body)
    }
}