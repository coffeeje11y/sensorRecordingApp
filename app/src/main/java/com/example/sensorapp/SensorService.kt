package com.example.sensorapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter

class SensorService : Service(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var gyroscope: Sensor? = null
    private var linearAcceleration: Sensor? = null
    private var magnet: Sensor? = null
    private var userName: String = "DefaultName"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN)
        )

        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(CHANNEL_NAME)
            .setContentText("${intent?.extras?.getString("userName")} のデータを収集中")
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .build()

        startForeground(1111, notification)

        // doWorkの処理
        if (sensorManager == null) {
            sensorManager =
                applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            linearAcceleration = sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
            magnet = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            registerSensors()
        }

        userName = intent?.extras?.getString("userName") ?: "DefaultName"
        Log.d("SensorWorker", "User name: $userName")

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
    }

    private fun writeGyroscopeText(context: Context, userName: String, log: String) {
        val fileName = "SensorLog_gyroscope_${userName}"
        val filePath : String = context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString().plus("/").plus(fileName).plus(".csv")

        val fil = FileWriter(filePath, true)
        val pw = PrintWriter(BufferedWriter(fil))
        pw.println(log)
        pw.close()
    }

    private fun writeLinearText(context: Context, userName: String, log: String) {
        val fileName = "SensorLog_linear_${userName}"
        val filePath : String = context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString().plus("/").plus(fileName).plus(".csv")

        val fil = FileWriter(filePath, true)
        val pw = PrintWriter(BufferedWriter(fil))
        pw.println(log)
        pw.close()
    }

    private fun writeMagneticText(context: Context, userName: String, log: String) {
        val fileName = "SensorLog_magnetic_${userName}"
        val filePath : String = context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString().plus("/").plus(fileName).plus(".csv")

        val fil = FileWriter(filePath, true)
        val pw = PrintWriter(BufferedWriter(fil))
        pw.println(log)
        pw.close()
    }

    private fun registerSensors() {
        sensorManager?.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME)
        sensorManager?.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_GAME)
        sensorManager?.registerListener(this, magnet, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == gyroscope) {
            // Handle Gyroscope Sensor Data
            val xGyroscope = event.values[0]
            val yGyroscope = event.values[1]
            val zGyroscope = event.values[2]
            val timestamp = System.currentTimeMillis()

            val logData = "$timestamp,$xGyroscope,$yGyroscope,$zGyroscope"

            writeGyroscopeText(applicationContext, userName, logData)
        }

        if (event.sensor == linearAcceleration) {
            // Handle Linear Acceleration Sensor Data
            val xLinear = event.values[0]
            val yLinear = event.values[1]
            val zLinear = event.values[2]
            val timestamp = System.currentTimeMillis()

            val logData = "$timestamp,$xLinear,$yLinear,$zLinear"

            writeLinearText(applicationContext, userName, logData)
        }

        if (event.sensor == magnet) {
            // Handle Linear Acceleration Sensor Data
            val xMag = event.values[0]
            val yMag = event.values[1]
            val zMag = event.values[2]
            val timestamp = System.currentTimeMillis()

            val logData = "$timestamp,$xMag,$yMag,$zMag"

            writeMagneticText(applicationContext, userName, logData)
        }
    }

    private fun stopRecording() {
        sensorManager?.unregisterListener(this) // センサーリスナーの登録を解除
        Log.d("SensorService", "Recording stopped.")
    }

    companion object {
        const val CHANNEL_ID = "sensorApp"
        const val CHANNEL_NAME = "sensorApp"
        var notification: Notification? = null
    }
}
