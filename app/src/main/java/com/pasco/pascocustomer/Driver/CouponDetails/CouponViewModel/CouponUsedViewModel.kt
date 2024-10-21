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
class CouponUsedViewModel  @Inject constructor(
    application: Application,
    private val couponUsedRepository: CouponUsedRepository
) : AndroidViewModel(application)  {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mCouponUsedResponse= MutableLiveData<Event<CouponUsedResponse>>()
    var context: Context? = null

    fun getLogoutServices(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        coupon_used: String

    ) =
        viewModelScope.launch {
            getUserLogout( progressDialog,
                activity,coupon_used)
        }
    suspend fun getUserLogout(
        progressDialog: CustomProgressDialog,
        activity: Activity, coupon_used: String
    )

    {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        couponUsedRepository.getUserCouponUsed(coupon_used)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CouponUsedResponse>() {
                override fun onNext(value: CouponUsedResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mCouponUsedResponse.value = Event(value)
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