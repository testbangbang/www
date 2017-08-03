package com.onyx.android.dr.request.local;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.onyx.android.dr.bean.ZoneBean;
import com.onyx.android.dr.data.database.AddressDBHelper;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-8-1.
 */

public class RequestZoneList extends BaseDataRequest {
    private String cityID;
    private List<ZoneBean> zones;
    private List<String> zoneNames;

    public RequestZoneList(String cityID) {
        this.cityID = cityID;
    }

    public List<ZoneBean> getZones() {
        return zones;
    }

    public List<String> getZoneNames() {
        return zoneNames;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        getZone();
        if (zones != null) {
            zoneNames = new ArrayList<>();
            for (ZoneBean bean : zones) {
                zoneNames.add(bean.zoneName);
            }
        }
    }

    public List<ZoneBean> getZone() {
        zones = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new AddressDBHelper(getContext()).getReadableDatabase();
            cursor = db.query("t_zone", new String[]{"ZoneName", "CityID", "ZoneID"}, "CityID=?", new String[]{cityID}, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                ZoneBean zoneBean = new ZoneBean();
                zoneBean.zoneName = cursor.getString(0);
                zoneBean.cityID = cursor.getString(1);
                zoneBean.zoneID = cursor.getString(2);
                zones.add(zoneBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.closeQuietly(cursor);
            Utils.closeQuietly(db);
        }
        return zones;
    }
}
