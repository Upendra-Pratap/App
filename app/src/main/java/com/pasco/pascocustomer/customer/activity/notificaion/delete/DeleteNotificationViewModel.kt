package com.pasco.pascocustomer.customer.activity.notificaion.delete

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
import javax.inject.Inject
@ExperimentalCoroutinesApi
@HiltViewModel
class DeleteNotificationViewModel @Inject constructor(
    application: Application,
    private val deleteNotificationRepository: CommonRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mDeleteNotificationResponse = MutableLiveData<Event<DeleteNotificationResponse>>()
    var context: Context? = null

    fun getDeleteNotifications(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        body: NotificationBody
    ) =
        viewModelScope.launch {
            getDeleteList(progressDialog,activity,body)
        }

    private suspend fun getDeleteList(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        body: NotificationBody
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        deleteNotificationRepository.getDeleteNotification(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<DeleteNotificationResponse>() {
                override fun onNext(value: DeleteNotificationResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mDeleteNotificationResponse.value = Event(value)
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