package com.onyx.android.sdk.data.model.v2;


import com.onyx.android.sdk.data.db.AccountDatabase;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2017/5/31.
 */
@Table(database = AccountDatabase.class, allFields = true)
public class EduAccount extends NeoAccountBase {

}
