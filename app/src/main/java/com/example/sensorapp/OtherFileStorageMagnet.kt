package com.example.sensorapp

import android.os.Environment
import androidx.work.ListenableWorker
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter

class OtherFileStorageMagnet() {
    Worker(context, workerParams) {

        private val fileAppend = true
        private var fileName: String = "SensorLog_linear_${inputData.getString("userName")}"

        private val extension: String = ".csv"
        private val filePath : String = context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString().plus("/").plus(fileName).plus(extension)
        override fun doWork(): ListenableWorker.Result {
            // inputDataから"log"を取得
            val log = inputData.getString("log") ?: ""

            // ファイルへの書き込み処理
            writeText(log)

            return ListenableWorker.Result.success()
        }

        private fun writeText(log: String) {
            val fil = FileWriter(filePath, fileAppend)
            val pw = PrintWriter(BufferedWriter(fil))
            pw.println(log)
            pw.close()
        }
}