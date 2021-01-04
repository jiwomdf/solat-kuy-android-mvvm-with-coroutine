package com.programmergabut.solatkuy.util

/*
 * Created by Katili Jiwo Adi Wiyono on 12/04/20.
 */


class EnumConfig {
    companion object{

        /* IS TESTING
         * if you change IS_TESTING, please rebuild the project
         */
        const val IS_TESTING = true

        /* DATABASES */
        const val DATABASE_NAME = "solatkuydb"

        /* Al-Quran */
        const val STARTED_SURAH = 1
        const val ENDED_SURAH = 114

        /* For the first time populating the database MsApi1 table */
        const val START_LAT = "0.0"
        const val START_LNG = "0.0"
        const val START_METHOD = "3"
        const val START_MONTH = "1"
        const val START_YEAR = "2020"

        /* Prayer Name */
        const val FAJR = "Fajr"
        const val DHUHR = "Dhuhr"
        const val ASR = "Asr"
        const val MAGHRIB = "Maghrib"
        const val ISHA = "Isha"
        const val SUNRISE = "Sunrise"
        const val IMSAK = "Imsak"

        /* Prayer Time for testing */
        const val FAJR_TIME = "04:00"
        const val DHUHR_TIME = "12:00"
        const val ASR_TIME = "14:20"
        const val MAGHRIB_TIME = "17:45"
        const val ISHA_TIME = "19:00"
        const val SUNRISE_TIME = "06:00"

        /* Notification */
        const val ID_DUA_PENDING_INTENT = 500
        const val ID_PRAYER_NOTIFICATION = 400

        /* Notification pattern */
        const val VIBRATE_MS = 800L
        const val AWAIT_VIBRATE_MS = 800L

        /* City not found */
        const val CITY_NOT_FOUND_STR = "City not found"

        /* Dua after adhan*/
        const val DUA_AFTER_ADHAN_STR = "Tap to see the dua after adhan \uD83D\uDE4F"
    }
}