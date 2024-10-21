package com.pasco.pascocustomer.activity.Driver.AddVehicle.ApprovalRequest

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
class ApprovalRequestViewModel@Inject constructor(
    application: Application,
    private val approvalReqRepsitory: CommonRepository
) : AndroidViewModel(application) {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mApprovalResponse = MutableLiveData<Event<ApprovalRequestResponse>>()
    var context: Context? = null

    fun getReqApproval(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        cargo: RequestBody,
        vehiclenumber: RequestBody,
        identify_document: MultipartBody.Part,
        identify_document1: MultipartBody.Part,
        identify_document2: MultipartBody.Part
    ) =
        viewModelScope.launch {
            userApproveReq(progressDialog, activity,cargo,vehiclenumber,identify_document,identify_document1,identify_document2)
        }

    suspend fun userApproveReq(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        cargo: RequestBody,
        vehiclenumber: RequestBody,
        identify_document: MultipartBody.Part,
        identify_document1: MultipartBody.Part,
        identify_document2: MultipartBody.Part
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        approvalReqRepsitory.getApprovalReqRepo(cargo,vehiclenumber,identify_document,identify_document1,identify_document2)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<ApprovalRequestResponse>() {
                override fun onNext(value: ApprovalRequestResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mApprovalResponse.value = Event(value)
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