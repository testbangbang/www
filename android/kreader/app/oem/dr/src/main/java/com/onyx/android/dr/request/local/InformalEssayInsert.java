package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class InformalEssayInsert extends BaseDataRequest {
    private InformalEssayEntity infromalEssayInfo;
    private boolean weatherInsert = true;
    private InformalEssayEntity infromalEssayEntity;

    public InformalEssayInsert(InformalEssayEntity infromalEssayEntity) {
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
            infromalEssayEntity.content = infromalEssayInfo.content;
            infromalEssayEntity.wordNumber = infromalEssayInfo.wordNumber;
            infromalEssayEntity.update();
        }
    }

    public boolean whetherInsert() {
        List<InformalEssayEntity> dataList = queryInfromalEssayList();
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                infromalEssayEntity = dataList.get(i);
                if (infromalEssayInfo.title.equals(infromalEssayEntity.title)) {
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

    public List<InformalEssayEntity> queryInfromalEssayList() {
        List<InformalEssayEntity> infromalEssayList = new Select().from(InformalEssayEntity.class).queryList();
        return infromalEssayList;
    }
}
