package com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import javax.inject.Inject

class AddAmountRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun addUserWalletRepo(
        amount: String

    ): Observable<AddAmountResponse> {
        return apiService.addUserWallet(
            PascoApp.encryptedPrefs.bearerToken,
        amount)
    }
}