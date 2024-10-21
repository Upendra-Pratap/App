package com.pasco.pascocustomer.Driver.CouponDetails.CouponViewModel

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
class CouponViewModel@Inject constructor(
    application: Application,
    private val couponRepository: CouponRepository
) : AndroidViewModel(application)  {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mGetCouponList = MutableLiveData<Event<CouponResponse>>()
    var context: Context? = null

    fun getCouponListData(
        progressDialog: CustomProgressDialog,
        activity: Activity

    ) =
        viewModelScope.launch {
            getCouponListDatas( progressDialog,
                activity)
        }
    suspend fun getCouponListDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity
    )

    {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        couponRepository.getCouponRepo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CouponResponse>() {
                override fun onNext(value: CouponResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mGetCouponList.value = Event(value)
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