package com.hao.commom.application

import android.app.Application

/**
 *@author raohaohao
 *@data 2023/3/10
 *@version 1.0
 */
open class BasicApplication : Application() {

    companion object {
        lateinit var appContext: Application
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

}