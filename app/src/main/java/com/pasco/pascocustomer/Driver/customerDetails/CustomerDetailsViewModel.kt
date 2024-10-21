package com.pasco.pascocustomer.Driver.customerDetails

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.driverdetails.modelview.DriverDetailsResponse
import com.pasco.pascocustomer.repository.CommonRepository
import com.pasco.pascocustomer.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CustomerDetailsViewModel @Inject constructor(
    application: Application, private val customerdetailrepository: CommonRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mCustomerdResponse = MutableLiveData<Event<CustomerDetailsResponse>>()
    var context: Context? = null


    fun getDriverDetails(Id: String, activity: Activity, progressDialog: CustomProgressDialog) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        customerdetailrepository.customerDetails(Id).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CustomerDetailsResponse>() {
                override fun onNext(value: CustomerDetailsResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mCustomerdResponse.value = Event(value)
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