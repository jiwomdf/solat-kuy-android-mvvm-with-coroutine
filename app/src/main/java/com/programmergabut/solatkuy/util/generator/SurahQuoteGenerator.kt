package com.programmergabut.solatkuy.util.generator

class SurahQuoteGenerator {

    companion object{
        fun retAyah(): String {
            val listAyah = mutableListOf<String>()
            listAyah.add("Which, then, of your Sustainer’s powers can you disavow? - QS Ar-Rahmaan Ayah 13")
            listAyah.add("CONSIDER the flight of time! Verily, man is bound to lose himself, unless he be of those who attain to faith, and do good works, and enjoin upon one another the keeping to truth, and enjoin upon one another patience in adversity. - QS Al-Asr Ayah 1 - 3")
            listAyah.add("YOU ARE OBSESSED by greed for more and more, until you go down to your graves. - QS At-Takaathur Ayah 1-2")

            return listAyah[(0 until listAyah.size).random()]
        }

    }
}