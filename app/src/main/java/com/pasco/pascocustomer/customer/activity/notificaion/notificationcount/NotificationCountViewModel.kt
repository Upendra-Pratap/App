package com.pasco.pascocustomer.customer.activity.notificaion.notificationcount

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
class NotificationCountViewModel @Inject constructor(
    application: Application,
    private val notificationCountRepository: CommonRepository
) : AndroidViewModel(application)  {

    val errorResponse = MutableLiveData<Throwable>()
    val mNotiCountResponse= MutableLiveData<Event<NotificationCountResponse>>()
    var context: Context? = null

    fun getCountNoti() = viewModelScope.launch {
            getCount()
        }
    suspend fun getCount()
    {
        notificationCountRepository.getCountNotifications()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<NotificationCountResponse>() {
                override fun onNext(value: NotificationCountResponse) {
                    mNotiCountResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    errorResponse.value = e
                }

                override fun onComplete() {
                }
            })
    }
}