package com.programmergabut.solatkuy

import kotlinx.coroutines.CompletableDeferred

class CoroutineTestUtil {
    companion object {
        fun <T> T.toDeferred() = CompletableDeferred(this)
    }
}