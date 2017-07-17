package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.NewWordData;
import com.onyx.android.dr.data.QueryRecordData;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.interfaces.QueryRecordView;
import com.onyx.android.dr.request.local.NewWordInsert;
import com.onyx.android.dr.request.local.QueryRecordQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class QueryRecordPresenter {
    private final QueryRecordData queryRecordData;
    private QueryRecordView queryRecordView;
    private NewWordData newWordData;
    private Context context;

    public QueryRecordPresenter(Context context, QueryRecordView queryRecordView) {
        this.queryRecordView = queryRecordView;
        this.context = context;
        queryRecordData = new QueryRecordData();
        newWordData = new NewWordData();
    }

    public void getAllQueryRecordData() {
        final QueryRecordQueryAll req = new QueryRecordQueryAll();
        queryRecordData.getAllQueryRecord(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                queryRecordView.setQueryRecordData(req.getList());
            }
        });
    }

    public void insertNewWord(String month, String week, String day, String newWord, String dictionaryLookup, String readingMatter) {
        NewWordNoteBookEntity bean = new NewWordNoteBookEntity();
        bean.week = week;
        bean.month = month;
        bean.day = day;
        bean.newWord = newWord;
        bean.dictionaryLookup = dictionaryLookup;
        bean.readingMatter = readingMatter;
        final NewWordInsert req = new NewWordInsert(bean);
        newWordData.insertNewWord(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
