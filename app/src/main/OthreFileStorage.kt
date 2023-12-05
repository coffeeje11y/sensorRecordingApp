
    import android.os.Bundle
    import android.os.Environment
    import androidx.appcompat.app.AppCompatActivity
    import java.io.BufferedWriter
    import java.io.FileWriter
    import java.io.PrintWriter

    class OtherFileStorage: AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val filePath: String = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString().plus("/Log.csv") //内部ストレージのDocumentのURL
            val fileAppend: Boolean = true //true=追記, false=上書き
            val fil = FileWriter(filePath,fileAppend)
            val pw = PrintWriter(BufferedWriter(fil))
            pw.println("3")
            pw.println("1")
            pw.println("4")
            pw.close()
        }
    }
