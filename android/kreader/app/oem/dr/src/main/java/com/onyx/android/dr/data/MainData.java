package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.action.ActionChain;
import com.onyx.android.dr.action.AuthTokenAction;
import com.onyx.android.dr.action.CloudLibraryListLoadAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;

import java.util.List;

/**
 * Created by hehai on 17-6-28.
 */

public class MainData {
    public void lookCloudLibraryList(AuthTokenAction authTokenAction, CloudLibraryListLoadAction loadAction, BaseCallback baseCallback) {
        ActionChain actionChain = new ActionChain();
        actionChain.addAction(authTokenAction);
        actionChain.addAction(loadAction);
        actionChain.execute(DRApplication.getLibraryDataHolder(), baseCallback);
    }

    public List<MenuData> loadTabMenu(String userType) {
        return MainTabMenuConfig.getMenuData(userType);
    }
}
