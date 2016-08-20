package com.onyx.cloud.db.transaction;

import com.onyx.cloud.model.BaseObject;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;

/**
 * Created by suicheng on 2016/8/12.
 */
public class ProcessDeleteModel<T extends BaseObject> implements ProcessModelTransaction.ProcessModel<T> {

    @Override
    public void processModel(T model) {
        model.delete();
    }
}