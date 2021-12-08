package com.hkamran.schedules

import android.content.Context
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class Period(val periodName: String, val startTime: String, val endTime: String)
data class Time(val hours: Int, val minutes: Int, val seconds: Int)
data class NotificationsSent(
    var oneHour: Boolean = false,
    var thirtyMinute: Boolean = false,
    var fifteenMinute: Boolean = false,
    var tenMinute: Boolean = false,
    var fiveMinute: Boolean = false,
    var oneMinute: Boolean = false,
    var thirtySecond: Boolean = false
)

fun readJSONFromFile(context: Context): JSONObject {
    val file = File(context.filesDir, "schedules.json")

    if (file.exists()) {
        val contents = file.readText()

        try {
            return JSONObject(contents)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return JSONObject()
}

fun getShortDay(): String {
    return SimpleDateFormat("E").format(Date()).uppercase()
}

fun getTime(): String {
    val date = Date()

    val hour = String.format("%02d", SimpleDateFormat("H").format(date).toInt())
    val minute = String.format("%02d", SimpleDateFormat("m").format(date).toInt())
    val second = String.format("%02d", SimpleDateFormat("s").format(date).toInt())

    return "$hour-$minute-$second"
}

fun calculateTimeDifference(startTime: String, endTime: String): Time {
    val format = SimpleDateFormat("H-m-s")
    val difference = format.parse(endTime).time - format.parse(startTime).time

    val hours = TimeUnit.MILLISECONDS.toHours(difference).toInt()
    var minutes = TimeUnit.MILLISECONDS.toMinutes(difference).toInt()
    var seconds = TimeUnit.MILLISECONDS.toSeconds(difference).toInt()

    minutes -= hours * 60
    seconds -= (minutes * 60) + (hours * 60 * 60)

    return Time(
        hours, minutes, seconds
    )
}

fun getCurrentPeriod(scheduleTimes: JSONObject, time: String): Period? {
    val periodNames = Array(scheduleTimes.names().length()) { scheduleTimes.names()[it] }

    periodNames.map { rawPeriodName ->
        val periodName = rawPeriodName as String

        val periodTimes = scheduleTimes.getJSONArray(periodName)
        val startTime = periodTimes.getString(0)
        val endTime = periodTimes.getString(1)

        if (startTime.replace("-", "")
                .toInt() <= time.toInt() && time.toInt() <= endTime.replace("-", "").toInt()
        ) {
            return Period(periodName, startTime, endTime)
        }
    }

    return null
}

private fun getPreviousEndTime(endTime: String): String {
    val splitEndTime = endTime.split("-")

    if (splitEndTime[2] != "59") {
        return (splitEndTime.joinToString(separator = "").toInt() + 1).toString()
    } else {
        var hours = splitEndTime[0].toInt()
        var minutes = splitEndTime[1].toInt()
        var seconds = splitEndTime[2].toInt()

        if (seconds >= 59) {
            minutes += 1
            seconds = if (seconds >= 60) seconds - 60 else seconds - 59
        }

        if (minutes >= 59) {
            hours += 1
            minutes = if (minutes >= 60) minutes - 60 else minutes - 59
        }

        return String.format("%02d", hours) + String.format("%02d", minutes) + String.format(
            "%02d",
            seconds
        )
    }
}

fun getNextPeriod(scheduleTimes: JSONObject, currentPeriodEndTime: String): Period? {
    val periodNames = Array(scheduleTimes.names().length()) { scheduleTimes.names()[it] }

    periodNames.map { rawPeriodName ->
        val periodName = rawPeriodName as String

        val periodTimes = scheduleTimes.getJSONArray(periodName)
        val startTime = periodTimes.getString(0)
        val endTime = periodTimes.getString(1)
        val previousEndTime =
            getPreviousEndTime(currentPeriodEndTime)

        if (startTime.replace("-", "").toInt().toString() == previousEndTime) {
            return Period(periodName, startTime, endTime)
        }
    }

    return null
}