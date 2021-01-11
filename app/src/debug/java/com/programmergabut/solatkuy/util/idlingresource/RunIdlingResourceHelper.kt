package com.programmergabut.solatkuy.util.idlingresource

class RunIdlingResourceHelper {

    companion object {
        fun runIdlingResourceIncrement(){
            EspressoIdlingResource.increment()
        }

        fun runIdlingResourceDecrement(){
            EspressoIdlingResource.decrement()
        }
    }

}