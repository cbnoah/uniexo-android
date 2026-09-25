package com.unicofrance.uniexo.data.local.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


/**
 * Prepopulate the database with the containers.csv file
 * @param context: The context to use
 * @param db: The database to prepopulate
 */
fun prepopulateDb(context: Context, db: SupportSQLiteDatabase) {
    try {
        val inputStream = context.assets.open("database/containers.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))

        db.beginTransaction()

        try {
            val regex: Regex = ",(?=(?:(?:[^\"]*\"){2})*[^\"]*$)".toRegex()
            reader.readLine()
            var line: String? = reader.readLine()

            while (line != null) {
                if (line.isNotBlank()) {
                    val values = regex.split(line)
                    val query =
                        """INSERT OR IGNORE INTO Container (id, longitude, latitude, label, producingPlaceLabel, description, streamLabel, streamColor, iconUrl, creationDatetime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"""
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val address =
                        values[5].removePrefix('"'.toString()).removeSuffix('"'.toString())

                    db.execSQL(
                        query,
                        arrayOf<Any>(
                            values[0].trim(),
                            values[1].trim().toDouble(),
                            values[2].trim().toDouble(),
                            values[3].trim(),
                            values[4].trim(),
                            address,
                            values[6].trim(),
                            values[7].trim(),
                            values[8].trim(),
                            LocalDateTime.parse(values[9].trim(), formatter)
                                .toEpochSecond(ZoneOffset.UTC)
                        )
                    )
                    line = reader.readLine()
                }
            }
            db.setTransactionSuccessful()
        } catch (e: IOException) {
            throw e
        } finally {
            db.endTransaction()
            reader.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
