package com.programmergabut.solatkuy.di.contextprovider

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class ContextProviderTest: ContextProvider {
    override fun main(): CoroutineContext = Dispatchers.Unconfined
    override fun io(): CoroutineContext = Dispatchers.Unconfined
}