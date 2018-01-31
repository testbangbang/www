package com.onyx.android.note.utils;

import com.onyx.android.sdk.rx.RxManager;
import com.onyx.android.sdk.rx.RxRequest;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lxm on 2018/1/31.
 */

public class RxManagerUtils {

    private static RxManager defaultRxManager;

    public static RxManager getDefaultRxManager() {
        if (defaultRxManager == null) {
            defaultRxManager = new RxManager.Builder()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .build();
        }
        return defaultRxManager;
    }

    public static <T extends RxRequest> Observable<T> enqueue(T request) {
        return getDefaultRxManager().enqueue(request);
    }
}
