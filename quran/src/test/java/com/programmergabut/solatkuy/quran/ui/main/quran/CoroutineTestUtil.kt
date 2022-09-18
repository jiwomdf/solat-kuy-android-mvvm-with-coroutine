package com.programmergabut.solatkuy.quran.ui.main.quran

import kotlinx.coroutines.CompletableDeferred

class CoroutineTestUtil {
    companion object {
        fun <T> T.toDeferred() = CompletableDeferred(this)
    }
}