package com.example.apartmentsalesproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apartmentsalesproject.BuildConfig
import com.example.apartmentsalesproject.model.data.ApartSales
import com.example.apartmentsalesproject.model.repository.ApartmentSalesService
import com.example.apartmentsalesproject.model.repository.RetrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainViewModel : ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    private var retrofitInstance: ApartmentSalesService =
        RetrofitInstance.getInstance().create(ApartmentSalesService::class.java)

    private var job: Job? = null
    private var job1: Job? = null

    private val _apartmentSalesData = MutableLiveData<ApartSales?>()
    val apartmentSalesData: LiveData<ApartSales?>
        get() = _apartmentSalesData

    private val _codeIdLiveData = MutableLiveData<String?>()
    val codeIdLiveData: LiveData<String?>
        get() = _codeIdLiveData

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, "Exception: ${throwable.localizedMessage}")
    }

    fun getApartSales(codeId: String?, yearMonth: String?) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                Log.d(TAG, "getApartSales() codeId = $codeId, yearMonth = $yearMonth")
                val response = retrofitInstance.getApartmentSales(
                    BuildConfig.api_key,
                    codeId.toString(),
                    yearMonth.toString(),
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful){
                        Log.d(TAG, "getApartSales : ${response.body()}")
                        _apartmentSalesData.value = response.body()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun requestReverseGeocoding(coord: String) {
        job1 = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                Log.d(TAG, "requestReverseGeocoding()")
                var query = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?\n" +
                        "request=coordsToaddr&coords=" + coord + "&sourcecrs=epsg:4326&output=json&orders=legalcode"

                val url = URL(query)
                val conn = url.openConnection() as HttpURLConnection
                conn.apply {
                    connectTimeout = 5000
                    readTimeout = 5000
                    requestMethod = "GET"
                    setRequestProperty(
                        "X-NCP-APIGW-API-KEY-ID",
                        BuildConfig.naver_client_id
                    )
                    setRequestProperty(
                        "X-NCP-APIGW-API-KEY",
                        BuildConfig.naver_client_secret
                    )
                    doInput = true
                }

                val responseCode = conn.responseCode

                val bufferedReader: BufferedReader =
                    if (responseCode == 200) BufferedReader(InputStreamReader(conn.inputStream))
                    else BufferedReader(InputStreamReader(conn.errorStream))

                val line = bufferedReader.readLine()
                val jsonInfo = JSONObject(line)
                val result = jsonInfo.getJSONArray("results").getJSONObject(0).getJSONObject("code")
                val codeId = result.getString("id").substring(0 until 5)
                Log.d(TAG, "법정동 코드 확인 : $codeId")
//                withContext(Dispatchers.Main) {
//                    _codeIdLiveData.value = codeId
//                }
                _codeIdLiveData.postValue(codeId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}