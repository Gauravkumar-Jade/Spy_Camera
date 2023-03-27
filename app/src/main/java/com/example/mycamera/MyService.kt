package com.example.mycamera

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.resources.Compatibility.Api18Impl.setAutoCancel
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

class MyService : Service() {
    private val CHANNEL_ID = "ForegroundService"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val intent = Intent(this, MyReceiver::class.java)
        intent.putExtra("message", "Take Picture")

        val pendingIntent = PendingIntent.getBroadcast(this,1,intent,0)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Service Started")
            .setSmallIcon(R.drawable.ic_notification)
            .addAction(R.drawable.ic_notification, "Take", pendingIntent)
            .build()
        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
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

}