package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.action.AuthTokenAction;
import com.onyx.android.dr.action.CloudLibraryListLoadAction;
import com.onyx.android.dr.activity.MainView;
import com.onyx.android.dr.data.MainData;
import com.onyx.android.dr.data.MainTabMenuConfig;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;

import java.util.List;

/**
 * Created by hehai on 17-6-28.
 */

public class MainPresenter {
    private MainView mainView;
    private MainData mainData;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        mainData = new MainData();
    }

    public void loadData(Context context) {
        MainTabMenuConfig.loadMenuInfo(context);
    }

    public void loadTabMenu(String userType) {
        mainView.setTabMenuData(mainData.loadTabMenu(userType));
    }

    public void lookCloudLibraryList(String libraryParentId) {
        AuthTokenAction authTokenAction = new AuthTokenAction();
        final CloudLibraryListLoadAction loadAction = new CloudLibraryListLoadAction(libraryParentId);
        mainData.lookCloudLibraryList(authTokenAction, loadAction, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<Library> libraryList = loadAction.getLibraryList();
                if (libraryList != null) {
                    mainView.setLibraryList(libraryList);
                }
            }
        });
    }
}
