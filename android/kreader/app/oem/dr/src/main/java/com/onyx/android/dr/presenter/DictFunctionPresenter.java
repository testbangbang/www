package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.data.DictFunctionConfig;
import com.onyx.android.dr.data.DictTypeConfig;
import com.onyx.android.dr.data.QueryRecordData;
import com.onyx.android.dr.data.database.QueryRecordEntity;
import com.onyx.android.dr.interfaces.DictResultShowView;
import com.onyx.android.dr.request.local.QueryRecordInsert;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/7/18.
 */
public class DictFunctionPresenter {
    private final DictFunctionConfig functionConfig;
    private final DictTypeConfig dictTypeConfig;
    private final QueryRecordData queryRecordData;
    private DictResultShowView dictView;

    public DictFunctionPresenter(DictResultShowView dictView) {
        this.dictView = dictView;
        functionConfig = new DictFunctionConfig();
        dictTypeConfig = new DictTypeConfig();
        queryRecordData = new QueryRecordData();
    }

    public void loadData(Context context) {
        functionConfig.loadDictInfo(context);
        dictTypeConfig.loadDictInfo(context);
    }

    public void loadDictType(int userType) {
        dictView.setDictTypeData(dictTypeConfig.getDictTypeData(userType));
    }

    public void insertQueryRecord(QueryRecordEntity bean) {
        final QueryRecordInsert req = new QueryRecordInsert(bean);
        queryRecordData.insertQueryRecord(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
