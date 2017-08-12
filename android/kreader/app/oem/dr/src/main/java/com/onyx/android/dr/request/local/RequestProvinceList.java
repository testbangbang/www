package com.onyx.android.dr.request.local;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.onyx.android.dr.bean.ProvinceBean;
import com.onyx.android.dr.data.database.AddressDBHelper;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-8-1.
 */

public class RequestProvinceList extends BaseDataRequest {

    private List<ProvinceBean> provinces;
    private List<String> provinceNames;

    public List<String> getProvinceNames() {
        return provinceNames;
    }

    public List<ProvinceBean> getProvinces() {
        return provinces;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        queryProvinces();
        if (provinces != null) {
            provinceNames = new ArrayList<>();
            for (ProvinceBean bean : provinces) {
                provinceNames.add(bean.proName);
            }
        }
    }

    private List<ProvinceBean> queryProvinces() {
        provinces = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new AddressDBHelper(getContext()).getReadableDatabase();
            cursor = db.query("t_province", new String[]{"ProName", "ProSort", "ProRemark"}, null, null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                ProvinceBean provinceBean = new ProvinceBean();
                provinceBean.proName = cursor.getString(0);
                provinceBean.proSort = cursor.getString(1);
                provinceBean.proRemark = cursor.getString(2);
                provinces.add(provinceBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.closeQuietly(cursor);
            Utils.closeQuietly(db);
        }
        return provinces;
    }
}
