package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.ExitGroupData;
import com.onyx.android.dr.interfaces.ExitGroupView;
import com.onyx.android.dr.request.cloud.ExitGroupRequest;
import com.onyx.android.dr.request.cloud.RequestAllGroup;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class ExitGroupPresenter {
    private ExitGroupView exitGroupView;
    private ExitGroupData exitGroupData;

    public ExitGroupPresenter(ExitGroupView exitGroupView) {
        this.exitGroupView = exitGroupView;
        exitGroupData = new ExitGroupData();
    }

    public void getAllGroup() {
        final RequestAllGroup req = new RequestAllGroup();
        exitGroupData.requestAllGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ArrayList<Boolean> checkList = req.getCheckList();
                exitGroupView.setAllGroupResult(req.getGroup(), checkList);
            }
        });
    }

    public void exitGroup() {
        final ExitGroupRequest req = new ExitGroupRequest();
        exitGroupData.exitGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                exitGroupView.setExitGroupResult(req.getResult() != null);
            }
        });
    }
}
