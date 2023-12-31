package com.example.sensorapp

import OtherFileStoragegyroscope
import OtherFileStoragelinear
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class SensorService(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var gyroscope: Sensor? = null
    private var linearAcceleration: Sensor? = null
    private var userName: String = "DefaultName"

    override fun doWork(): Result {
        if (sensorManager == null) {
            sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            linearAcceleration = sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
            registerSensors()
        }

        userName = inputData.getString("userName") ?: "DefaultName"
        Log.d("SensorWorker", "User name: $userName")

        return Result.success()
    }

    private fun registerSensors() {
        sensorManager?.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager?.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_NORMAL)
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

            val data = workDataOf("userName" to userName, "log" to logData)

            val gyroscopeWorkRequest = OneTimeWorkRequestBuilder<OtherFileStoragegyroscope>()
                .setInputData(data)
                .addTag("gyroscopeWorkTag")
                .build()

            WorkManager.getInstance(applicationContext).enqueue(gyroscopeWorkRequest)
        }

        if (event.sensor == linearAcceleration) {
            // Handle Linear Acceleration Sensor Data
            val xLinear = event.values[0]
            val yLinear = event.values[1]
            val zLinear = event.values[2]
            val timestamp = System.currentTimeMillis()

            val logData = "$timestamp,$xLinear,$yLinear,$zLinear"

            val data = workDataOf("userName" to userName, "log" to logData)

            val linearAccelerationWorkRequest = OneTimeWorkRequestBuilder<OtherFileStoragelinear>()
                .setInputData(data)
                .addTag("linearAccelerationWorkTag")
                .build()

            WorkManager.getInstance(applicationContext).enqueue(linearAccelerationWorkRequest)
        }
    }
}
