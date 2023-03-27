package com.example.mycamera

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class MyReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
       context.startService(Intent(context, CameraService::class.java))
    }

}