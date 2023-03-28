package com.example.mycamera

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.FileOutputStream


class MyService : Service(), LocationListener {
    private val CHANNEL_ID = "ForegroundService"
    private var locationManager: LocationManager? = null
    private var LOCATION_REFRESH_TIME = 5000
    private var LOCATION_REFRESH_DISTANCE = 0

    private var mRecorder: MediaRecorder? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val intent = Intent(this, MyReceiver::class.java)
        intent.putExtra("message", 0)
        val pendingIntent = PendingIntent.getBroadcast(this,1,intent,0)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Service Started")
            .setSmallIcon(R.drawable.ic_notification)
            .addAction(R.drawable.ic_notification, "STOP", pendingIntent)
            .build()
        startForeground(1, notification)

        onLocationUpdate()
        onStartAudioRecording()

        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun onStartAudioRecording() {
        mRecorder = MediaRecorder()

        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        val dir = getExternalFilesDir("SpyAudio")

        val file = File(dir, "${System.currentTimeMillis()}.3gp").absolutePath
        mRecorder?.setOutputFile(file)
        try {
            mRecorder?.prepare()
            mRecorder?.start()
        }catch (e: Exception){
            Log.e("RECORDER_1", e.message.toString())
        }
    }

    @SuppressLint("MissingPermission")
    private fun onLocationUpdate() {
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            LOCATION_REFRESH_TIME.toLong(),
            LOCATION_REFRESH_DISTANCE.toFloat(),
            this
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager?.removeUpdates(this)
        locationManager = null
        mRecorder?.stop()
        mRecorder?.release()
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    override fun onLocationChanged(location: Location) {
        val intent = Intent(this, MyReceiver::class.java)
        intent.putExtra("message", 1)
        val lat = location.latitude.toString()
        val lang = location.longitude.toString()
        Toast.makeText(this@MyService, "Location $lat\n $lang", Toast.LENGTH_SHORT).show()
        sendBroadcast(intent)
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
}
