package com.programmergabut.solatkuy.util

import com.programmergabut.solatkuy.util.enumclass.EnumStatus

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

data class Resource<out T>(val status: EnumStatus, val data: T?, val message:String?) {

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