package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.GoodSentenceBean;
import com.onyx.android.dr.bean.NewWordBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.DictFunctionConfig;
import com.onyx.android.dr.data.DictTypeConfig;
import com.onyx.android.dr.data.GoodSentenceData;
import com.onyx.android.dr.data.NewWordData;
import com.onyx.android.dr.data.QueryRecordData;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.data.database.QueryRecordEntity;
import com.onyx.android.dr.interfaces.DictResultShowView;
import com.onyx.android.dr.request.local.GoodSentenceExcerptInsert;
import com.onyx.android.dr.request.local.NewWordInsert;
import com.onyx.android.dr.request.local.QueryRecordInsert;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/7/18.
 */
public class DictFunctionPresenter {
    private final DictFunctionConfig functionConfig;
    private final DictTypeConfig dictTypeConfig;
    private final QueryRecordData queryRecordData;
    private final NewWordData newWordData;
    private GoodSentenceData goodSentenceData;
    private Context context;
    private DictResultShowView dictView;

    public DictFunctionPresenter(Context context, DictResultShowView dictView) {
        this.dictView = dictView;
        this.context = context;
        functionConfig = new DictFunctionConfig();
        dictTypeConfig = new DictTypeConfig();
        queryRecordData = new QueryRecordData();
        newWordData = new NewWordData();
        goodSentenceData = new GoodSentenceData();
    }

    public void loadData(Context context) {
        functionConfig.loadDictInfo(context);
        dictTypeConfig.loadDictInfo(context);
    }

    public void loadTabMenu(int userType) {
        dictView.setDictResultData(functionConfig.getDictData(userType));
    }

    public void loadDictType(int userType) {
        dictView.setDictTypeData(dictTypeConfig.getDictTypeData(userType));
    }

    public void insertQueryRecord(String word, long time) {
        QueryRecordEntity bean = new QueryRecordEntity();
        bean.word = word;
        bean.time = time;
        final QueryRecordInsert req = new QueryRecordInsert(bean);
        queryRecordData.insertQueryRecord(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
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

    public void insertGoodSentence(GoodSentenceBean goodSentenceBean) {
        GoodSentenceNoteEntity bean = new GoodSentenceNoteEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.details = goodSentenceBean.getDetails();
        bean.readingMatter = goodSentenceBean.getReadingMatter();
        bean.pageNumber = goodSentenceBean.getPageNumber();
        GoodSentenceExcerptInsert req = new GoodSentenceExcerptInsert(bean);
        if (req.whetherInsert()){
            CommonNotices.showMessage(context, context.getString(R.string.good_sentence_already_exist));
        }else{
            CommonNotices.showMessage(context, context.getString(R.string.already_add_good_sentence_notebook));
        }
        goodSentenceData.insertGoodSentence(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
