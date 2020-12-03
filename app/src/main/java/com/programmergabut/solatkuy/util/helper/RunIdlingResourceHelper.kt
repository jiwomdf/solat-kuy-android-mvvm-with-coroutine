package com.programmergabut.solatkuy.util.helper

import com.programmergabut.solatkuy.util.EspressoIdlingResource

class RunIdlingResourceHelper {

    companion object {

        private const val isTesting = true

        fun runIdlingResourceIncrement(){
            if(!isTesting) return
            EspressoIdlingResource.increment()
        }

        fun runIdlingResourceDecrement(){
            if(!isTesting) return
            EspressoIdlingResource.decrement()
        }
    }

}