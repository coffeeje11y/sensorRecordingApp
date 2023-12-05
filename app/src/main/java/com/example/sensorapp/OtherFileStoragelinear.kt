
import android.content.Context
import android.os.Environment
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter

class OtherFileStoragelinear(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val fileAppend = true
    private var fileName: String = "SensorLog_linear_${inputData.getString("userName")}"

    private val extension: String = ".csv"
    private val filePath : String = context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString().plus("/").plus(fileName).plus(extension)
    override fun doWork(): Result {
        // inputDataから"log"を取得
        val log = inputData.getString("log") ?: ""

        // ファイルへの書き込み処理
        writeText(log)

        return Result.success()
    }

    private fun writeText(log: String) {
        val fil = FileWriter(filePath, fileAppend)
        val pw = PrintWriter(BufferedWriter(fil))
        pw.println(log)
        pw.close()
    }
}


