package com.onyx.android.dr.request.local;

import android.util.Log;

import com.onyx.android.dr.data.database.InfromalEssayEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class InfromalEssayInsert extends BaseDataRequest {
    private InfromalEssayEntity infromalEssayInfo;
    private boolean weatherInsert = true;
    private InfromalEssayEntity infromalEssayEntity;

    public InfromalEssayInsert(InfromalEssayEntity infromalEssayEntity) {
        this.infromalEssayInfo = infromalEssayEntity;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        insertData();
    }

    private void insertData() {
        if (!whetherInsert()) {
            infromalEssayInfo.insert();
        } else {
            infromalEssayEntity.currentTime = infromalEssayInfo.currentTime;
            infromalEssayEntity.title = infromalEssayInfo.title;
            infromalEssayEntity.wordNumber = infromalEssayInfo.wordNumber;
            infromalEssayEntity.update();
        }
    }

    public boolean whetherInsert() {
        List<InfromalEssayEntity> dataList = queryInfromalEssayList();
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                infromalEssayEntity = dataList.get(i);
                if (infromalEssayInfo.content.equals(infromalEssayEntity.content)) {
                    weatherInsert = false;
                    return true;
                }
            }
            if (weatherInsert) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public List<InfromalEssayEntity> queryInfromalEssayList() {
        List<InfromalEssayEntity> infromalEssayList = new Select().from(InfromalEssayEntity.class).queryList();
        return infromalEssayList;
    }
}
