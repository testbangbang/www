package com.onyx.android.dr.request.local;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.onyx.android.dr.bean.CityBean;
import com.onyx.android.dr.data.database.AddressDBHelper;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-8-1.
 */

public class RequestCityList extends BaseDataRequest {

    private List<CityBean> citys;
    private List<String> cityNames;
    private String proID;

    public List<CityBean> getCitys() {
        return citys;
    }

    public List<String> getCityNames() {
        return cityNames;
    }

    public RequestCityList(String proID) {
        this.proID = proID;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        queryCitys();
        if (citys != null) {
            cityNames = new ArrayList<>();
            for (CityBean bean : citys) {
                cityNames.add(bean.cityName);
            }
        }
    }

    private List<CityBean> queryCitys() {
        citys = new ArrayList<>();
        SQLiteDatabase db = new AddressDBHelper(getContext()).getReadableDatabase();
        Cursor cursor = db.query("t_city", new String[]{"CityName", "CitySort", "ProID"}, "ProID=?", new String[]{proID}, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            CityBean cityBean = new CityBean();
            cityBean.cityName = cursor.getString(0);
            cityBean.citySort = cursor.getString(1);
            cityBean.proID = cursor.getString(2);
            citys.add(cityBean);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return citys;
    }
}
