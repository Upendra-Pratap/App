package com.pasco.pascocustomer.Driver.Fragment.DriverAllBiddsDetail.ViewModel

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
class GetDriverBidDetailsDataViewModel@Inject constructor(
    application: Application,
    private val getDriverBidDetailsDataRepository: GetDriverBidDetailsDataRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mgetDBiddDataResponse= MutableLiveData<Event<GetDriverBidDetailsDataResponse>>()
    var context: Context? = null

    fun getDriverBidingData(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        bookingId: String
    ) =
        viewModelScope.launch {
            driverBidingDatas(progressDialog,activity,bookingId)
        }

    suspend fun driverBidingDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        bookingId: String
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        getDriverBidDetailsDataRepository.getDriverBDataRepo(bookingId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<GetDriverBidDetailsDataResponse>() {
                override fun onNext(value: GetDriverBidDetailsDataResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mgetDBiddDataResponse.value = Event(value)
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