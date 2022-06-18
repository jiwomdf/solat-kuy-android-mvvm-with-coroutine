package com.programmergabut.solatkuy.util

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

data class Resource<out T>(var status: Status, val data: T?, var message:String = "Something went wrong") {
    companion object{
        fun<T> success(data:T?, msg: String = ""): Resource<T>{
            return Resource(Status.Success, data, msg)
        }
        fun<T> error(data:T?, msg: String = ""): Resource<T>{
            return Resource(Status.Error, data, msg)
        }
        fun<T> loading(data:T?): Resource<T>{
            return Resource(Status.Loading, data, "")
        }
    }
}

enum class Status {
    Success,
    Error,
    Loading
}