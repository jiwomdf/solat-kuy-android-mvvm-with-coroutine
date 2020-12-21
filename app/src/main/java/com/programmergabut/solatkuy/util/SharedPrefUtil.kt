package com.programmergabut.solatkuy.util

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPrefUtil @Inject constructor() {

    companion object {
        const val LAST_READ_SURAH = "lastReadSurah"
        const val LAST_READ_AYAH = "lastReadAyah"
    }

    @Inject
    lateinit var sharedPref: SharedPreferences

    fun getLastReadSurah(): Int {
        return sharedPref.getInt(LAST_READ_SURAH, -1)
    }

    fun getIsNotHasOpenAnimation(): Boolean {
        return sharedPref.getBoolean("isHasNotOpenAnimation", true)
    }

    fun setIsNotHasOpenAnimation(value: Boolean){
        sharedPref.edit()?.apply{
            putBoolean("isHasNotOpenAnimation", value)
            apply()
        }
    }

    fun getLastReadAyah(): Int {
        return sharedPref.getInt(LAST_READ_AYAH, -1)
    }

    fun getIsBrightnessActive(): Boolean {
        return sharedPref.getBoolean("isBrightnessActive", false)
    }

    fun setIsBrightnessActive(value: Boolean){
        sharedPref.edit()?.apply{
            putBoolean("isBrightnessActive", value)
            apply()
        }
    }

    fun insertLastReadSharedPref(selectedSurahId: Int, numberInSurah: Int) {
        sharedPref.edit()?.apply{
            putInt(LAST_READ_SURAH, selectedSurahId)
            putInt(LAST_READ_AYAH, numberInSurah)
            apply()
        }
    }


}