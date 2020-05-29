package com.programmergabut.solatkuy.util.generator

import androidx.lifecycle.LiveData
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.AsmaAlHusnaApi
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.En
import com.programmergabut.solatkuy.data.remote.remoteentity.quransurahJson.Ayah
import com.programmergabut.solatkuy.data.remote.remoteentity.quransurahJson.Edition
import com.programmergabut.solatkuy.data.remote.remoteentity.quransurahJson.QuranSurahApi
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.enumclass.EnumConfig

object DummyData {

    fun getNotifiedPrayer(): List<NotifiedPrayer> {
        val listNotifiedPrayer = mutableListOf<NotifiedPrayer>()

        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.fajr,
                true,
                "00:00"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.dhuhr,
                true,
                "00:00"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.asr,
                true,
                "00:00"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.maghrib,
                true,
                "00:00"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.isha,
                true,
                "00:00"
            )
        )
        listNotifiedPrayer.add(
            NotifiedPrayer(
                EnumConfig.sunrise,
                true,
                "00:00"
            )
        )


        return listNotifiedPrayer
    }

    fun getAsmaAlHusna(): AsmaAlHusnaApi {

        val list = mutableListOf<Data>()

        list.add(Data(En("test"),"test",0,"test"))

        return AsmaAlHusnaApi(0, list, "testing")
    }

    fun getSurahApi(): QuranSurahApi{
        val listAyah = mutableListOf<Ayah>()
        listAyah.add(Ayah(0,0,0,0,0,0,0,false,"test"))
        return QuranSurahApi(0,
            com.programmergabut.solatkuy.data.remote.remoteentity.quransurahJson.Data(listAyah,
                Edition("","","","","","",""),
                "", "", "", 0,0, ""
            )
            ,"")
    }

}