package com.pasco.pascocustomer.userFragment.profile.updatemodel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.repository.CommonRepository
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
class UpdateProfileModelView @Inject constructor(
    application: Application, private val customerRepository: CommonRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mCustomerResponse = MutableLiveData<Event<UpdateProfileResponse>>()
    var context: Context? = null

    fun updateProfile(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        full_name: RequestBody,
        email: RequestBody,
        currentCity: RequestBody,
        identify_document: MultipartBody.Part

    ) = viewModelScope.launch {
        updateProfiles(
            progressDialog,
            activity,
            full_name,
            email,
            currentCity,
            identify_document

        )
    }

    private fun updateProfiles(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        full_name: RequestBody,
        email: RequestBody,
        currentCity: RequestBody,
        identify_document: MultipartBody.Part
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        customerRepository.updateProfile(
            full_name,
            email,
            currentCity,
            identify_document,

            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<UpdateProfileResponse>() {
                override fun onNext(value: UpdateProfileResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mCustomerResponse.value = Event(value)
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