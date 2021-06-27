package com.programmergabut.solatkuy.data.remote.json.methodJson


import com.google.gson.annotations.SerializedName

data class MethodData(
    @SerializedName("CUSTOM")
    val cUSTOM: CUSTOM,
    @SerializedName("EGYPT")
    val eGYPT: EGYPT,
    @SerializedName("FRANCE")
    val fRANCE: FRANCE,
    @SerializedName("GULF")
    val gULF: GULF,
    @SerializedName("ISNA")
    val iSNA: ISNA,
    @SerializedName("JAFARI")
    val jAFARI: JAFARI,
    @SerializedName("KARACHI")
    val kARACHI: KARACHI,
    @SerializedName("KUWAIT")
    val kUWAIT: KUWAIT,
    @SerializedName("MAKKAH")
    val mAKKAH: MAKKAH,
    @SerializedName("MOONSIGHTING")
    val mOONSIGHTING: MOONSIGHTING,
    @SerializedName("MWL")
    val mWL: MWL,
    @SerializedName("QATAR")
    val qATAR: QATAR,
    @SerializedName("RUSSIA")
    val rUSSIA: RUSSIA,
    @SerializedName("SINGAPORE")
    val sINGAPORE: SINGAPORE,
    @SerializedName("TEHRAN")
    val tEHRAN: TEHRAN,
    @SerializedName("TURKEY")
    val tURKEY: TURKEY
)