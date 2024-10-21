package com.pasco.pascocustomer.Profile.PutViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val   profileRepository: ProfileRepository
) : AndroidViewModel(application) {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mputProfileResponse = MutableLiveData<Event<ProfileResponse>>()
    var context: Context? = null

    fun putProfile(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        full_name: RequestBody,
        email: RequestBody,
        identify_document: MultipartBody.Part
    ) =
        viewModelScope.launch {
            putProfileM(progressDialog, activity, full_name,email,identify_document)
        }

    suspend fun putProfileM(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        full_name: RequestBody,
        email: RequestBody,
        identify_document: MultipartBody.Part
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        profileRepository.putUserProfile( full_name,email, identify_document)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<ProfileResponse>() {
                override fun onNext(value: ProfileResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mputProfileResponse.value = Event(value)
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