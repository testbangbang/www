package com.onyx.android.sdk.rx;

/**
 * Created by john on 29/10/2017.
 */

public abstract class RxCallback<T>  {

    public abstract void onNext(T t);

    public void onError(Throwable throwable) {}

    public void onComplete() {
    }

    public void onSubscribe() {
    }

    public void onFinally() {
    }

    public static <T> void invokeNext(RxCallback<T> callback, T t) {
        if (callback != null) {
            callback.onNext(t);
        }
    }

    public static void invokeError(RxCallback callback, Throwable e) {
        if (callback != null) {
            callback.onError(e);
        }
    }

    public static void invokeSubscribe(RxCallback callback) {
        if (callback != null) {
            callback.onSubscribe();
        }
    }

    public static void invokeComplete(RxCallback callback) {
        if (callback != null) {
            callback.onComplete();
        }
    }

    public static void invokeFinally(RxCallback callback) {
        if (callback != null) {
            callback.onFinally();
        }
    }
}
