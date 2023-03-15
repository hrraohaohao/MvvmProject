package com.hao.netcommon.interfaces;

public interface MultSubscriber {
    void onNext(int what, int which1, int which2, int which3, Object t);

    void onError(int what, int which1, int which2, int which3, int code, Throwable throwable);

    void onComplete(int what, int which1, int which2, int which3);
}
