package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.DictFunctionConfig;
import com.onyx.android.dr.data.DictTypeConfig;
import com.onyx.android.dr.data.QueryRecordData;
import com.onyx.android.dr.data.database.QueryRecordEntity;
import com.onyx.android.dr.interfaces.DictResultShowView;
import com.onyx.android.dr.request.local.QueryRecordInsert;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by hehai on 17-6-28.
 */

public class DictFunctionPresenter {
    private final DictFunctionConfig functionConfig;
    private final DictTypeConfig dictTypeConfig;
    private final QueryRecordData queryRecordData;
    private Context context;
    private DictResultShowView dictView;

    public DictFunctionPresenter(Context context, DictResultShowView dictView) {
        this.dictView = dictView;
        this.context = context;
        functionConfig = new DictFunctionConfig();
        dictTypeConfig = new DictTypeConfig();
        queryRecordData = new QueryRecordData();
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
}
