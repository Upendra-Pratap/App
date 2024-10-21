package com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import javax.inject.Inject

class GetAmountRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun getAmountRepository(

    ): Observable<GetAmountResponse> {
        return apiService.getUserWallet(
            PascoApp.encryptedPrefs.bearerToken
        )
    }
}