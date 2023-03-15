package com.hao.netcommon.net

import androidx.lifecycle.LifecycleOwner
import com.hao.netcommon.retrofit.RestService
import com.hao.netcommon.retrofit.RetrofitWrap
import com.hao.netcommon.utils.CryptoUtils
import com.hao.netcommon.utils.ServerTime
import io.reactivex.disposables.CompositeDisposable
import okhttp3.Interceptor
import okhttp3.Response
import java.lang.reflect.Type

/**
 *@author raohaohao
 *@data 2023/3/10
 *@version 1.0
 */
class HttpGetCreate(
    mCompositeSubscription: CompositeDisposable,
    url: String,
    options: MutableMap<String, Any>?,
    owner: LifecycleOwner? = null,
) : HttpCreate(mCompositeSubscription, url, owner) {

    private var options: MutableMap<String, Any>? = options

    override fun createRestService(what: Int, classType: Type) {
        if (options == null) {
            options = mutableMapOf()
        }
        options!!["nonce"] = getNonce(DEFAULT_LENGTH)

        var sb = StringBuilder()
        var urlPickSb = StringBuilder()

        sb.append("kVl55eO1n3DZhWC8Z7")

        options?.map {
            sb.append(it.key)
            sb.append(it.value)
            urlPickSb.append(it.key)
            urlPickSb.append("=")
            urlPickSb.append(it.value)
            urlPickSb.append("&")
        }

        var timeStamp = ServerTime.getDefault().timeStamp
        sb.append(timeStamp)
        var sign = CryptoUtils.genMD5Str(sb.toString())
        var getUrl = url + "?" + urlPickSb.toString() + "sign=" + sign

        val flowable = RetrofitWrap.getInstance().create(RestService::class.java)
            .get(getUrl, timeStamp.toString())
        Transformer.requestActual(flowable,
            resourceSubscriber,
            mCompositeSubscription,
            what,
            classType)
    }


    override fun clearSubField() {
        options?.clear()
    }
}