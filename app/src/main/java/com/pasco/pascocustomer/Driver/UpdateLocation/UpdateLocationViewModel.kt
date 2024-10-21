package com.pasco.pascocustomer.Driver.UpdateLocation

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
class UpdateLocationViewModel  @Inject constructor(
    application: Application,
    private val updateLocationRepository: UpdateLocationRepository
) : AndroidViewModel(application) {
    val errorResponse = MutableLiveData<Throwable>()
    val mUpdateLocationResponse= MutableLiveData<Event<UpdateLocationResponse>>()
    var context: Context? = null

    fun updateLocationDriver(
        activity: Activity,
        body: UpdationLocationBody
    ) =
        viewModelScope.launch {
            showBookingDetailsss(activity,body)
        }

    suspend fun showBookingDetailsss(
        activity: Activity,
        body: UpdationLocationBody
    ) {
        updateLocationRepository.updateLocationRepository(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<UpdateLocationResponse>() {
                override fun onNext(value: UpdateLocationResponse) {
                    mUpdateLocationResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    errorResponse.value = e
                }

                override fun onComplete() {
                }
            })
    }
}