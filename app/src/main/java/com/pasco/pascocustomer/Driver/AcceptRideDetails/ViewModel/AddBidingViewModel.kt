package com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel

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
class AddBidingViewModel@Inject constructor(
    application: Application,
    private val addBidingRepository: AddBidingRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mAddBiddibgResponse= MutableLiveData<Event<AddBidingResponse>>()
    var context: Context? = null

    fun addBidingData(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        id: String,
        body: AddBiddingBody
    ) =
        viewModelScope.launch {
            addBidingDatas(progressDialog,activity, id,body)
        }

    suspend fun addBidingDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        id: String,
        body: AddBiddingBody
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        addBidingRepository.addBidingRepository(id,body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<AddBidingResponse>() {
                override fun onNext(value: AddBidingResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mAddBiddibgResponse.value = Event(value)
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