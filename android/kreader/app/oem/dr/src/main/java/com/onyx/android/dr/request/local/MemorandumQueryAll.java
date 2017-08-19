package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.util.SortClass;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class MemorandumQueryAll extends BaseDataRequest {
    private List<MemorandumEntity> memorandumList;
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryMemorandumList();
    }

    public List<MemorandumEntity> getAllDatas() {
        SortClass sort = new SortClass();
        Collections.sort(memorandumList, sort);
        return memorandumList;
    }

    public void setAllDatas(List<MemorandumEntity> memorandumList) {
        this.memorandumList = memorandumList;
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    public void queryMemorandumList() {
        List<MemorandumEntity> memorandumList = new Select().from(MemorandumEntity.class).queryList();
        if (memorandumList != null && memorandumList.size() > 0) {
            setAllDatas(memorandumList);
            listCheck.clear();
            for (int i = 0; i < memorandumList.size(); i++) {
                listCheck.add(false);
            }
        }
    }
}
