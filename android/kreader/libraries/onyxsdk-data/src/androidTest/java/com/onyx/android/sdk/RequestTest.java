package com.onyx.android.sdk;

import android.app.Application;
import android.os.Looper;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by suicheng on 2017/4/8.
 */

public class RequestTest extends ApplicationTestCase<Application> {
    private static String[] IDENTIFIER;

    class TestRequest extends BaseDataRequest {

        @Override
        public void execute(DataManager dataManager) throws Exception {
            long sleepTime = TestUtils.randInt(2 * 1000, 6 * 1000);
            sleep(sleepTime);
        }
    }

    public RequestTest() {
        super(Application.class);
        IDENTIFIER = TestUtils.defaultContentTypes().toArray(new String[0]);
    }

    private final String getRandomIdentifier(int position) {
        String identifier = IDENTIFIER[position % IDENTIFIER.length];
        if (identifier.equals("zip")) {
            identifier = null;
        }
        return identifier;
    }

    public void testRequestQueueEmpty() {
        Debug.setDebug(true);

        final DataManager dataManager = new DataManager();
        int roundCount = TestUtils.randInt(15, 22);
        for (int r = 0; r < roundCount; r++) {
            final int round = r;
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final AtomicInteger atomicInteger = new AtomicInteger(0);
            final int requestCount = TestUtils.randInt(60, 80);
            for (int i = 0; i < requestCount; i++) {
                final int position = i;
                TestRequest testRequest = new TestRequest() {

                    public final String getIdentifier() {
                        return getRandomIdentifier(position);
                    }

                };
                BaseCallback baseCallback = new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        assertNull(e);
                        Log.i("###done,round:" + round, "requestIndex:" + position);
                        if (atomicInteger.incrementAndGet() == requestCount) {
                            countDownLatch.countDown();
                        }
                    }
                };
                if (TestUtils.randInt(0, 10) % 2 == 0) {
                    dataManager.submitToMulti(getContext(), testRequest, baseCallback);
                } else {
                    dataManager.submit(getContext(), testRequest, baseCallback);
                }
            }
            awaitCountDownLatch(countDownLatch);
            assertTrue(dataManager.getRequestManager().isAllQueueEmpty());
            Log.e("###done,round:" + round, "isAllQueueEmpty:true");
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void awaitCountDownLatch(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static public class MyRxRequest {

        private int value = 0;
        public MyRxRequest() {
        }

        public MyRxRequest(int v) {
            value = v;
        }

        public void execute() throws Exception {
            if (value < 0) {
                throw new Exception("error");
            }
            assertTrue(Thread.currentThread() != Looper.getMainLooper().getThread());
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    static public class RxManager {

        public static Observable<MyRxRequest> create(final MyRxRequest request) {
            io.reactivex.Observable<MyRxRequest> observable = io.reactivex.Observable.fromCallable(new Callable<MyRxRequest>() {
                @Override
                public MyRxRequest call() throws Exception {
                    request.execute();
                    return request;
                }
            });
            return observable;
        }

        public void enqueue(final MyRxRequest request, final RxCallback<MyRxRequest> callback) {
            create(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(new Action() {
                        @Override
                        public void run() throws Exception {

                        }})
                    .subscribe(new Consumer<MyRxRequest>() {
                                   @Override
                                   public void accept(MyRxRequest o) throws Exception {
                                       callback.onNext(request);
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       callback.onError(throwable);
                                   }
                               }, new Action() {
                                   @Override
                                   public void run() throws Exception {
                                        callback.onComplete();
                                   }
                               });
        }

        public void concat(final List<MyRxRequest> requests,
                           final RxCallback<MyRxRequest> callback) {
            final List<Observable<MyRxRequest>> list = new ArrayList<>();
            for(MyRxRequest rxRequest : requests) {
                list.add(create(rxRequest));
            }
            Observable.concat(list)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(new Action() {
                        @Override
                        public void run() throws Exception {

                        }})
                    .subscribe(new Consumer<MyRxRequest>() {
                        @Override
                        public void accept(MyRxRequest o) throws Exception {
                            callback.onNext(o);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            callback.onError(throwable);
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            callback.onComplete();
                        }
                    });
        }



    }

    public void test1RxRequestMap() {
        final MyRxRequest myRxRequest = new MyRxRequest();
        io.reactivex.Observable<MyRxRequest> observable = io.reactivex.Observable.fromCallable(new Callable<MyRxRequest>() {
            @Override
            public MyRxRequest call() throws Exception {
                myRxRequest.execute();
                return myRxRequest;
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MyRxRequest>() {
                    @Override
                    public void accept(MyRxRequest o) throws Exception {
                        assertTrue(Thread.currentThread() == Looper.getMainLooper().getThread());
                    }
                });
    }

    public void test2RxRequestMap() {
        final List<Integer> valueList = new ArrayList<>();
        valueList.add(100);
        valueList.add(200);
        valueList.add(-100);
        valueList.add(300);
        final List<MyRxRequest> requestList = new ArrayList<>();
        for(Integer value : valueList) {
            requestList.add(new MyRxRequest(value));
        }

        final RxManager rxManager = new RxManager();
        rxManager.concat(requestList, new RxCallback<MyRxRequest>() {
            @Override
            public void onNext(MyRxRequest request) {
                int index = valueList.indexOf(request.getValue());
                assertTrue(index == 0);
                valueList.remove(index);
            }

            @Override
            public void onComplete() {
                assertTrue(valueList.size() > 0);
            }

            public void onError(Throwable throwable) {
                assertTrue(valueList.size() == 2);
            }
        });
    }
}
