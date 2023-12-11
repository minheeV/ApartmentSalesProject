package com.example.apartmentsalesproject.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.apartmentsalesproject.viewmodel.MainViewModel
import com.example.apartmentsalesproject.R
import com.example.apartmentsalesproject.databinding.ActivityMainBinding
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource

//TODO 전체적인 코드 정리 필요
class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private val LOCATION_PERMISSION_REQUEST_CODE = 5000

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initMapView()
        }

        viewModel.apartmentSalesData.observe(this) {
            Log.d(TAG, it.toString())
        }

        viewModel.codeIdLiveData.observe(this) {
            viewModel.getApartSales(it)
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

    //    REASON_DEVELOPER: 개발자가 API를 호출해 카메라가 움직였음을 나타냅니다. 기본값입니다.
//    REASON_GESTURE: 사용자의 제스처로 인해 카메라가 움직였음을 나타냅니다.
//    REASON_CONTROL: 사용자의 버튼 선택으로 인해 카메라가 움직였음을 나타냅니다.
//    REASON_LOCATION: 위치 트래킹 기능으로 인해 카메라가 움직였음을 나타냅니다.
    private fun initMapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        naverMap.apply {
            uiSettings.isLocationButtonEnabled = true
            locationTrackingMode = LocationTrackingMode.Follow
            val coord =
                cameraPosition.target.longitude.toString() + ", " + cameraPosition.target.latitude
            viewModel.requestReverseGeocoding(coord)
        }

        naverMap.addOnCameraChangeListener { reason, animated ->
            Log.d(TAG, "카메라 변경 - reason: $reason, animated: $animated")
        }

        naverMap.addOnCameraIdleListener {
            Toast.makeText(applicationContext, "카메라 움직임 종료", Toast.LENGTH_SHORT).show()
            val coord =
                naverMap.cameraPosition.target.longitude.toString() + ", " + naverMap.cameraPosition.target.latitude
            viewModel.requestReverseGeocoding(coord)
        }
    }
}