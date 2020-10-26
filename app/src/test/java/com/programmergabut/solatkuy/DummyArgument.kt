package com.programmergabut.solatkuy

import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.util.enumclass.EnumConfig

object DummyArgument {

    /* Last update : 2 July 2020 */

    val msApi1 = MsApi1(0, "-7.5633548", "110.8041806", "3","7","2020")
    val msfavSurah = MsFavSurah(1,"test","test")
    val surahID = 1
    val msFavAyah = MsFavAyah(1,1,"test","test","test")
    val msFavSurah = MsFavSurah(1,"test", "test")

    fun getMapPrayer(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()

        map[EnumConfig.FAJR] = "04:35"
        map[EnumConfig.DHUHR] = "11:41"
        map[EnumConfig.ASR] = "15:02"
        map[EnumConfig.MAGHRIB] = "17:32"
        map[EnumConfig.ISHA] = "18:42"
        map[EnumConfig.SUNRISE] = "05:00"

        return map
    }

}