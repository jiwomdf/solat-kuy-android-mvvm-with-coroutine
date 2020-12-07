package com.programmergabut.solatkuy.util.helper

import com.programmergabut.solatkuy.util.EspressoIdlingResource
import com.programmergabut.solatkuy.util.EnumConfig.Companion.IS_TESTING

class RunIdlingResourceHelper {

    companion object {
        fun runIdlingResourceIncrement(){
            if(!IS_TESTING) return
            EspressoIdlingResource.increment()
        }

        fun runIdlingResourceDecrement(){
            if(!IS_TESTING) return
            EspressoIdlingResource.decrement()
        }
    }

}