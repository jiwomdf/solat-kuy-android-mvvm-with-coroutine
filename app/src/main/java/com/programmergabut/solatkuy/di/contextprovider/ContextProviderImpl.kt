package com.programmergabut.solatkuy.di.contextprovider

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class ContextProviderImpl: ContextProvider {
    override fun main(): CoroutineContext = Dispatchers.Main
    override fun io(): CoroutineContext = Dispatchers.IO
}