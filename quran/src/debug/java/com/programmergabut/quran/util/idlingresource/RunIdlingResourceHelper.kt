package com.programmergabut.quran.util.idlingresource

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