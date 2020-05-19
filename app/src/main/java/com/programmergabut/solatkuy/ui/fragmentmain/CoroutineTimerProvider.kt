package com.programmergabut.solatkuy.ui.fragmentmain

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class CoroutineTimerProvider {

    open val Main: CoroutineContext = Dispatchers.Main
    open val IO: CoroutineContext = Dispatchers.IO

    companion object {
        @Volatile
        private var INSTANCE: CoroutineTimerProvider? = null

        fun getInstance() : CoroutineTimerProvider {
            return INSTANCE?: synchronized(this) {
                CoroutineTimerProvider()
            }.also {
                INSTANCE = it
            }
        }
    }
}