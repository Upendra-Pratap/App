package com.pasco.pascocustomer.Driver.StartRiding.ViewModel
import io.reactivex.Observable
import com.pasco.pascocustomer.services.ApiServices
import javax.inject.Inject

class GetRouteUpdateRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun getDriverStatusRepo(

    ): Observable<GetRouteUpdateResponse> {
        return apiService.getDriverStatus(
        )
    }
}