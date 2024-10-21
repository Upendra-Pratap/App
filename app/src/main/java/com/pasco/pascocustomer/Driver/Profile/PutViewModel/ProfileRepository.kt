package com.pasco.pascocustomer.Profile.PutViewModel

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apiService: ApiServices){
    suspend fun putUserProfile(
        full_name: RequestBody,
        email: RequestBody,
        identify_document: MultipartBody.Part


    ): Observable<ProfileResponse> {
        return apiService.putProfile(
           PascoApp.encryptedPrefs.bearerToken,
            full_name,
            email,
            identify_document
        )
    }
}