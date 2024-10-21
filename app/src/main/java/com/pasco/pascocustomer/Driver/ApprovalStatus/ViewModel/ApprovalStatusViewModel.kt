package com.pasco.pascocustomer.Driver.ApprovalStatus.ViewModel

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
class ApprovalStatusViewModel@Inject constructor(
    application: Application,
    private val   approvalStatusRepository: ApprovalStatusRepository
) : AndroidViewModel(application) {
    val errorResponse = MutableLiveData<Throwable>()
    val mCheckApproveResponse= MutableLiveData<Event<ApprovalStatusResponse>>()
    var context: Context? = null

    fun getCheckApproveBooking(
        activity: Activity
    ) =
        viewModelScope.launch {
            getCheckApproveBookingg(activity)
        }

    suspend fun getCheckApproveBookingg(
        activity: Activity
    ) {
        approvalStatusRepository.getcheckStatusRepository()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<ApprovalStatusResponse>() {
                override fun onNext(value: ApprovalStatusResponse) {
                    mCheckApproveResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    errorResponse.value = e
                }

                override fun onComplete() {
                }
            })
    }
}