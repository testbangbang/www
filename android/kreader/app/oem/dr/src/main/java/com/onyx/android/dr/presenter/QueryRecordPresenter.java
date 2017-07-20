package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.NewWordBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.NewWordData;
import com.onyx.android.dr.data.QueryRecordData;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.interfaces.QueryRecordView;
import com.onyx.android.dr.request.local.NewWordInsert;
import com.onyx.android.dr.request.local.QueryRecordQueryAll;
import com.onyx.android.dr.util.TimeUtils;
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

    public void insertNewWord(NewWordBean newWordBean) {
        NewWordNoteBookEntity bean = new NewWordNoteBookEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.newWord = newWordBean.getNewWord();
        bean.dictionaryLookup = newWordBean.getDictionaryLookup();
        bean.readingMatter = newWordBean.getReadingMatter();
        final NewWordInsert req = new NewWordInsert(bean);
        if (req.whetherInsert()){
            CommonNotices.showMessage(context, context.getString(R.string.new_word_notebook_already_exist));
        }else{
            CommonNotices.showMessage(context, context.getString(R.string.already_add_new_word_notebook));
        }
        newWordData.insertNewWord(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
