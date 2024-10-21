package com.pasco.pascocustomer.Driver.ApprovalStatus.ViewModel

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import javax.inject.Inject

class ApprovalStatusRepository @Inject constructor(private val apiServices: ApiServices) {
    fun getcheckStatusRepository():
            Observable<ApprovalStatusResponse> {
        return apiServices.getApprovedData(PascoApp.encryptedPrefs.bearerToken)
    }
}