package com.example.notestrack.utils

import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TimeUtilStudy {

    /**Any Doubt follow below pattern*/
    private fun testTiming() {
        /*1. Ms to local time & Local Date
        * 2. Get Zone time
        * 3. Date format
        * 4. Change one format to another
        * */

        /**
         * Pattern type Datetime changed to UTC
         * */
        val timeFormatter = "2025-01-18 18:34:00"
        val localDateTimeFormat = LocalDateTime.parse(timeFormatter, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val converterFormatter = localDateTimeFormat.atZone(ZoneId.systemDefault()).toInstant()
        val zone = ZonedDateTime.of(localDateTimeFormat, ZoneId.systemDefault()).withZoneSameInstant(
            ZoneOffset.UTC)

        println("testTiming converterFormatter >>>$converterFormatter")
        println("testTiming converterFormatter >>>$zone")

        /**
         * Milli second type Datetime changed to UTC
         * */
        val instantInMs = Instant.now().toEpochMilli()
        val localDateTimeInMs = LocalDateTime.ofInstant(Instant.ofEpochMilli(instantInMs), ZoneId.systemDefault())
        val converterMs = localDateTimeInMs.atZone(ZoneId.systemDefault()).toInstant()

        /**
         * Local Date type Datetime changed to UTC
         * */
        val localDate = LocalDateTime.now()
        val localDateUsingNow = localDate.atZone(ZoneId.systemDefault()).toInstant()
        val converterLocal = localDateUsingNow


        /**
         * Utc Timestamp Convert to Local format
         * support
         * a. converterFormatter
         * b. converterMs
         * c. converterLocal
         * */
        val utcString = Instant.parse(converterMs.toString()).atZone(ZoneId.systemDefault()).toInstant()
        val localDateTime = LocalDateTime.ofInstant(utcString, ZoneId.systemDefault())

        /**
         * DateTimeFormatter
         * */
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")
        val resultMs = dateTimeFormatter.format(localDateTime)
        Timber.d("testTiming testTiming UI ms done >>> $resultMs ")
        // Loop again to [Pattern type Datetime changed to UTC]
    }

}