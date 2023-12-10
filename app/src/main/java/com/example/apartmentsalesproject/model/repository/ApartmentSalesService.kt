package com.example.apartmentsalesproject.model.repository

import com.example.apartmentsalesproject.model.data.ApartSales
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApartmentSalesService {
    @GET("getRTMSDataSvcAptTrade")
    suspend fun getApartmentSales(
        @Query("ServiceKey") serviceKey: String,
        @Query("LAWD_CD") lawdCd: String,
        @Query("DEAL_YMD") dealYmd: String
    ): Response<ApartSales>
}
