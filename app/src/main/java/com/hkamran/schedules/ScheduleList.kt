package com.hkamran.schedules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hkamran.schedules.ui.theme.SchedulesTheme
import org.json.JSONObject

fun Color.toHex(): String = this.toArgb().toUInt().toString(16).takeLast(6)

@Composable
fun ScheduleList(
    navController: NavController,
    schedules: JSONObject?
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.h3)
        }

        Spacer(modifier = Modifier.height(15.dp))

        Column {
            val rootKeys = if (schedules != null) Array(
                schedules.names().length()
            ) { schedules.names()[it] } else arrayOf<String>()

            if (schedules != null) {
                rootKeys.forEach { rootKey ->
                    val scheduleId = rootKey as String
                    val schedule = schedules.getJSONObject(scheduleId)

                    ScheduleCard(
                        Modifier.clickable(
                            enabled = true,
                            onClickLabel = schedule.getString("name"),
                            role = Role.Button,
                            onClick = {
                                navController.navigate("schedule/$scheduleId")
                            }), schedule.getString("name"), schedule.getString("color")
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    createNotificationChannel(
                        LocalContext.current,
                        "schedule.$scheduleId",
                        schedule.getString("name"),
                        "Notifications for ${schedule.getString("name")}"
                    )
                }
            }

            if (rootKeys.isEmpty()) {
                ScheduleCard(
                    scheduleName = stringResource(R.string.no_schedules_available),
                    color = "#404040"
                )
                Spacer(modifier = Modifier.height(15.dp))
            }

            Spacer(modifier = Modifier.height(15.dp))
            ScheduleCard(
                Modifier.clickable(
                    enabled = true,
                    onClickLabel = stringResource(R.string.settings_label),
                    role = Role.Button,
                    onClick = {
                        navController.navigate("settings")
                    }),
                scheduleName = stringResource(R.string.settings_label),
                color = "#${
                    MaterialTheme.colors.primary.toHex()
                }"
            )
        }
    }
}

@Composable
fun ScheduleCard(modifier: Modifier = Modifier, scheduleName: String, color: String) {
    val actualColor = Color.parse(color)
    Card(
        backgroundColor = actualColor,
        contentColor = if ((actualColor.red * 0.299) + (actualColor.green * 0.587) + (actualColor.blue * 0.114) > 186) Color.Black else Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = scheduleName,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(name = "Schedule Cards", showBackground = true)
@Composable
fun ScheduleCardPreview() {
    SchedulesTheme {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Schedules", style = MaterialTheme.typography.h3)
            Spacer(modifier = Modifier.height(15.dp))
            Column {
                ScheduleCard(
                    Modifier.clickable(
                        enabled = true,
                        onClickLabel = "AUHSD Standard Schedule",
                        role = Role.Button,
                        onClick = {}), "AUHSD Standard Schedule", "#343254"
                )

                Spacer(modifier = Modifier.height(10.dp))

                ScheduleCard(
                    Modifier.clickable(
                        enabled = true,
                        onClickLabel = "AUHSD Finals Schedule",
                        role = Role.Button,
                        onClick = {}), "AUHSD Finals Schedule", "#343254"
                )

            }

        }
    }
}

@Preview(name = "Empty Schedule Card (Light)", showBackground = true, group = "Empty Schedule Card")
@Composable
fun EmptyScheduleCardPreview() {
    SchedulesTheme {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Schedules", style = MaterialTheme.typography.h3)
            Spacer(modifier = Modifier.height(15.dp))
            Column {
                ScheduleCard(scheduleName = "No Schedules Available", color = "#404040")
            }

        }
    }
}

@Preview(name = "Empty Schedule Card (Dark)", group = "Empty Schedule Card")
@Composable
fun DarkEmptyScheduleCardPreview() {
    SchedulesTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Schedules", style = MaterialTheme.typography.h3)
            Spacer(modifier = Modifier.height(15.dp))
            Column {
                ScheduleCard(scheduleName = "No Schedules Available", color = "#404040")
            }

        }
    }
}