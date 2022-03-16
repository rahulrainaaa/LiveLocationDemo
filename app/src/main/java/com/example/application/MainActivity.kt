package com.example.application

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.location = LocationData(0.0, 0.0)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (checkPermissionsGranted()) {
            mainViewModel.locationLiveData.observe(this) { binding.location = it }
        }
    }

    private fun checkPermissionsGranted(): Boolean {
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasCoarseLocationPermission || !hasFineLocationPermission) requestPermissions()
        else return true
        return false
    }

    private fun requestPermissions(
        permissions: Array<String> = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) {
        ActivityCompat.requestPermissions(this, permissions, 1111)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1111) {
            var appPermissionsEnabled = true
            for (index in permissions.indices) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    appPermissionsEnabled = false
                    Toast.makeText(this, permissions[index] + " permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            if (appPermissionsEnabled) startLocationUpdates()
        }
    }

}

@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext context: Context) : ViewModel() {
    val locationLiveData by lazy { LocationLiveData(context) }
}
