package com.hao.mvvmpproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.hao.netcommon.net.HttpGetCreate
import com.hao.netcommon.interfaces.MultSubscriber
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    var mCompositeSubscription = CompositeDisposable()

    fun get(view: View) {
        var params = mutableMapOf<String, Any>()
        HttpGetCreate(mCompositeSubscription,
            "https://pos-stage.zhuizhikeji.com/pos/account/getauthcodeimage",
            params).subscribe(subscriber = object :
            MultSubscriber {
            override fun onNext(what: Int, which1: Int, which2: Int, which3: Int, t: Any?) {
                Log.i("onNext", t.toString())
            }

            override fun onError(
                what: Int,
                which1: Int,
                which2: Int,
                which3: Int,
                code: Int,
                throwable: Throwable?,
            ) {
                Log.e("onError", "code：$code  message：${throwable?.message}")
            }

            override fun onComplete(what: Int, which1: Int, which2: Int, which3: Int) {
                Log.i("onComplete", "onComplete")
            }
        }, what = 0, classType = String::class.java)
    }


}