package com.pasco.pascocustomer.Driver.StartRiding.ViewModel

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
class StartTripViewModel@Inject constructor(
    application: Application,
    private val startTripRepository: StartTripRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mStartTripResponse= MutableLiveData<Event<StartTripResponse>>()
    var context: Context? = null

    fun getStartTripData(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        id: String,
        driver_status:String
    ) =
        viewModelScope.launch {
            getStartTripDatas(progressDialog,activity, id,driver_status)
        }

    suspend fun getStartTripDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        id: String,
        driver_status: String,
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        startTripRepository.addStartTripRepository(id,driver_status)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<StartTripResponse>() {
                override fun onNext(value: StartTripResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mStartTripResponse.value = Event(value)
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