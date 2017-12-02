package com.onyx.kcb;

import android.test.ApplicationTestCase;

import com.onyx.android.sdk.utils.TestUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by john on 2/12/2017.
 */

public class RxTest extends ApplicationTestCase<KCBApplication> {


    public RxTest() {
        super(KCBApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testZip() throws Exception {
        final int s1 = 1000;
        final int s2 = 1500;
        Observable<String> first = Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TestUtils.sleep(s1);
                return String.valueOf(s1);
            }
        });

        Observable<String> second = Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TestUtils.sleep(s2);
                return String.valueOf(s2);
            }
        });

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Observable.zip(first, second, new BiFunction<String, String, String>() {
            @Override
            public String apply(String s, String s2) throws Exception {
                return s + s2;
            }})
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    String v = String.valueOf(s1) + String.valueOf(s2);
                    assertEquals(v, s);
                    countDownLatch.countDown();
                }
            });
        countDownLatch.await();
    }
}
