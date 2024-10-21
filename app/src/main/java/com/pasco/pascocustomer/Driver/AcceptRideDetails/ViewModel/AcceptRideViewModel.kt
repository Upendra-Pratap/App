package com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel

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
class AcceptRideViewModel @Inject constructor(
    application: Application,
    private val acceptRideRepository: AcceptRideRepository
) : AndroidViewModel(application) {
    val errorResponse = MutableLiveData<Throwable>()
    val mAcceptRideResponse= MutableLiveData<Event<AcceptRideResponse>>()
    var context: Context? = null

    fun getAcceptRideData(
        activity: Activity,
        id: String
    ) =
        viewModelScope.launch {
            getAcceptRideDatas(activity, id)
        }

    suspend fun getAcceptRideDatas(
        activity: Activity,
        id: String
    ) {
        acceptRideRepository.putMarkRepository(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<AcceptRideResponse>() {
                override fun onNext(value: AcceptRideResponse) {
                    mAcceptRideResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    errorResponse.value = e
                }

                override fun onComplete() {
                }
            })
    }
}