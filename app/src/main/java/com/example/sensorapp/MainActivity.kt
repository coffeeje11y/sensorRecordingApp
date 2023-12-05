package com.example.sensorapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startRecordingButton: Button = findViewById(R.id.startRecordingButton)
        val stopRecordingButton: Button = findViewById(R.id.stopRecordingButton)
        val nameEditText: EditText = findViewById(R.id.nameEditText)


        startRecordingButton.setOnClickListener {
            val userName = nameEditText.text.toString()
            Log.d("com.example.sensorapp.MainActivity", "Button clicked! User name: $userName")

            val data = Data.Builder().putString("userName", userName).build()

            val workRequest = OneTimeWorkRequestBuilder<SensorService>()
                .setInputData(data)
                .build()

            WorkManager.getInstance(this).enqueue(workRequest)
        }

        stopRecordingButton.setOnClickListener {
            //ログを書き込むのをやめる
            Log.d("com.example.sensorapp.MainActivity", "Stop Button clicked!")
        }

    }
}
