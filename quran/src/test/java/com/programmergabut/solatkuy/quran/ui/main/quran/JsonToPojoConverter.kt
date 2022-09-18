package com.programmergabut.solatkuy.quran.ui.main.quran

import com.google.gson.Gson
import java.io.IOException

class JsonToPojoConverter {

    companion object{
        inline fun <reified BASE, reified RES> convertJson(fileName: String): RES {
            val inputStream = readFromFile<BASE>(fileName)

            return Gson().fromJson(inputStream, RES::class.java)
        }

        @Throws(IOException::class)
        inline fun <reified BASE> readFromFile(name: String): String {
            val inputStream = BASE::class.java.classLoader.getResourceAsStream(name)
            val stringBuilder = StringBuilder()
            var i: Int
            val b = ByteArray(4096)
            while (inputStream.read(b).also { i = it } != -1) {
                stringBuilder.append(String(b, 0, i))
            }
            return stringBuilder.toString()
        }
    }


}