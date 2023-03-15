package com.hao.netcommon.retrofit

import com.hao.netcommon.ResBaseModel
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

/**
 *@author raohaohao
 *@data 2023/3/10
 *@version 1.0
 */
interface RestService {

    @GET
    fun get(@Url url: String, @Header("X-ZZ-Timestamp") timestamp: String): Flowable<ResBaseModel<Any>>


    @POST
    fun post(@Url url: String, @Body body: RequestBody): Flowable<ResBaseModel<Any>>

}