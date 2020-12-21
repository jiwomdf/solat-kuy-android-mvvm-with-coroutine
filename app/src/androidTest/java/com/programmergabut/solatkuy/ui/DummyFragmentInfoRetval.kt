package com.programmergabut.solatkuy.ui

import com.programmergabut.solatkuy.R

object DummyFragmentInfoRetval {

    /* Fragment Info
    * Last update 08 July 2020
    */
    fun fragmentInfoTopCard(): MutableMap<String, String> {
        val expectedData = mutableMapOf<String, String>()

        expectedData["tv_imsak_info_title"] = "Imsak Info"
        expectedData["tv_imsak_date"] = "09 Jul 2020"
        expectedData["tv_city"] = "Kota Surakarta"
        expectedData["tv_imsak_time"] = "04:26 (WIB)"

        return expectedData
    }

    fun fragmentInfoGregorianHijriCard(): MutableMap<String, String> {
        val expectedData = mutableMapOf<String, String>()

        expectedData["tv_gregorian_date"] = "08-07-2020"
        expectedData["tv_hijri_date"] = "17-11-1441"
        expectedData["tv_gregorian_month"] = "July"
        expectedData["tv_hijri_month"] = ""
        expectedData["tv_gregorian_day"] = "Wednesday"
        expectedData["tv_hijri_day"] = ""

        return expectedData
    }

}