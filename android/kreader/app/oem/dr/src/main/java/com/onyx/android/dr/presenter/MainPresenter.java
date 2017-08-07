package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.action.AuthTokenAction;
import com.onyx.android.dr.activity.MainView;
import com.onyx.android.dr.data.MainData;
import com.onyx.android.dr.data.MainTabMenuConfig;
import com.onyx.android.dr.event.LoginFailedEvent;
import com.onyx.android.dr.request.cloud.RequestGetMyGroup;
import com.onyx.android.dr.request.local.RequestLoadLocalDB;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.v2.GroupBean;
import com.onyx.android.sdk.data.request.cloud.v2.CloudChildLibraryListLoadRequest;

import org.greenrobot.eventbus.EventBus;

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
        RequestLoadLocalDB req = new RequestLoadLocalDB();
        mainData.loadLocalDB(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

    public void loadTabMenu(String userType) {
        mainView.setTabMenuData(mainData.loadTabMenu(userType));
    }

    public void authToken() {
        AuthTokenAction authTokenAction = new AuthTokenAction();
        mainData.authToken(authTokenAction, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    EventBus.getDefault().post(new LoginFailedEvent());
                }
            }
        });

    }

    public void getMyGroup() {
        final RequestGetMyGroup req = new RequestGetMyGroup();
        mainData.getMyGroup(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<GroupBean> groups = req.getGroups();
                if (groups != null && groups.size() > 0) {
                    String library = groups.get(0).library;
                    DRPreferenceManager.saveLibraryParentId(DRApplication.getInstance(), library);
                    getLibraryList(library);
                }
            }
        });
    }

    private void getLibraryList(String library) {
        final CloudChildLibraryListLoadRequest req = new CloudChildLibraryListLoadRequest(library);
        mainData.getLibraryList(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<Library> libraryList = req.getLibraryList();
                mainView.setTabMenuData(mainData.loadTabMenu(libraryList));
                mainView.setLibraryList(libraryList);
            }
        });
    }
}
