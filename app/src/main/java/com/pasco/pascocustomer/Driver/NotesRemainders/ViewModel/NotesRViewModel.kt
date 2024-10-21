package com.pasco.pascocustomer.Driver.NotesRemainders.ViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.utils.Event
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class NotesRViewModel @Inject constructor(
    application: Application,
    private val notesRRepository: NotesRRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mGetNotesResponse = MutableLiveData<Event<NotesRResponse>>()
    var context: Context? = null

    fun getNotesReminderData(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        title: String,
        description: String,
        reminderdate: String
    ) =
        viewModelScope.launch {
            getNotesReminderDatas(progressDialog,activity,title,description,reminderdate)
        }

    private suspend fun getNotesReminderDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        title: String,
        description: String,
        reminderdate: String
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        notesRRepository.getNotesReminder(title, description, reminderdate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<NotesRResponse>() {
                override fun onNext(value: NotesRResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mGetNotesResponse.value = Event(value)
                }
                override fun onError(e: Throwable) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    errorResponse.value = e
                }

                override fun onComplete() {
                    progressDialog.stop()
                    progressIndicator.value = false
                }
            })
    }



}