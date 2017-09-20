package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.action.AuthTokenAction;
import com.onyx.android.dr.activity.MainView;
import com.onyx.android.dr.data.MainData;
import com.onyx.android.dr.event.LoginFailedEvent;
import com.onyx.android.dr.request.cloud.RequestGetMyGroup;
import com.onyx.android.dr.request.local.RequestLoadLocalDB;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.GroupBean;

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
                    String parentId = groups.get(0)._id;
                    DRPreferenceManager.saveLibraryParentId(DRApplication.getInstance(), library);
                    DRPreferenceManager.saveParentId(DRApplication.getInstance(), parentId);
                    DRPreferenceManager.saveUserType(DRApplication.getInstance(), groups.get(0).name);
                }
            }
        });
    }
}