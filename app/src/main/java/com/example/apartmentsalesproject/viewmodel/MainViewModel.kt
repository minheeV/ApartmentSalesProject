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

class MainViewModel : ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    private var retrofitInstance: ApartmentSalesService =
        RetrofitInstance.getInstance().create(ApartmentSalesService::class.java)
    private var job: Job? = null

    private val _apartmentSalesData = MutableLiveData<ApartSales?>()
    val apartmentSalesData: LiveData<ApartSales?>
        get() = _apartmentSalesData

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception: ${throwable.localizedMessage}")
    }

    private fun onError(message: String) {
        Log.d(TAG, message)
    }


    fun getApartSales() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = retrofitInstance.getApartmentSales(
                    BuildConfig.api_key,
                    "11110",
                    "202311",
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful)
                        _apartmentSalesData.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}