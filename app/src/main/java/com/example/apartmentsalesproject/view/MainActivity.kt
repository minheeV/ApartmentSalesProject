package com.example.apartmentsalesproject.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.apartmentsalesproject.viewmodel.MainViewModel
import com.example.apartmentsalesproject.R

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.getApartSales()

        viewModel.apartmentSalesData.observe(this, Observer {
            Log.d(TAG, it.toString())
        })

    }
}