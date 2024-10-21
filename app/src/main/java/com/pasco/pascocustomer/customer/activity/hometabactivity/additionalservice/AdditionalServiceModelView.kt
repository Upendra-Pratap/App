package com.pasco.pascocustomer.customer.activity.hometabactivity.additionalservice

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.adddetailsmodel.ServicesResponse
import com.pasco.pascocustomer.repository.CommonRepository
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
class AdditionalServiceModelView @Inject constructor(
    application: Application,
    private val servicesRepository: CommonRepository
) : AndroidViewModel(application)  {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mGetServices = MutableLiveData<Event<AdditionalServiceResponse>>()
    var context: Context? = null

    fun getServicesData(
        progressDialog: CustomProgressDialog,
        activity: Activity

    ) =
        viewModelScope.launch {
            getServicesDatas( progressDialog,
                activity)
        }
    suspend fun getServicesDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity
    )

    {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        servicesRepository.getAdditionalService()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<AdditionalServiceResponse>() {
                override fun onNext(value: AdditionalServiceResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mGetServices.value = Event(value)
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