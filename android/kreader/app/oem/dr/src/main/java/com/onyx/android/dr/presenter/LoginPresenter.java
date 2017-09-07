package com.onyx.android.dr.presenter;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.activity.LoginView;
import com.onyx.android.dr.bean.SignUpInfo;
import com.onyx.android.dr.data.LoginData;
import com.onyx.android.dr.request.cloud.LoginByAdminRequest;
import com.onyx.android.dr.request.cloud.RequestGetRootGroupList;
import com.onyx.android.dr.request.cloud.SignUpRequest;
import com.onyx.android.dr.request.local.RequestCityList;
import com.onyx.android.dr.request.local.RequestProvinceList;
import com.onyx.android.dr.request.local.RequestZoneList;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;

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

    public void login(String userName, String password, final boolean autoLogin) {
        final BaseAuthAccount neoAccountBase = BaseAuthAccount.create(userName, password);
        final LoginByAdminRequest req = new LoginByAdminRequest(neoAccountBase);
        loginData.login(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                loginView.setAccountInfo(req.getNeoAccount());
                if (req.getNeoAccount() != null && autoLogin) {
                    DRPreferenceManager.saveUserAccount(DRApplication.getInstance(), neoAccountBase.username);
                    DRPreferenceManager.saveUserPassword(DRApplication.getInstance(), neoAccountBase.password);
                }
            }
        });
    }

    public void getRootGroups() {
        final RequestGetRootGroupList req = new RequestGetRootGroupList();
        loginData.getRootGroups(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                loginView.setGroups(req.getGroups());
            }
        });
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
}
