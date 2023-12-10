package com.example.apartmentsalesproject.model.repository

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Retrofit


object RetrofitInstance {
    private const val BASE_URL =
        "http://openapi.molit.go.kr:8081/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/"

    private val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()

    private val retrofitApartSales =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(TikXmlConverterFactory.create(parser))
            .build()

    fun getInstance(): Retrofit {
        return retrofitApartSales
    }
}
