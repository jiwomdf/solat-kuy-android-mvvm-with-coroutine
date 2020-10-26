package com.programmergabut.solatkuy.util.enumclass

/*
 * Created by Katili Jiwo Adi Wiyono on 12/04/20.
 */


class EnumConfig {
    companion object{

        const val DATABASE_NAME = "solatkuydb"

        const val FAJR = "Fajr"
        const val DHUHR = "Dhuhr"
        const val ASR = "Asr"
        const val MAGHRIB = "Maghrib"
        const val ISHA = "Isha"
        const val SUNRISE = "Sunrise"
        const val IMSAK = "Imsak"

        /* Notification */
        const val ID_DUA = 500 //main notification
        const val ID_MAIN = 400 //main notification
        //const val nId1 = 100 //first remind
        //const val nId2 = 200 //second remind

        /* more time */
        //const val mTime = 10

        /* Prayer Method */
        //const val pMethod = "3"

        /* City not found */
        const val CITY_NOT_FOUND_STR = "City not found"

        /* Dua after adhan*/
        const val DUA_AFTER_ADHAN_STR = "Tap to see the dua after adhan \uD83D\uDE4F"
    }
}