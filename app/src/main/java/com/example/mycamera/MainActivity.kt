package com.example.mycamera

import android.Manifest.permission.*
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mycamera.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val permission = arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA, READ_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission(permission)

    }

    override fun onResume() {
        super.onResume()
        if(isMyServiceRunning(MyService::class.java)){
            binding.btStart.text = "STOP"

        }else{
            binding.btStart.text = "START"

        }
    }

    private fun checkPermission(permission: Array<String>) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this@MainActivity, WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@MainActivity, permission, 100)
            }

            if(ContextCompat.checkSelfPermission(this@MainActivity, CAMERA)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@MainActivity, permission, 101)
            }

            if(ContextCompat.checkSelfPermission(this@MainActivity, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@MainActivity, permission, 102)
            }

            if(ContextCompat.checkSelfPermission(this@MainActivity, ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@MainActivity,permission,103)
                turnOnGPS()
            }else{
                turnOnGPS()
            }

            if(ContextCompat.checkSelfPermission(this@MainActivity, RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@MainActivity,permission,104)
            }
        }
    }

    fun onClick(view: View) {

        if(isMyServiceRunning(MyService::class.java)){
            binding.btStart.text = "START"
            stopService(Intent(this, MyService::class.java))
        }else{
            binding.btStart.text = "STOP"
            startService(Intent(this, MyService::class.java))
        }

    }


    private fun turnOnGPS() {
        if (!isLocationEnabled()) {
            Toast.makeText(this@MainActivity, "Please Enable GPS", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

}