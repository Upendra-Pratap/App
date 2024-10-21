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
class GetRouteUpdateViewModel@Inject constructor(
    application: Application,
    private val getRouteUpdateRepository: GetRouteUpdateRepository
) : AndroidViewModel(application)  {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mGetRouteUpdate = MutableLiveData<Event<GetRouteUpdateResponse>>()
    var context: Context? = null

    fun getDriverStatusData(
        progressDialog: CustomProgressDialog,
        activity: Activity
    ) =
        viewModelScope.launch {
            getDriverStatusDataa( progressDialog,
                activity)
        }
    suspend fun getDriverStatusDataa(
        progressDialog: CustomProgressDialog,
        activity: Activity
    )

    {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        getRouteUpdateRepository.getDriverStatusRepo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<GetRouteUpdateResponse>() {
                override fun onNext(value: GetRouteUpdateResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mGetRouteUpdate.value = Event(value)
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