package com.example.apartmentsalesproject.view

import android.Manifest
import android.app.DatePickerDialog.OnDateSetListener
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apartmentsalesproject.R
import com.example.apartmentsalesproject.databinding.ActivityMainBinding
import com.example.apartmentsalesproject.model.data.SaleItem
import com.example.apartmentsalesproject.viewmodel.MainViewModel
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource


//TODO 전체적인 코드 정리 필요
class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = MainActivity::class.java.simpleName

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    //위치 권한 관련
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    //지도 관련
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationSource: FusedLocationSource

    private var legalCode = 0
    private var yearMonth = ""

    var d =
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            yearMonth = "$year" + "${getMonth(monthOfYear)}"
            //월 2자리로
            Log.d(TAG, "년월 = $yearMonth")
        }

    private fun getMonth(month: Int): String {
        return if (month in 1..9)
            "0$month"
        else
            "$month"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnDatePicker.setOnClickListener {
            val pd = MyDatePickerDialog()
            pd.setListener(d)
            pd.show(supportFragmentManager, "MyPicker")

        }
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initMapView()
        }

        viewModel.apartmentSalesData.observe(this) {
            Log.d(
                TAG,
                "size = ${it?.body?.items?.item?.size} and ====== ${it?.body?.items?.item.toString()}"
            )
            initSalesRecyclerView(it?.body?.items?.item!!)
        }

        viewModel.codeIdLiveData.observe(this) {
            Log.d(TAG, "ReverseGeocoding =  $it, legalCode = $legalCode")
            if (legalCode != it?.toInt()) {
                legalCode = it?.toInt()!!
                viewModel.getApartSales(it, yearMonth)
            }
        }

    }

    // hasPermission()에서는 위치 권한이 있을 경우 true를, 없을 경우 false를 반환한다.
    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun initMapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
        fusedLocationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun initSalesRecyclerView(items: List<SaleItem>) {
        val adapter = SalesRecyclerAdapter(items)
        binding.recyclerSales.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        naverMap.apply {
            locationSource = fusedLocationSource
            uiSettings.isLocationButtonEnabled = true
            locationTrackingMode = LocationTrackingMode.Follow
            val coord =
                cameraPosition.target.longitude.toString() + ", " + cameraPosition.target.latitude
            //TODO 현위치는 나중에 푸는걸로
            //viewModel.requestReverseGeocoding(coord)
        }

        //지도 카메라가 변경 되고 있을 때
        naverMap.addOnCameraChangeListener { reason, animated ->
            Log.d(TAG, "카메라 변경 - reason: $reason, animated: $animated")
        }

        //지도 카메라 변경이 끝났을 때
        naverMap.addOnCameraIdleListener {
            val coord =
                naverMap.cameraPosition.target.longitude.toString() + ", " + naverMap.cameraPosition.target.latitude
            viewModel.requestReverseGeocoding(coord)
        }

        //사용자의 위치가 변경될 때
        naverMap.addOnLocationChangeListener {
            Log.d(TAG, "현재 위치 변경")
        }

    }


}