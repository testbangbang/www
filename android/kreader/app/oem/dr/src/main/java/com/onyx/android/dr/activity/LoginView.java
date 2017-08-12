package com.onyx.android.dr.activity;

import com.onyx.android.dr.bean.CityBean;
import com.onyx.android.dr.bean.InterestBean;
import com.onyx.android.dr.bean.ProvinceBean;
import com.onyx.android.sdk.data.model.v2.GroupBean;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;

import java.util.List;

/**
 * Created by hehai on 17-6-30.
 */

public interface LoginView {
    void setAccountInfo(NeoAccountBase accountInfo);

    void setGroups(List<GroupBean> groups);

    void setInterestList(List<InterestBean> interestList);

    void setSignUpResult(boolean result);

    void setProvince(List<String> provinceNames, List<ProvinceBean> provinces);

    void setCitys(List<String> cityNames, List<CityBean> citys);

    void setZone(List<String> zoneNames);
}
