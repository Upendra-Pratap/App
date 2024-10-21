package com.pasco.pascocustomer.commonpage.login.signup.model

import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import javax.inject.Inject

class DriverRepository  @Inject constructor(private val apiService: ApiServices) {
     fun getDriverSign(courseBody: DriverBody): Observable<DriverResponse> {
        return apiService.driverRegister(courseBody)
    }
}