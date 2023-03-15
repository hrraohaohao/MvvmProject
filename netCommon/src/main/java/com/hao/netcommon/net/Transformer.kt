package com.hao.netcommon.net

import android.hardware.biometrics.BiometricManager.Strings
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hao.netcommon.ResBaseModel
import com.hao.netcommon.enums.EnumErrorMsg
import com.hao.netcommon.utils.ServerTime
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.reactivestreams.Publisher
import java.lang.reflect.Type
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber

/**
 *@author raohaohao
 *@data 2023/3/10
 *@version 1.0
 */
object Transformer {
    var gson: Gson = GsonBuilder().enableComplexMapKeySerialization().create()

    fun requestActual(
        flowable: Flowable<out Any?>,
        subscriber: ResourceSubscriber<Any>,
        mCompositeSubscription: CompositeDisposable?,
        what: Int,
        classType: Type,
    ): Flowable<out Any?> {
        flowable.flatMap(flatMap(mCompositeSubscription, what, classType))
            .compose(flowableTransformer())
            .subscribeWith(subscriber)
        return flowable
    }


    private fun flowableTransformer(): FlowableTransformer<in Any, out Any>? {
        return FlowableTransformer<Any?, Any> { upstream ->
            upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        }
    }

    /**
     * 解析数据
     */
    private fun flatMap(
        mCompositeSubscription: CompositeDisposable?,
        what: Int,
        classType: Type,
    ): Function<Any?, out Publisher<out Any>>? {
        return object : Function<Any?, Publisher<Any>> {
            override fun apply(t: Any): Publisher<Any> {
                if (t is ResBaseModel<*>) {
                    if (!isNotDetach(mCompositeSubscription)) {
                        return Flowable.error(ServerException(EnumErrorMsg.EHttpRequestFinishied,
                            0,
                            "",
                            ""))
                    }
                    if (t.now > 0) {
                        ServerTime.getDefault().syncTimeStamp(t.now)
                    }
                    if (t.isSuccess && t.data != null) {
                        if (classType == String::class.java) {
                            return Flowable.just(t.data.toString())
                        }
                        var result = gson.toJson(t.data)
                        if (!TextUtils.isEmpty(result)) {
                            return Flowable.just(gson.fromJson(result, classType));
                        }
                        return Flowable.error(Exception("response's model is null"));
                    } else {
                        return Flowable.error(ServerException(what, t.code, t.message, t.data));
                    }
                }
                return Flowable.error(Exception("error data !"));
            }
        }
    }

    fun isNotDetach(mCompositeSubscription: CompositeDisposable?): Boolean {
        return mCompositeSubscription != null && mCompositeSubscription.size() > 0 && !mCompositeSubscription.isDisposed
    }

}


