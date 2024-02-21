package com.example.sensorapp

// MainActivity.kt
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import jp.gr.java_conf.alpherg0221.compose.material3.theme.BlueJadeTheme
import java.io.File

data class SavedData(
    val path: String,
    val size: Long,
    val checked: Boolean,
)

class MainActivity : AppCompatActivity() {
    private lateinit var sensorService: Intent
    private lateinit var savedData: Array<File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorService = Intent(this, SensorService::class.java)
        loadSavedData()

        setContent {
            var userName by remember { mutableStateOf("") }
            val checkedList = remember {
                savedData.map { SavedData(it.name, it.length(), false) }.toMutableStateList()
            }

            BlueJadeTheme {
                Scaffold { paddingValues ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("センサデータ収集")

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = userName,
                            onValueChange = { newValue -> userName = newValue },
                        )

                        Spacer(Modifier.height(12.dp))

                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxWidth(),
                            columns = GridCells.Fixed(2),
                        ) {
                            // Start Button
                            item {
                                Button(
                                    modifier = Modifier.padding(end = 8.dp),
                                    colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)),
                                    onClick = { onStartClick(userName) },
                                ) {
                                    Text("Start Recording")
                                }
                            }
                            // Stop Button
                            item {
                                Button(
                                    modifier = Modifier.padding(start = 8.dp),
                                    colors = ButtonDefaults.buttonColors(Color(0xFFFF0000)),
                                    onClick = {
                                        onStopClick()
                                        loadSavedData()
                                        checkedList.clear()
                                        checkedList.addAll(savedData.map { file ->
                                            SavedData(file.name, file.length(), false)
                                        })
                                    },
                                ) {
                                    Text("Stop Recording")
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Send Data Button
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(Color(0xFF03A9F4)),
                            onClick = {
                                onSendDataClick(checkedList.filter { it.checked }.map { it.path })
                            },
                        ) {
                            Text("Send Data")
                        }

                        Spacer(Modifier.height(24.dp))

                        Text("収集済みデータ")

                        Spacer(Modifier.height(12.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            itemsIndexed(checkedList) { idx, checkedPath ->
                                Surface(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Checkbox(
                                            checked = checkedPath.checked,
                                            onCheckedChange = {
                                                checkedList[idx] = SavedData(
                                                    checkedPath.path,
                                                    checkedPath.size,
                                                    !checkedPath.checked,
                                                )
                                            },
                                        )

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = checkedPath.path,
                                                fontSize = 14.sp,
                                                maxLines = 2,
                                            )
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .alpha(.6f),
                                                text = "${checkedPath.size / 1024} KB",
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadSavedData() {
        savedData =
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.listFiles()
                ?: arrayOf()
    }

    private fun onStartClick(userName: String) {
        Log.d("com.example.sensorapp.MainActivity", "Button clicked! User name: $userName")

        sensorService.apply {
            putExtra("userName", userName)
        }
        startForegroundService(sensorService)
    }

    private fun onStopClick() {
        //書き込むのをやめる
        Log.d("com.example.sensorapp.MainActivity", "Stop Button clicked!")
        stopService(sensorService)
        loadSavedData()
    }

    private fun onSendDataClick(checked: List<String>) {
        Log.d(
            "com.example.sensorapp.MainActivity",
            "${applicationContext.packageName}.fileprovider"
        )

        // センサデータを記録したファイル一覧を取得
        val filesDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val files = filesDir?.listFiles()?.filter {
            checked.contains(it.name)
        }?.map { file ->
            FileProvider.getUriForFile(this, "com.example.sensorapp.fileprovider", file)
        }

        files?.forEach { fileUri ->
            grantUriPermission(
                packageName,
                fileUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }

        files?.let {
            ShareCompat.IntentBuilder(this).apply {
                setChooserTitle("Send CSV files to ...")
                files.forEach { file ->
                    addStream(file)
                }
                setType("text/csv")
            }.startChooser()
        }
    }
}

