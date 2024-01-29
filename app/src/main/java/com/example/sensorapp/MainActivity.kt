package com.example.sensorapp

// MainActivity.kt
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startRecordingButton: Button = findViewById(R.id.startRecordingButton)
        val stopRecordingButton: Button = findViewById(R.id.stopRecordingButton)
        val sendDataButton: Button = findViewById(R.id.sendDataButton)
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

        sendDataButton.setOnClickListener {
            Log.d(
                "com.example.sensorapp.MainActivity",
                "${applicationContext.packageName}.fileprovider"
            )

            // センサデータを記録したファイル一覧を取得
            val filesDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val files = filesDir?.listFiles()?.map { file ->
                FileProvider.getUriForFile(
                    this@MainActivity, "com.example.sensorapp.fileprovider", file
                )
            }

            files?.forEach { fileUri ->
                grantUriPermission(
                    packageName,
                    fileUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }

            files?.let {
                val shareIntent = ShareCompat.IntentBuilder(this).apply {
                    setChooserTitle("Send CSV files to ...")
                    files.forEach { file ->
                        addStream(file)
                    }
                    setType("text/csv")
                }.startChooser()
            }
        }
    }
}

