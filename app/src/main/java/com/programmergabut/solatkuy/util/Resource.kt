package com.programmergabut.solatkuy.util

data class Resource<out T>(val Status: EnumStatus, val data: T?, val message:String?) {

    companion object{

        fun<T> success(data:T?): Resource<T>{
            return Resource(EnumStatus.SUCCESS,data, null)
        }

        fun<T> error(msg: String, data:T?): Resource<T>{
            return Resource(EnumStatus.ERROR,data, msg)
        }

        fun<T> loading(data:T?): Resource<T>{
            return Resource(EnumStatus.LOADING,data, null)
        }

    }

}