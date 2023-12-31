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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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

    private var legalCode = 0 // 법정동코드
    var yearMonth = "202401" // 년월 TODO 현재 년, 월로 디폴트
    var coord = ""

    var recyclerViewState = "년/월을 선택해주세요."

    private var dateSetListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        yearMonth = "$year" + getMonth(monthOfYear)
        binding.dateText = yearMonth
        viewModel.requestReverseGeocoding(coord)
        Log.d(TAG, "년월 = $yearMonth")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            lifecycleOwner = this@MainActivity
            btnDatePicker.setOnClickListener {
                val pd = MyDatePickerDialog()
                pd.setListener(dateSetListener)
                pd.show(supportFragmentManager, "MyPicker")
            }

            dataStateText = recyclerViewState
        }

        if (!hasPermission())
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        else
            initMapView()

        setLiveDataObserve()
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
        Log.d(TAG, "init recyclerview")
        val recyclerAdapter = SalesRecyclerAdapter(items)
        binding.recyclerSales.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerAdapter
        }
    }

    private fun setLiveDataObserve() {
        viewModel.apartmentSalesData.observe(this) {
            Log.d(
                TAG,
                "apartmentSalesData observe size = ${it?.body?.items?.item?.size}, item ${it?.body?.items?.item.toString()}"
            )
            initSalesRecyclerView(it?.body?.items?.item!!)
            recyclerViewState = "    년/월   |   거래금액(만원)   |   전용면적"
            binding.dataStateText = recyclerViewState
        }

        //TODO 년월 바꿨을 때 법정동 같을 때 처리
        viewModel.codeIdLiveData.observe(this) {
            Log.d(TAG, "codeIdLiveData observe =  $it, legalCode = $legalCode")
            viewModel.getApartSales(it, yearMonth)
//            if (legalCode != it?.toInt()) {
//                legalCode = it?.toInt()!!
//            }
        }
    }

    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        naverMap.apply {
            locationSource = fusedLocationSource
            uiSettings.isLocationButtonEnabled = true
            locationTrackingMode = LocationTrackingMode.Follow
        }

        //지도 카메라가 변경 되고 있을 때
        naverMap.addOnCameraChangeListener { reason, animated ->
            Log.d(TAG, " 지도 카메라가 변경 되고 있을 때 카메라 변경 - reason: $reason, animated: $animated")
            coord =
                naverMap.cameraPosition.target.longitude.toString() + ", " + naverMap.cameraPosition.target.latitude
        }

        //지도 카메라 변경이 끝났을 때
        naverMap.addOnCameraIdleListener {
            Log.d(TAG, "지도 카메라 변경이 끝났을 때")
            coord =
                naverMap.cameraPosition.target.longitude.toString() + ", " + naverMap.cameraPosition.target.latitude
        }

        //사용자의 위치가 변경될 때
        naverMap.addOnLocationChangeListener {
            Log.d(TAG, "사용자의 위치가 변경될 때")
            coord =
                naverMap.cameraPosition.target.longitude.toString() + ", " + naverMap.cameraPosition.target.latitude
        }
    }


    //1~9월 : 01~09월 변환
    private fun getMonth(month: Int): String {
        return if (month in 1..9)
            "0$month"
        else
            "$month"
    }


}