package com.example.sensorapp

// MainActivity.kt
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit


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
                .setInitialDelay(15, TimeUnit.SECONDS) // アプリ起動後10秒後から実行
                .setInputData(data)
                .addTag("gyroscopeWorkTag")
                .addTag("linearAccelerationWorkTag")
                .addTag("MagneticWorkTag")
                .build()

            WorkManager.getInstance(this).enqueue(workRequest)
        }


        stopRecordingButton.setOnClickListener {
            //書き込むのをやめる
            Log.d("com.example.sensorapp.MainActivity", "Stop Button clicked!")
            WorkManager.getInstance(applicationContext).cancelAllWorkByTag("gyroscopeWorkTag")
            WorkManager.getInstance(applicationContext).cancelAllWorkByTag("linearAccelerationWorkTag")
            WorkManager.getInstance(applicationContext).cancelAllWorkByTag("MagneticWorkTag")
        }
    }
}

