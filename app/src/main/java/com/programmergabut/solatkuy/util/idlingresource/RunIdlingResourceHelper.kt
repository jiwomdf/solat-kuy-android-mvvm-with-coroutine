package com.programmergabut.solatkuy.util.idlingresource

import com.programmergabut.solatkuy.util.EnumConfig.Companion.IS_IDLING_RESOURCE_ACTIVE

class RunIdlingResourceHelper {

    companion object {
        fun runIdlingResourceIncrement(){
            if(!IS_IDLING_RESOURCE_ACTIVE) return
            EspressoIdlingResource.increment()
        }

        fun runIdlingResourceDecrement(){
            if(!IS_IDLING_RESOURCE_ACTIVE) return
            EspressoIdlingResource.decrement()
        }
    }

}