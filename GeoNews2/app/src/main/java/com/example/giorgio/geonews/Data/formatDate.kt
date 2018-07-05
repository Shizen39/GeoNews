package com.example.giorgio.geonews.Data

import android.text.format.DateUtils
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import org.joda.time.format.DateTimeFormat

/**
 * Created by giorgio on 02/07/18.
 */

object formatter{
    fun formatDate(articlePubDateTime: String): String {
            // Tries to format the publish time properly prior to setting it as text. Otherwise,
            // catches an exception and sets the text invisible.
        try {

            // DateTimeFormatter initialized that's used to retrieve the UTC (e.g. 4 hours
            // faster than EST) time in milliseconds of when the article was published.
            val formatter = DateTimeFormat
                    .forPattern("yyyy-MM-dd HH:mm:ss")
                    .withZoneUTC()
            val millisecondsSinceUnixEpoch =
                    formatter.parseMillis(formatUTCDateTime(articlePubDateTime))

            // Uses one of DateUtils' static methods to compare how long ago the article was
            // published (e.g. 37 minutes ago, 5 hours ago, and etc.).
            val relativeTime = DateUtils.getRelativeTimeSpanString(
                    millisecondsSinceUnixEpoch,
                    System.currentTimeMillis(),
                    MINUTE_IN_MILLIS) // Minimum time to be displayed (secs would constitute as "0min ago")

            // Initially converts relativeTime to a String to possibly set the following
            // TextView as "just now!". Or, the TextView will be hidden if the String is
            // something like "In 5 min" which sometimes appears due to a back-end bug.
            // Otherwise, sets the publish time as is.
            val relativeTimeString = relativeTime.toString()
            if (relativeTimeString[0] == 'I' && relativeTimeString[1] == 'n') {
                return ""
            } else {
                return relativeTimeString
            }
        } catch (e: IndexOutOfBoundsException) {
            println("Problem formatting time string")
            return ""
        }
    }

    /**
     * Modifies the UTC date-time by removing the 'T' and 'Z' chars, and retrieving only the first
     * two chars of seconds (in case of partial seconds such as decimals) in order to satisfy
     * DateTimeFormat's format.
     *
     * takes utcDateTime is the UTC date-time according to ISO 8601 standards.
     *
     * throws IndexOutOfBoundsException since some custom indexing will be out of bounds of the # of time-attribute parts.
     */
    @Throws(IndexOutOfBoundsException::class)
    private fun formatUTCDateTime(utcDateTime: String?): String {
        val dateTimeSb = StringBuilder(utcDateTime)
        dateTimeSb.deleteCharAt(utcDateTime!!.length - 1)
        val dateTime = dateTimeSb.toString()
        val formattedDateTime = dateTime.replace('T', ' ')
        val dateTimeParts = formattedDateTime.split(" ".toRegex()).dropLastWhile {
            it.isEmpty()
        }.toTypedArray()
        val timeParts = dateTimeParts[1].split(":".toRegex()).dropLastWhile {
            it.isEmpty()
        }.toTypedArray()
        val time = timeParts[0] + ":" +
                timeParts[1] + ":" +
                timeParts[2][0].toString() +
                timeParts[2][1].toString()

        return dateTimeParts[0] + " " + time
    }
}