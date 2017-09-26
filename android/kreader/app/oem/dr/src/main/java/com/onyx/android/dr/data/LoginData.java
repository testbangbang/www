package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.CityBean;
import com.onyx.android.dr.bean.InterestBean;
import com.onyx.android.dr.bean.ProvinceBean;
import com.onyx.android.dr.bean.ZoneBean;
import com.onyx.android.dr.request.cloud.RequestGetMyGroup;
import com.onyx.android.dr.request.cloud.RequestGetRootGroupList;
import com.onyx.android.dr.request.cloud.RequestIndexServiceAndLogin;
import com.onyx.android.dr.request.cloud.SignUpRequest;
import com.onyx.android.dr.request.local.RequestCityList;
import com.onyx.android.dr.request.local.RequestLoadLocalDB;
import com.onyx.android.dr.request.local.RequestProvinceList;
import com.onyx.android.dr.request.local.RequestZoneList;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-6-30.
 */

public class LoginData {

    private List<ProvinceBean> provinces;
    private List<CityBean> citys;
    private List<ZoneBean> zones;

    public void login(RequestIndexServiceAndLogin request, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), request, callback);
    }

    public void getRootGroups(RequestGetRootGroupList req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public List<InterestBean> getInterestList() {
        List<InterestBean> list = new ArrayList<>();
        String[] stringArray = DRApplication.getInstance().getResources().getStringArray(R.array.Interest);
        for (String s : stringArray) {
            InterestBean interestBean = new InterestBean(s, false);
            list.add(interestBean);
        }
        return list;
    }

    public void signUp(SignUpRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void queryProvince(final RequestProvinceList req, final BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                provinces = req.getProvinces();
                invoke(baseCallback, req, e);
            }
        });
    }

    public void queryCity(final RequestCityList req, final BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                citys = req.getCitys();
                invoke(baseCallback, req, e);
            }
        });
    }

    public void queryZone(final RequestZoneList req, final BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                zones = req.getZone();
                invoke(baseCallback, req, e);
            }
        });
    }

    public void loadLocalDB(RequestLoadLocalDB req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }

    public void getMyGroup(RequestGetMyGroup req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
