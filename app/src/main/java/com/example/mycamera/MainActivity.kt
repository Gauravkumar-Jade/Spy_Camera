package com.example.mycamera

import android.Manifest.permission.*
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
    private val MULTIPLE_PERMISSIONS = 10
    private val permission = arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA, ACCESS_FINE_LOCATION, RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onStart() {
        super.onStart()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                if(ContextCompat.checkSelfPermission(this@MainActivity, ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                    turnOnGPS()
                }
            }
        }else{
            turnOnGPS()
        }
    }

    override fun onResume() {
        super.onResume()
        if(isMyServiceRunning(MyService::class.java)){
            binding.btStart.text = "STOP"

        }else{
            binding.btStart.text = "START"

        }
    }

    private fun checkPermission():Boolean {

        var result:Int
        val listPermissionsNeeded = ArrayList<String>()

        for (p in permission) {
            result = ContextCompat.checkSelfPermission(this@MainActivity, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                MULTIPLE_PERMISSIONS)
            return false
        }
        return true
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            MULTIPLE_PERMISSIONS -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MainActivity, "No Permission Granted", Toast.LENGTH_SHORT).show()
                    AlertDialog.Builder(this)
                        .setTitle("Alert!!")
                        .setMessage("Please Restart To App Allow All Permission")
                        .setCancelable(false)
                        .setPositiveButton("OK"
                        ) { p0, p1 ->
                            p0?.dismiss()
                            finish()
                        }.show()
                }
                return
            }
        }
    }

}