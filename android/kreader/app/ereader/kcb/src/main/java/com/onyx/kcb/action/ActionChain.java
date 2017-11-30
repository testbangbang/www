package com.onyx.kcb.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.kcb.holder.DataBundle;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by suicheng on 2017/5/18.
 */

public class ActionChain<T extends BaseAction> {
    private List<T> observableList = new ArrayList<>();

    public void addAction(final T action) {
        observableList.add(action);
    }

    public void execute(final DataBundle dataBundle, final RxCallback<T> callback) {
        Observable<T> observable = Observable.fromIterable(observableList);
        observable.observeOn(observeScheduler())
                .subscribeOn(subscribeScheduler())
                .subscribe(new Consumer<T>() {
            @Override
            public void accept(T t) throws Exception {
                t.execute(dataBundle, null);
                if (callback != null) {
                    callback.onNext(t);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (callback != null) {
                    callback.onError(throwable);
                }
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                if (callback != null) {
                    callback.onComplete();
                }
            }
        });
    }

    public Scheduler observeScheduler() {
        return AndroidSchedulers.mainThread();
    }

    public Scheduler subscribeScheduler() {
        return Schedulers.newThread();
    }
}
