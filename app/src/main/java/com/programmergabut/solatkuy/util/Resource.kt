package com.programmergabut.solatkuy.util

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

data class Resource<out T>(var status: Status, val data: T?, val message:String?) {
    companion object{
        fun<T> success(data:T?, msg: String = ""): Resource<T>{
            return Resource(Status.SUCCESS, data, msg)
        }
        fun<T> error(data:T?, msg: String = ""): Resource<T>{
            return Resource(Status.ERROR, data, msg)
        }
        fun<T> loading(data:T?): Resource<T>{
            return Resource(Status.LOADING, data, null)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}