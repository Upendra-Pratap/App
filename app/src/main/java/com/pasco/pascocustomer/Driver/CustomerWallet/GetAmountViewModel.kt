package com.pasco.pascocustomer.Driver.Customer.Fragment.CustomerWallet

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet.GetAmountRepository
import com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet.GetAmountResponse
import com.pasco.pascocustomer.R
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
class GetAmountViewModel @Inject constructor(
    application: Application,
    private val getAmountRepository: GetAmountRepository
) : AndroidViewModel(application)  {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mGetAmounttt = MutableLiveData<Event<GetAmountResponse>>()
    var context: Context? = null

    fun getAmountData(
        progressDialog: CustomProgressDialog,
        activity: Activity

    ) =
        viewModelScope.launch {
            getAmountDatas( progressDialog,
                activity)
        }
    suspend fun getAmountDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity
    )

    {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        getAmountRepository.getAmountRepository()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<GetAmountResponse>() {
                override fun onNext(value: GetAmountResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mGetAmounttt.value = Event(value)
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