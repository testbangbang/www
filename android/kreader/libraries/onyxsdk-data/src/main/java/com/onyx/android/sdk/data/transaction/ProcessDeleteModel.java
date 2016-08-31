package com.onyx.android.sdk.data.transaction;

import com.onyx.android.sdk.data.model.BaseData;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;

/**
 * Created by suicheng on 2016/8/12.
 */
public class ProcessDeleteModel<T extends BaseData> implements ProcessModelTransaction.ProcessModel<T> {

    @Override
    public void processModel(T model) {
        model.delete();
    }
}