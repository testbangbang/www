package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class MemorandumInsert extends BaseDataRequest {
    private MemorandumEntity memorandumInfo;
    private boolean weatherInsert = true;
    private MemorandumEntity memorandumEntity;

    public MemorandumInsert(MemorandumEntity memorandumEntity) {
        this.memorandumInfo = memorandumEntity;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        insertData();
    }

    private void insertData() {
        if (!whetherInsert()) {
            memorandumInfo.insert();
        } else {
            memorandumEntity.currentTime = memorandumInfo.currentTime;
            memorandumEntity.matter = memorandumInfo.matter;
            memorandumEntity.update();
        }
    }

    public boolean whetherInsert() {
        List<MemorandumEntity> dataList = queryNewWordList();
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                memorandumEntity = dataList.get(i);
                if (memorandumInfo.getDate().equals(memorandumEntity.date)) {
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

    public List<MemorandumEntity> queryNewWordList() {
        List<MemorandumEntity> memorandumList = new Select().from(MemorandumEntity.class).queryList();
        return memorandumList;
    }
}
