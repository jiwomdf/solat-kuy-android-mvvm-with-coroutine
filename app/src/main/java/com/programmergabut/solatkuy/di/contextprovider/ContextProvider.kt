package com.programmergabut.solatkuy.di.contextprovider

import kotlin.coroutines.CoroutineContext

interface ContextProvider {
    fun main(): CoroutineContext
    fun io(): CoroutineContext
}