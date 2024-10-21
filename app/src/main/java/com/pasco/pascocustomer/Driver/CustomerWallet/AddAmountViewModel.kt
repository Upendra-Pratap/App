package com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
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
class AddAmountViewModel @Inject constructor(
    application: Application,
    private val addAmountRepository: AddAmountRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mAddAmountResponse = MutableLiveData<Event<AddAmountResponse>>()
    var context: Context? = null

    fun getAddAmountData(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        amount: String

    ) =
        viewModelScope.launch {
            getAddAmountDatas(progressDialog,activity,amount)
        }

    private suspend fun getAddAmountDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        amount: String
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        addAmountRepository.addUserWalletRepo(amount)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<AddAmountResponse>() {
                override fun onNext(value: AddAmountResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mAddAmountResponse.value = Event(value)
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