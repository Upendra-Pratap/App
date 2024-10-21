package com.pasco.pascocustomer.activity.Driver.AddVehicle.VehicleType

import VehicleTypeResponse
import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
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
class VehicleTypeViewModel@Inject constructor(
    application: Application,
    private val vehicleTypeRepository: CommonRepository
) : AndroidViewModel(application)  {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mVehicleTypeResponse= MutableLiveData<Event<VehicleTypeResponse>>()
    var context: Context? = null

    fun getVehicleTypeData(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        shipment_type: String

    ) =
        viewModelScope.launch {
            getVehicleTypeDatas( progressDialog,
                activity,shipment_type)
        }
    suspend fun getVehicleTypeDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity, shipment_type: String
    )

    {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        vehicleTypeRepository.getVehicleType(shipment_type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<VehicleTypeResponse>() {
                override fun onNext(value: VehicleTypeResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mVehicleTypeResponse.value = Event(value)
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