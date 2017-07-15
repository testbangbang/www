package com.onyx.android.dr.request.local;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
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

    public NewWordInsert(NewWordNoteBookEntity newWordNoteBookEntity) {
        this.newWordsInfo = newWordNoteBookEntity;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        List<NewWordNoteBookEntity> queryRecordList = queryNewWordList();
        if (queryRecordList != null && queryRecordList.size() > 0) {
            for (int i = 0; i < queryRecordList.size(); i++) {
                NewWordNoteBookEntity newWordEntity = queryRecordList.get(i);
                if (newWordsInfo.newWord.equals(newWordEntity.newWord)) {
                    newWordEntity.week = newWordsInfo.week;
                    newWordEntity.month = newWordsInfo.month;
                    newWordEntity.day = newWordsInfo.day;
                    newWordEntity.dictionaryLookup = newWordsInfo.dictionaryLookup;
                    newWordEntity.readingMatter = newWordsInfo.readingMatter;
                    newWordEntity.update();
                    weatherInsert = false;
                    CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getResources().getString(R.string.new_word_notebook_already_exist));
                    break;
                }
            }
            if (weatherInsert){
                newWordsInfo.insert();
                CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getResources().getString(R.string.already_add_new_word_notebook));
            }
        } else {
            newWordsInfo.insert();
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getResources().getString(R.string.already_add_new_word_notebook));
        }
    }

    public List<NewWordNoteBookEntity> queryNewWordList() {
        List<NewWordNoteBookEntity> newWordList = new Select().from(NewWordNoteBookEntity.class).queryList();
        return newWordList;
    }
}
