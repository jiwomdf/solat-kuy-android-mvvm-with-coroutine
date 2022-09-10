package com.programmergabut.solatkuy.util

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPrefUtil @Inject constructor() {

    companion object {
        const val LAST_READ_SURAH = "lastReadSurah"
        const val LAST_READ_AYAH = "lastReadAyah"
        const val SELECTED_METHOD = "selected_method"
    }

    @Inject
    lateinit var sharedPref: SharedPreferences

    fun getLastReadSurah(): Int {
        return sharedPref.getInt(LAST_READ_SURAH, -1)
    }

    fun getIsHasOpenAnimation(): Boolean {
        return sharedPref.getBoolean("isHasOpenAnimation", true)
    }

    fun setIsHasOpenAnimation(value: Boolean){
        sharedPref.edit()?.apply{
            putBoolean("isHasOpenAnimation", value)
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

    fun getReadSurahContentType(): Int {
        return sharedPref.getInt("setReadSurahContentType", 1)
    }

    fun setReadSurahContentType(contentType: Int) {
        sharedPref.edit()?.apply{
            putInt("setReadSurahContentType", contentType)
            apply()
        }
    }

    fun getReadSurahArTextSize(): Int {
        return sharedPref.getInt("setReadSurahArTextSize", 1)
    }

    fun setReadSurahArTextSize(textSize: Int) {
        sharedPref.edit()?.apply{
            putInt("setReadSurahArTextSize", textSize)
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

    fun getSelectedMethod(): Int {
        return sharedPref.getInt(SELECTED_METHOD, -1)
    }

    fun insertSelectedMethod(selectedMethod: Int) {
        sharedPref.edit()?.apply{
            putInt(SELECTED_METHOD, selectedMethod)
            apply()
        }
    }

}