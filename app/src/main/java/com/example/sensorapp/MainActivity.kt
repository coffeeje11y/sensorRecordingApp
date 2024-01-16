package com.example.sensorapp

// MainActivity.kt
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startRecordingButton: Button = findViewById(R.id.startRecordingButton)
        val stopRecordingButton: Button = findViewById(R.id.stopRecordingButton)
        val nameEditText: EditText = findViewById(R.id.nameEditText)

        val sensorService = Intent(this, SensorService::class.java)

        startRecordingButton.setOnClickListener {
            val userName = nameEditText.text.toString()
            Log.d("com.example.sensorapp.MainActivity", "Button clicked! User name: $userName")

            sensorService.apply {
                putExtra("userName", userName)
            }
            startForegroundService(sensorService)
        }


        stopRecordingButton.setOnClickListener {
            //書き込むのをやめる
            Log.d("com.example.sensorapp.MainActivity", "Stop Button clicked!")
            stopService(sensorService)
        }
    }
}

