package com.onyx.android.sdk.rx;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by john on 3/12/2017.
 */

public class RxRequestChain {

    private List<RxRequest> requestList = new ArrayList<>();

    public List<RxRequest> getRequestList() {
        return requestList;
    }

    public void add(final RxRequest request) {
        requestList.add(request);
    }

    public void execute(final RxCallback<RxRequest> callback) {
        final List<Observable<RxRequest>> list = new ArrayList<>();
        for(RxRequest rxRequest : requestList) {
            list.add(rxRequest.createObservable());
        }
        Observable observable = Observable.concat(list);
        observable
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<RxRequest>() {
                @Override
                public void accept(RxRequest request) throws Exception {
                    RxCallback.invokeNext(callback, request);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    RxCallback.invokeError(callback, throwable);
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                    RxCallback.invokeComplete(callback);
                }
            }, new Consumer<Disposable>() {
                @Override
                public void accept(Disposable disposable) throws Exception {
                    RxCallback.invokeSubscribe(callback);
                }
            });
    }
}
