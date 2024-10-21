package com.pasco.pascocustomer.Driver.StartRiding.ViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
class CompleteRideViewModel@Inject constructor(
    application: Application,
    private val  completeRideRepository: CompleteRideRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mCRideResponse= MutableLiveData<Event<CompleteRideResponse>>()
    var context: Context? = null

    fun getCompletedRideData(
        activity: Activity,
        id: String
    ) =
        viewModelScope.launch {
            getStartTripDatas(activity,id)
        }

    suspend fun getStartTripDatas(
        activity: Activity,
        id: String
    ) {
        completeRideRepository.getCompleteDriverRide(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CompleteRideResponse>() {
                override fun onNext(value: CompleteRideResponse) {
                    mCRideResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    errorResponse.value = e
                }

                override fun onComplete() {
                }
            })
    }
}