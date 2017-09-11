package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.data.database.InformalEssayEntity_Table;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class InformalEssayQueryByTitle extends BaseDataRequest {
    private final String informalEssayTitle;
    private List<InformalEssayEntity> informalEssayList = new ArrayList<>();

    public InformalEssayQueryByTitle(String title) {
        this.informalEssayTitle = title;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryInformalEssayList();
    }

    public List<InformalEssayEntity> getData() {
        return informalEssayList;
    }

    public void queryInformalEssayList() {
        List<InformalEssayEntity> essayList = new Select().from(InformalEssayEntity.class).orderBy(InformalEssayEntity_Table.title, false).queryList();
        if (essayList != null && essayList.size() > 0) {
            for (int i = 0; i < essayList.size(); i++) {
                InformalEssayEntity informalEssayEntity = essayList.get(i);
                boolean tag = Utils.stringIsContain(informalEssayEntity.title, informalEssayTitle);
                if (tag) {
                    if (!informalEssayList.contains(informalEssayEntity)){
                        informalEssayList.add(informalEssayEntity);
                    }
                }
            }
        }
    }
}
