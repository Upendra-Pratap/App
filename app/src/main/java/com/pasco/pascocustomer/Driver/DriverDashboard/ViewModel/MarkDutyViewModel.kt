package com.example.transportapp.DriverApp.MarkDuty

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.DriverDashboard.ViewModel.MarkDutyBody
import com.pasco.pascocustomer.R
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import com.pasco.pascocustomer.utils.Event
import retrofit2.http.Body
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MarkDutyViewModel  @Inject constructor(
    application: Application,
    private val markDutyRepository: MarkDutyRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mmarkDutyResponse= MutableLiveData<Event<MarkDutyResponse>>()
    var context: Context? = null

    fun putMarkOn(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        @Body body: MarkDutyBody
    ) =
        viewModelScope.launch {
            userBookingHList(progressDialog,activity,body)
        }

    suspend fun userBookingHList(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        @Body body: MarkDutyBody
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        markDutyRepository.putMarkRepository(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<MarkDutyResponse>() {
                override fun onNext(value: MarkDutyResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mmarkDutyResponse.value = Event(value)
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