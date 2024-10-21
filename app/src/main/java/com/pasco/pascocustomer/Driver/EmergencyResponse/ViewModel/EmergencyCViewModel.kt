package com.pasco.pascocustomer.Driver.EmergencyResponse.ViewModel

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
class EmergencyCViewModel@Inject constructor(
    application: Application,
    private val emergencyCReposiotory: EmergencyCReposiotory
) : AndroidViewModel(application)  {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mGetEmergencyList = MutableLiveData<Event<EmergencyCResponse>>()
    var context: Context? = null

    fun getEmeergencyListData(
        progressDialog: CustomProgressDialog,
        activity: Activity

    ) =
        viewModelScope.launch {
            getCouponListDatas( progressDialog,
                activity)
        }
    suspend fun getCouponListDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity
    )

    {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        emergencyCReposiotory.getEmregencyRepo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<EmergencyCResponse>() {
                override fun onNext(value: EmergencyCResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mGetEmergencyList.value = Event(value)
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