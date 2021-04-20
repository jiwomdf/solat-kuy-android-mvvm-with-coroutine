package com.programmergabut.solatkuy.util

import android.util.Log
import com.google.gson.Gson
import com.programmergabut.solatkuy.BuildConfig
import com.programmergabut.solatkuy.base.BaseResponse
import com.programmergabut.solatkuy.util.LogConfig.Companion.RES
import retrofit2.Call
import retrofit2.HttpException

abstract class DebugUtil {
    companion object {
        fun<T : BaseResponse> execute(call : Call<T>) : T {
            try{
                val response = call.execute()
                return when(response.isSuccessful){
                    true -> {
                        if(BuildConfig.BUILD_TYPE == ("debug"))
                            Log.d(RES, Gson().toJson(response.body()!!))
                        response.body()!!
                    }
                    false -> {
                        if(BuildConfig.BUILD_TYPE == "debug")
                            Log.d(RES, response.message())
                        throw HttpException(response)
                    }
                }
            }
            catch (e : Exception){
                if(BuildConfig.BUILD_TYPE == "debug")
                    e.message?.let {
                        Log.d(RES, it)
                    }
                throw e
            }
        }
    }
}