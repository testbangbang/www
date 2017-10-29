package com.onyx.android.sdk.rx;

/**
 * Created by john on 29/10/2017.
 */

public abstract class RxCallback<T>  {

    public abstract void onNext(T t);

    public void onError(Throwable throwable) {}

    public void onComplete() {
    }

}
