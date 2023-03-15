package com.hao.mvvmpproject;

import com.hao.commom.application.BasicApplication;
import com.hao.netcommon.retrofit.RetrofitWrap;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author raohaohao
 * @version 1.0
 * @data 2023/3/10
 */
public class BaseApplication extends BasicApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitWrap.DEFAULT_HOST = "https://pos-stage.zhuizhikeji.com";
        RetrofitWrap.level = BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE;
    }

}
