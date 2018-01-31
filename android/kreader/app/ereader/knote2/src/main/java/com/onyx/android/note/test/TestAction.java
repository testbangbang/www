package com.onyx.android.note.test;

import android.support.annotation.NonNull;

import com.onyx.android.note.utils.RxManagerUtils;
import com.onyx.android.sdk.rx.RxAction;
import com.onyx.android.sdk.rx.RxCallback;



/**
 * Created by lxm on 2018/1/31.
 */

public class TestAction extends RxAction<TestRequest> {

    private TestViewModel viewModel;

    public TestAction(TestViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void execute(final RxCallback<TestRequest> rxCallback) {
        RxManagerUtils.enqueue(new TestRequest()).subscribe(new RxCallback<TestRequest>() {
            @Override
            public void onNext(@NonNull TestRequest testRequest) {
                viewModel.setText("TestAction Success");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                viewModel.setText("TestAction Failed");
            }
        });
    }
}
