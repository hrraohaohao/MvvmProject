package com.hao.netcommon.net

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.google.gson.JsonSyntaxException
import com.hao.commom.application.BasicApplication
import com.hao.netcommon.enums.EnumErrorMsg
import com.hao.netcommon.interfaces.MultSubscriber
import com.hao.netcommon.retrofit.RetrofitWrap
import com.hao.netcommon.utils.NetWorkUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONException
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.reflect.Type
import java.net.*
import javax.net.ssl.SSLHandshakeException

/**
 *@author raohaohao
 *@data 2023/3/10
 *@version 1.0
 */
abstract class HttpCreate(
    var mCompositeSubscription: CompositeDisposable?,
    var url: String?,
    var owner: LifecycleOwner?,
) {

    companion object {
        const val nonceStr = "0123456789abcdefghiklmnopqrstuvwxyz"
        val TAG: String = HttpCreate::class.java.name
    }

    val DEFAULT_LENGTH = 20
    lateinit var resourceSubscriber: ResourceSubscriber<Any>
    var retryCount = 0

    fun getNonce(length: Int): String {
        return (1..length).map {
            val random = nonceStr.random()
            random
        }.joinToString("")
    }

    @JvmOverloads
    fun subscribe(
        subscriber: MultSubscriber?,
        what: Int,
        which1: Int = 0,
        which2: Int = 0,
        which3: Int = 0,
        classType: Type,
    ) {
        var resourceSubscriber = object : ResourceSubscriber<Any>() {

            override fun onNext(t: Any?) {
                if (mCompositeSubscription?.let { Transformer.isNotDetach(it) } == true) {
                    subscriber?.onNext(what, which1, which2, which3,t)
                }
                onComplete()
            }

            override fun onError(t: Throwable?) {
                var msgWhat: Int
                when (t) {
                    //连接服务器错误
                    is ConnectException -> {
                        msgWhat = EnumErrorMsg.EHttpIO_Msg;
                    }
                    //socket链接超时
                    is SocketTimeoutException -> {
                        msgWhat = EnumErrorMsg.EConnectTimeout_Msg;
                    }
                    //找不到Host主体
                    is UnknownHostException -> {
                        if (NetWorkUtil.isNetWorkActive(BasicApplication.appContext)) {
                            msgWhat = EnumErrorMsg.EMobileNetUseless_Msg;
                        } else {
                            msgWhat = EnumErrorMsg.EUnknownHost_msg;
                        }
                    }
                    //SSL签名错误
                    is SSLHandshakeException -> {
                        msgWhat = EnumErrorMsg.EHttpRequestSSLHandshakeException;
                    }
                    //解析出错
                    is JsonSyntaxException -> {
                        msgWhat = EnumErrorMsg.EJsonParser_Msg;
                    }
                    //解析出错
                    is JSONException -> {
                        msgWhat = EnumErrorMsg.EJsonParser_Msg;
                    }
                    // 网络协议错误
                    is MalformedURLException -> {
                        msgWhat = EnumErrorMsg.EHttpProtocol_Msg;
                    }
                    // 服务端出错
                    is UnknownServiceException -> {
                        msgWhat = EnumErrorMsg.EUnknownService_msg;
                    }
                    // 通信编码错误
                    is UnsupportedEncodingException -> {
                        msgWhat = EnumErrorMsg.EUnsupportedEncoding_msg;
                    }
                    //服务器开小差
                    is IOException -> {
                        msgWhat = EnumErrorMsg.EHttpIO_Msg;
                    }
                    //服务器响应错误
                    is ServerException -> {
                        msgWhat = t.code
                        if (t.code == 100058) {
                            retryCount++
                            if (retryCount == 2) {
                                Log.e(TAG, "code：100058 请求重试")
                                subscribe(subscriber, what, which1, which2, which3, classType)
                                return
                            }
                        }
                    }
                    else -> {
                        msgWhat = EnumErrorMsg.ENotDefine_Msg;
                    }
                }
                subscriber?.onError(what, which1, which2, which3, msgWhat, t)
                onComplete()
            }

            override fun onComplete() {
                if (mCompositeSubscription?.let { Transformer.isNotDetach(it) } == true) {
                    subscriber?.onComplete(what, which1, which2, which3);
                }
                clearField()
            }

        }
        this.resourceSubscriber = resourceSubscriber
        mCompositeSubscription?.add(resourceSubscriber)
        return createRestService(what, classType)
    }

    private fun clearField() {
        mCompositeSubscription?.remove(resourceSubscriber)
        url = null
        clearSubField()
    }

    open fun clearSubField() {}

    abstract fun createRestService(what: Int, classType: Type)
}