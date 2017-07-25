package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.NewWordData;
import com.onyx.android.dr.interfaces.NewWordView;
import com.onyx.android.dr.request.local.NewWordDelete;
import com.onyx.android.dr.request.local.NewWordQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class NewWordPresenter {
    private final NewWordData newWordData;
    private NewWordView newWordView;
    private Context context;

    public NewWordPresenter(Context context, NewWordView newWordView) {
        this.newWordView = newWordView;
        this.context = context;
        newWordData = new NewWordData();
    }

    public void getAllNewWordData() {
        final NewWordQueryAll req = new NewWordQueryAll();
        newWordData.getAllNewWord(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                newWordView.setNewWordData(req.getNewWordList());
            }
        });
    }

    public void deleteNewWord(long time) {
        final NewWordDelete req = new NewWordDelete(time, true);
        newWordData.deleteNewWord(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
