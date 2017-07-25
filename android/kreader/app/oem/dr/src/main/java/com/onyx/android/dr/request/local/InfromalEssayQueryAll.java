package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.InfromalEssayEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class InfromalEssayQueryAll extends BaseDataRequest {
    private List<InfromalEssayEntity> infromalEssayList;

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryInfromalEssayList();
    }

    public List<InfromalEssayEntity> getAllDatas() {
        return infromalEssayList;
    }

    public void setAllDatas(List<InfromalEssayEntity> infromalEssayList) {
        this.infromalEssayList = infromalEssayList;
    }

    public void queryInfromalEssayList() {
        List<InfromalEssayEntity> infromalEssayList = new Select().from(InfromalEssayEntity.class).queryList();
        if (infromalEssayList != null && infromalEssayList.size() > 0) {
            setAllDatas(infromalEssayList);
        }
    }
}
