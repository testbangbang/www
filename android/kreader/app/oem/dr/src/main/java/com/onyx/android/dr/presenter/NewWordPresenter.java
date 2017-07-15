package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.NewWordData;
import com.onyx.android.dr.interfaces.NewWordView;
import com.onyx.android.dr.request.local.NewWordQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class NewWordPresenter {
    private final NewWordData newWordData;
    private NewWordView newWordView;

    public NewWordPresenter(NewWordView newWordView) {
        this.newWordView = newWordView;
        newWordData = new NewWordData();
    }

    public void getAllNewWordData() {
        final NewWordQueryAll req = new NewWordQueryAll();
        newWordData.getAllNewWord(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                newWordView.setNewWordData(req.getNewWordList());
            }
        });
    }
}
