package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.activity.MainView;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.data.MainData;
import com.onyx.android.dr.data.ReadingRateData;
import com.onyx.android.dr.request.cloud.RequestGetMyGroup;
import com.onyx.android.dr.request.cloud.RequestIndexServiceAndLogin;
import com.onyx.android.dr.request.local.ReadingRateQueryAll;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.GroupBean;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

/**
 * Created by hehai on 17-6-28.
 */

public class MainPresenter {
    private MainView mainView;
    private MainData mainData;
    private ReadingRateData readingRateData;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        mainData = new MainData();
    }

    public void loadTabMenu(String userType) {
        mainView.setTabMenuData(mainData.loadTabMenu(userType));
        readingRateData = new ReadingRateData();
    }

    public void authToken(Context context) {
        String userAccount = DRPreferenceManager.getUserAccount(DRApplication.getInstance(), "");
        String password = DRPreferenceManager.getUserPassword(DRApplication.getInstance(), "");
        if (StringUtils.isNullOrEmpty(userAccount) || StringUtils.isNullOrEmpty(password)) {
            ActivityManager.startLoginActivity(context);
            return;
        }
        final BaseAuthAccount neoAccountBase = BaseAuthAccount.create(userAccount, password);
        final RequestIndexServiceAndLogin req = new RequestIndexServiceAndLogin(neoAccountBase);
        mainData.login(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

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

    public void getAllReadingRateData() {
        final ReadingRateQueryAll req = new ReadingRateQueryAll(readingRateData);
        readingRateData.getAllReadingRate(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
