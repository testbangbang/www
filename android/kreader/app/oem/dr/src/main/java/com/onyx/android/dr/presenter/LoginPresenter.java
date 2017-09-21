package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.activity.LoginView;
import com.onyx.android.dr.bean.SignUpInfo;
import com.onyx.android.dr.data.LoginData;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.manager.LeanCloudManager;
import com.onyx.android.dr.request.cloud.RequestGetRootGroupList;
import com.onyx.android.dr.request.cloud.RequestIndexServiceAndLogin;
import com.onyx.android.dr.request.cloud.SignUpRequest;
import com.onyx.android.dr.request.local.RequestCityList;
import com.onyx.android.dr.request.local.RequestLoadLocalDB;
import com.onyx.android.dr.request.local.RequestProvinceList;
import com.onyx.android.dr.request.local.RequestZoneList;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;
import com.onyx.android.sdk.data.request.cloud.v2.CloudIndexServiceRequest;
import com.onyx.android.sdk.utils.NetworkUtil;

/**
 * Created by hehai on 17-6-30.
 */

public class LoginPresenter {
    private LoginView loginView;
    private LoginData loginData;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
        loginData = new LoginData();
    }

    public void login(String userName, String password) {
        final BaseAuthAccount neoAccountBase = BaseAuthAccount.create(userName, password);
        final RequestIndexServiceAndLogin req = new RequestIndexServiceAndLogin(neoAccountBase);
        loginData.login(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

    public void getRootGroups() {
        final CloudIndexServiceRequest indexServiceRequest = new CloudIndexServiceRequest(DeviceConfig.sharedInstance(DRApplication.getInstance()).getCloudMainIndexServerApi(),
                createIndexService(DRApplication.getInstance()));
        CloudRequestChain requestChain = new CloudRequestChain();
        requestChain.addRequest(indexServiceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DRApplication.getInstance().setHaveIndexService(indexServiceRequest.getResultIndexService() != null);
            }
        });

        final RequestGetRootGroupList req = new RequestGetRootGroupList();
        requestChain.addRequest(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                loginView.setGroups(req.getGroups());
            }
        });

        requestChain.execute(DRApplication.getInstance(), DRApplication.getCloudStore().getCloudManager());
    }

    public void getInterestList() {
        loginView.setInterestList(loginData.getInterestList());
    }

    public void signUp(SignUpInfo signUpInfo) {
        final SignUpRequest req = new SignUpRequest(signUpInfo);
        loginData.signUp(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                loginView.setSignUpResult(req.getAuthToken() != null);
            }
        });
    }

    public void queryProvince() {
        final RequestProvinceList req = new RequestProvinceList();
        loginData.queryProvince(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                loginView.setProvince(req.getProvinceNames(), req.getProvinces());
            }
        });
    }

    public void queryCity(String proID) {
        final RequestCityList req = new RequestCityList(proID);
        loginData.queryCity(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                loginView.setCitys(req.getCityNames(), req.getCitys());
            }
        });
    }

    public void queryZone(String cityID) {
        final RequestZoneList req = new RequestZoneList(cityID);
        loginData.queryZone(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                loginView.setZone(req.getZoneNames());
            }
        });
    }

    private IndexService createIndexService(Context context) {
        IndexService authService = new IndexService();
        authService.mac = NetworkUtil.getMacAddress(context);
        authService.installationId = LeanCloudManager.getInstallationId();
        return authService;
    }

    public void loadDB() {
        RequestLoadLocalDB req = new RequestLoadLocalDB();
        loginData.loadLocalDB(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }
}
