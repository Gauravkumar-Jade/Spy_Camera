package com.example.mycamera

import android.app.Service
import android.content.Intent
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class CameraService : Service() {

    //a surface holder
    private lateinit var sHolder: SurfaceHolder

    //a variable to control the camera
    private var mCamera: Camera? = null

    //the camera parameters
    private var parameters: Camera.Parameters? = null


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        mCamera = getAvailableFrontCamera()
        if (mCamera == null){
            mCamera = Camera.open()    //Take rear facing camera only if no front camera available
        }
        val sv = SurfaceView(applicationContext)
        val surfaceTexture = SurfaceTexture(10)

        try {
            mCamera?.setPreviewTexture(surfaceTexture)
            parameters = mCamera?.parameters

            //set camera parameters
            mCamera?.parameters = parameters

            mCamera?.startPreview()
            mCamera?.takePicture(null, null, object : Camera.PictureCallback{
                override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
                    onSaveData(data, camera)
                }

            })

        } catch (e: IOException) {
            Log.d("CAMERA_1", e.toString())
        }

        //Get a surface
        sHolder = sv.holder
        //tells Android that this surface will have its data constantly replaced
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private fun onSaveData(data: ByteArray?, camera: Camera?) {

        try {
           // val mediaStorageDir:File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

            val dir = getExternalFilesDir("SpyCam")

            val file = File(dir, "${System.currentTimeMillis()}.jpg")
            val outStream = FileOutputStream(file)

            outStream.write(data!!)
            outStream.flush()
            outStream.close()
            mCamera?.release()

            Log.d("PATH_", file.absolutePath)

            Toast.makeText(this, "picture clicked", Toast.LENGTH_LONG).show()

            stopSelf()

        } catch (e: FileNotFoundException) {
            Log.d("CAMERA_2", e.message.toString())
        } catch (e: IOException) {
            Log.d("CAMERA_3", e.message.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        Toast.makeText(this, "Camera Service Stopped", Toast.LENGTH_SHORT).show()
    }

    private fun getAvailableFrontCamera(): Camera? {
        var cameraCount = 0
        var cam: Camera? = null
        val cameraInfo = CameraInfo()
        cameraCount = Camera.getNumberOfCameras()
        for (camIdx in 0 until cameraCount) {
            Camera.getCameraInfo(camIdx, cameraInfo)
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx)
                } catch (e: RuntimeException) {
                    Log.e("CAMERA_4", "Camera failed to open: " + e.localizedMessage)
                }
            }
        }
        return cam
    }
}

