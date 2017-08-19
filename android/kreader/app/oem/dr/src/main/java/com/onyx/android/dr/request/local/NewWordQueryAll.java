package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
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
public class NewWordQueryAll extends BaseDataRequest {
    private List<NewWordNoteBookEntity> newWordList;
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryNewWordList();
    }

    public List<NewWordNoteBookEntity> getNewWordList() {
        SortClass sort = new SortClass();
        Collections.sort(newWordList, sort);
        return newWordList;
    }

    public void setNewWordList(List<NewWordNoteBookEntity> newWordList) {
        this.newWordList = newWordList;
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    public void queryNewWordList() {
        List<NewWordNoteBookEntity> newWordList = new Select().from(NewWordNoteBookEntity.class).queryList();
        if (newWordList != null && newWordList.size() > 0) {
            setNewWordList(newWordList);
        }
        listCheck.clear();
        for (int i = 0; i < newWordList.size(); i++) {
            listCheck.add(false);
        }
    }
}
