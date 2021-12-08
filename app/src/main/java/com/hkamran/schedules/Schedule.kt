package com.hkamran.schedules

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.delay
import org.json.JSONObject

@Composable
fun Schedule(scheduleId: String, schedules: JSONObject) {
    val schedule = schedules.getJSONObject(scheduleId)

    val scheduleDays = Array(
        schedule.getJSONObject("schedule").names().length()
    ) { schedule.getJSONObject("schedule").names()[it] }
    val scheduleTimes = if (scheduleDays.contains(getShortDay())) schedule.getJSONObject("schedule")
        .getJSONObject(getShortDay()) else null

    val (currentPeriod, setCurrentPeriod) = remember {
        mutableStateOf(
            if (scheduleTimes != null) getCurrentPeriod(
                scheduleTimes,
                getTime().replace("-", "")
            ) else null
        )
    }

    val (oldPeriod, setOldPeriod) = remember { mutableStateOf<Period?>(null) }

    val (timeDifference, setTimeDifference) = remember {
        mutableStateOf(
            if (currentPeriod != null) calculateTimeDifference(
                getTime(),
                currentPeriod.endTime
            ) else null
        )
    }

    val (nextPeriod, setNextPeriod) = remember {
        mutableStateOf(
            if (scheduleTimes != null && currentPeriod != null) getNextPeriod(
                scheduleTimes,
                currentPeriod.endTime
            ) else null
        )
    }

    val (notificationsSent, setNotificationsSent) = remember { mutableStateOf(NotificationsSent()) }

    val context = LocalContext.current

    fun notify(title: String, content: Int) {
        val notification = NotificationCompat.Builder(context, "schedule.$scheduleId")
            .setSmallIcon(R.drawable.ic_schedules_logo)
            .setContentTitle(title)
            .setContentText(context.resources.getString(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup("com.hkamran.schedules.$scheduleId")

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), notification.build())
        }
    }

    setCurrentPeriod(
        if (scheduleTimes != null) getCurrentPeriod(
            scheduleTimes,
            getTime().replace("-", "")
        ) else null
    )

    setOldPeriod(currentPeriod)

    setTimeDifference(
        if (currentPeriod != null) calculateTimeDifference(
            getTime(),
            currentPeriod.endTime
        ) else null
    )

    setNextPeriod(
        if (scheduleTimes != null && currentPeriod != null) getNextPeriod(
            scheduleTimes,
            currentPeriod.endTime
        ) else null
    )

    LaunchedEffect(true) {
        while (true) {
            setCurrentPeriod(
                if (scheduleTimes != null) getCurrentPeriod(
                    scheduleTimes,
                    getTime().replace("-", "")
                ) else null
            )

            if (oldPeriod != currentPeriod) {
                setOldPeriod(currentPeriod)
                setTimeDifference(null)
                setNotificationsSent(NotificationsSent())
            }

            setTimeDifference(
                if (currentPeriod != null) calculateTimeDifference(
                    getTime(),
                    currentPeriod.endTime
                ) else null
            )

            setNextPeriod(
                if (scheduleTimes != null && currentPeriod != null) getNextPeriod(
                    scheduleTimes,
                    currentPeriod.endTime
                ) else null
            )

            // Notifications
            if (currentPeriod != null) {
                val localTimeDifference = calculateTimeDifference(
                    getTime(),
                    currentPeriod.endTime
                )
                val notificationTitle =
                    ("${schedule.getString("shortName")} - ${currentPeriod.periodName}")

                if (localTimeDifference.hours == 0 && localTimeDifference.seconds == 0) {
                    if (localTimeDifference.minutes == 30 && !notificationsSent.thirtyMinute) {
                        notify(notificationTitle, R.string.notification_thirty_minute_remaining)

                        notificationsSent.thirtyMinute = true
                        setNotificationsSent(notificationsSent)
                    } else if (localTimeDifference.minutes == 15 && !notificationsSent.fifteenMinute) {
                        notify(notificationTitle, R.string.notification_fifteen_minute_remaining)

                        notificationsSent.fifteenMinute = true
                        setNotificationsSent(notificationsSent)
                    } else if (localTimeDifference.minutes == 10 && !notificationsSent.tenMinute) {
                        notify(notificationTitle, R.string.notification_ten_minute_remaining)

                        notificationsSent.tenMinute = true
                        setNotificationsSent(notificationsSent)
                    } else if (localTimeDifference.minutes == 5 && !notificationsSent.fiveMinute) {
                        notify(notificationTitle, R.string.notification_five_minute_remaining)

                        notificationsSent.fiveMinute = true
                        setNotificationsSent(notificationsSent)
                    } else if (localTimeDifference.minutes == 1 && !notificationsSent.oneMinute) {
                        notify(notificationTitle, R.string.notification_one_minute_remaining)

                        notificationsSent.oneMinute = true
                        setNotificationsSent(notificationsSent)
                    }
                } else if (localTimeDifference.hours == 0 && localTimeDifference.minutes == 0 && localTimeDifference.seconds == 30 && !notificationsSent.thirtySecond) {
                    notify(notificationTitle, R.string.notification_thirty_second_remaining)

                    notificationsSent.thirtySecond = true
                    setNotificationsSent(notificationsSent)
                } else if (localTimeDifference.hours == 1 && localTimeDifference.minutes == 0 && localTimeDifference.seconds == 0 && !notificationsSent.oneHour) {
                    notify(notificationTitle, R.string.notification_one_hour_remaining)

                    notificationsSent.oneHour = true
                    setNotificationsSent(notificationsSent)
                }
            }

            // Delay is in milliseconds
            delay(if (scheduleDays.contains(getShortDay())) 1000 else 60000)
        }
    }

    Column {
        Text(
            schedule.getString("shortName"),
            style = MaterialTheme.typography.h3,
            color = if (isSystemInDarkTheme()) Color.White else Color.Black
        )
        Spacer(modifier = Modifier.height(15.dp))
        Column {
            if (currentPeriod != null && timeDifference != null) {
                ScheduleRow(
                    currentPeriod.periodName,
                    String.format("%02d", timeDifference.hours) + ":" + String.format(
                        "%02d",
                        timeDifference.minutes
                    ) + ":" + String.format("%02d", timeDifference.seconds)
                )
            } else {
                ScheduleRow("Out of Schedule", "No Period")
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (nextPeriod != null) {
                var (hour, minutes) = nextPeriod.startTime.split("-").take(2)
                hour = String.format("%02d", hour.toInt())
                minutes = String.format("%02d", minutes.toInt())

                ScheduleRow(nextPeriod.periodName, "$hour:$minutes")
            }
        }
    }
}

@Composable
fun ScheduleRow(period: String, time: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = 12.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = period,
                style = MaterialTheme.typography.h6,
            )

            Text(
                text = time,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Preview
@Composable
fun SchedulePreview() {
    Schedule("ca-auhsd-hss", JSONObject())
}

@Preview(showBackground = true)
@Composable
fun ScheduleRowPreview() {
    ScheduleRow("Creative Writing and Public Speaking (Sixth Period)", "00:54:01")
}