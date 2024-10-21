package com.pasco.pascocustomer.Driver.CouponDetails.CouponViewModel

import io.reactivex.Observable
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import javax.inject.Inject

class CouponUsedRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun getUserCouponUsed(
        coupon_used: String
    ): Observable<CouponUsedResponse>
    {
        return apiService.getCouponUsed(
            PascoApp.encryptedPrefs.bearerToken,
            coupon_used)
    }
}