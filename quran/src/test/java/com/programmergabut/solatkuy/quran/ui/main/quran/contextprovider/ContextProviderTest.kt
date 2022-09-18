package com.programmergabut.solatkuy.quran.ui.main.quran.contextprovider

import com.programmergabut.solatkuy.di.contextprovider.ContextProvider
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class ContextProviderTest: ContextProvider {
    override fun main(): CoroutineContext = Dispatchers.Unconfined
    override fun io(): CoroutineContext = Dispatchers.Unconfined
}