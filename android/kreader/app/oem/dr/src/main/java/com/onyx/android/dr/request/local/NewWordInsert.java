package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class NewWordInsert extends BaseDataRequest {
    private NewWordNoteBookEntity newWordsInfo;
    private boolean weatherInsert = true;
    private NewWordNoteBookEntity newWordEntity;

    public NewWordInsert(NewWordNoteBookEntity newWordNoteBookEntity) {
        this.newWordsInfo = newWordNoteBookEntity;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        insertData();
    }

    private void insertData() {
        if (!whetherInsert()) {
            newWordsInfo.insert();
        } else {
            newWordEntity.currentTime = newWordsInfo.currentTime;
            newWordEntity.dictionaryLookup = newWordsInfo.dictionaryLookup;
            newWordEntity.readingMatter = newWordsInfo.readingMatter;
            newWordEntity.newWordType = newWordsInfo.newWordType;
            newWordEntity.update();
        }
    }

    public boolean whetherInsert() {
        List<NewWordNoteBookEntity> dataList = queryNewWordList();
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                newWordEntity = dataList.get(i);
                if (newWordsInfo.newWord.equals(newWordEntity.newWord)) {
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

    public List<NewWordNoteBookEntity> queryNewWordList() {
        List<NewWordNoteBookEntity> newWordList = new Select().from(NewWordNoteBookEntity.class).queryList();
        return newWordList;
    }
}
