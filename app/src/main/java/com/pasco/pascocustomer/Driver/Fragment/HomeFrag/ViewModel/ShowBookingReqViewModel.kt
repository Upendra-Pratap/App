package com.pasco.pascocustomer.Driver.Fragment.HomeFrag.ViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import com.pasco.pascocustomer.utils.Event
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ShowBookingReqViewModel@Inject constructor(
    application: Application,
    private val showBookingReqRepository: ShowBookingReqRepository
) : AndroidViewModel(application)  {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mShowBookingReq = MutableLiveData<Event<ShowBookingReqResponse>>()
    var context: Context? = null

    fun getShowBookingRequestsData(
        activity: Activity) =
        viewModelScope.launch {
            getShowBookingRequests(
                activity)
        }
    suspend fun getShowBookingRequests(
        activity: Activity
    )

    {
        showBookingReqRepository.getShowBookingRequests()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<ShowBookingReqResponse>() {
                override fun onNext(value: ShowBookingReqResponse) {
                    mShowBookingReq.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    progressIndicator.value = false
                    errorResponse.value = e
                }

                override fun onComplete() {
                    progressIndicator.value = false
                }
            })
    }
}