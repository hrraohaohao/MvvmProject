package com.hao.netcommon.retrofit

import com.hao.commom.application.BasicApplication
import com.hao.netcommon.HttpConfig
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 *@author raohaohao
 *@data 2023/3/10
 *@version 1.0
 */
class RetrofitWrap {

    companion object {
        const val TIMEOUT = HttpConfig.DEFAULT_TIMEOUT_MILLISECONDS
        lateinit var level: HttpLoggingInterceptor.Level
        lateinit var DEFAULT_HOST: String
        private var interceptors: Array<out Interceptor>? = null

        @Volatile
        private var instance: RetrofitWrap? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: RetrofitWrap()
                    .also { instance = it }
            }

        /**
         * 添加额外的Interceptor
         */
        fun addInterceptor(vararg interceptors: Interceptor) {
            this.interceptors = interceptors
        }
    }

    init {
        var headerInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                return chain.proceed(request.newBuilder()
                    .addHeader("Connection", "keep-alive")
                    .addHeader("X-ZZ-Device-Sn", "S201D89F70115")
                    .addHeader("v", "1000")
                    .addHeader("POS-Authorization", "")
                    .build())
            }
        }
        addInterceptor(headerInterceptor)
    }


    var retrofit: Retrofit

    private constructor() {
        retrofit = initRetrofit()
    }

    private fun initRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DEFAULT_HOST)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(build())
            .build()
    }


    private fun build(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (level != HttpLoggingInterceptor.Level.NONE) {
            var logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = level
            builder.addInterceptor(logInterceptor)
        }
        interceptors?.map {
            builder.addInterceptor(it)
        }
        builder.callTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        //设置从主机读信息超时
        builder.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        //设置写信息超时
        builder.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        //设置出现错误进行重新连接
        builder.retryOnConnectionFailure(true)
        //10M cache
        builder.cache(Cache(BasicApplication.appContext.cacheDir, 10 * 1024 * 1024))
        return builder.build()
    }

    fun <T> create(service: Class<T>): T = retrofit.create(service)
}