package com.pasco.pascocustomer.Driver.NotesRemainders.ViewModel

import io.reactivex.Observable
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import javax.inject.Inject

class NotesRRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun getNotesReminder(
        title: String,
        description: String,
        reminderdate: String,
    ): Observable<NotesRResponse>
    {
        return apiService.addNotesReminder(
            PascoApp.encryptedPrefs.bearerToken,
            title,description,reminderdate)
    }
}