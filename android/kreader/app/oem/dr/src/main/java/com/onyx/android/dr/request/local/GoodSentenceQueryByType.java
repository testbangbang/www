package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class GoodSentenceQueryByType extends BaseDataRequest {
    private List<GoodSentenceNoteEntity> goodSentenceList;
    private ArrayList<Boolean> listCheck = new ArrayList<>();
    private int type;

    public GoodSentenceQueryByType(int type) {
        this.type = type;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryGoodSentenceList();
    }

    public List<GoodSentenceNoteEntity> getGoodSentenceList() {
        return goodSentenceList;
    }

    public void setGoodSentenceList(List<GoodSentenceNoteEntity> goodSentenceList) {
        this.goodSentenceList = goodSentenceList;
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    public void queryGoodSentenceList() {
        List<GoodSentenceNoteEntity> goodSentenceList = new Select().from(GoodSentenceNoteEntity.class).
                where(GoodSentenceNoteEntity_Table.goodSentenceType.eq(type)).orderBy(GoodSentenceNoteEntity_Table.currentTime, false).queryList();
        if (goodSentenceList != null && goodSentenceList.size() > 0) {
            setGoodSentenceList(goodSentenceList);
        }
        listCheck.clear();
        for (int i = 0; i < goodSentenceList.size(); i++) {
            listCheck.add(false);
        }
    }
}
