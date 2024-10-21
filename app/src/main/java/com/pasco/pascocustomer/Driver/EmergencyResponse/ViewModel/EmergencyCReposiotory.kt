package com.pasco.pascocustomer.Driver.EmergencyResponse.ViewModel

import io.reactivex.Observable
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import javax.inject.Inject

    class EmergencyCReposiotory@Inject constructor(private val apiService: ApiServices) {
        suspend fun getEmregencyRepo(

        ): Observable<EmergencyCResponse> {
            return apiService.getEmergencyList(
                PascoApp.encryptedPrefs.bearerToken
            )
        }
    }