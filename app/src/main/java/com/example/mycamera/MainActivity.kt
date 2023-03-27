package com.example.mycamera

import android.content.Intent
import android.graphics.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mycamera.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    fun onClick(view: View) {

        when(view){
            binding.btStart ->{
                startService(Intent(this, MyService::class.java))
            }
            binding.btStop -> {
                stopService(Intent(this, MyService::class.java))
            }
        }
    }
}