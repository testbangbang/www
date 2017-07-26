package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class InformalEssayQueryAll extends BaseDataRequest {
    private List<InformalEssayEntity> informalEssayList;
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryInformalEssayList();
    }

    public List<InformalEssayEntity> getAllDatas() {
        return informalEssayList;
    }

    public void setAllDatas(List<InformalEssayEntity> informalEssayList) {
        this.informalEssayList = informalEssayList;
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    public void queryInformalEssayList() {
        List<InformalEssayEntity> essayList = new Select().from(InformalEssayEntity.class).queryList();
        if (essayList != null && essayList.size() > 0) {
            setAllDatas(essayList);
            listCheck.clear();
            for (int i = 0; i < essayList.size(); i++) {
                listCheck.add(false);
            }
        }
    }
}
