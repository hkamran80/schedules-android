package com.hkamran.schedules

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hkamran.schedules.ui.theme.SchedulesTheme
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val (schedules, setSchedules) = remember { mutableStateOf<JSONObject?>(null) }
            val (refreshingSchedules, setRefreshingSchedules) = remember { mutableStateOf(false) }

            fun loadJSONFromFile() {
                if (schedules == null) {
                    setSchedules(readJSONFromFile(applicationContext))
                    Log.i("ScheduleLoading", "Loaded JSON from file")
                }
            }

            LaunchedEffect(false) {
                if (isNetworkConnected(applicationContext)) {
                    Thread {
                        runOnUiThread {
                            setRefreshingSchedules(true)
                        }

                        val url = URL("https://schedules.unisontech.org/schedules.json")
                        val httpClient = url.openConnection() as HttpURLConnection
                        if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
                            try {
                                val stream =
                                    BufferedReader(InputStreamReader(httpClient.inputStream))
                                val stringBuilder = StringBuilder()
                                stream.forEachLine { stringBuilder.append(it) }
                                val data = stringBuilder.toString()
                                val json = JSONObject(data)

                                val streamWriter = OutputStreamWriter(
                                    applicationContext.openFileOutput(
                                        "schedules.json",
                                        Context.MODE_PRIVATE
                                    )
                                )
                                streamWriter.write(json.toString())
                                streamWriter.close()

                                runOnUiThread { setSchedules(json) }

                                Log.i(
                                    "ScheduleLoading",
                                    "Written data to ${applicationContext.filesDir}/schedules.json"
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.e("ScheduleLoading", e.toString())
                            } finally {
                                httpClient.disconnect()
                            }
                        } else {
                            Log.e("ScheduleLoading", "HTTP Error ${httpClient.responseCode}")
                        }

                        runOnUiThread {
                            setRefreshingSchedules(false)
                            loadJSONFromFile()
                        }
                    }.start()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Unable to check for updates to the schedules. Please try again later.",
                        Toast.LENGTH_LONG
                    ).show()

                    Log.i("ScheduleLoading", "No internet connection found")

                    loadJSONFromFile()
                }
            }

            SchedulesTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    NavHost(
                        navController = navController,
                        startDestination = "list",
                        modifier = Modifier.padding(36.dp)
                    ) {
                        composable("list") {
                            ScheduleList(
                                navController,
                                schedules
                            )
                        }

                        composable("schedule/{scheduleId}") { backStackEntry ->
                            backStackEntry.arguments?.getString("scheduleId")
                                ?.let { Schedule(it, schedules as JSONObject) }
                        }

                        composable("settings") {
                            Settings()
                        }
                    }
                }
            }
        }
    }
}

fun Color.Companion.parse(colorString: String): Color {
    return Color(color = android.graphics.Color.parseColor(colorString))
}
