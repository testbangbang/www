package com.onyx.android.sdk.data.model.v2;

import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2017/5/18.
 */

public class BaseAuthAccount extends NeoAccountBase {
    public String username;
    public String password;

    public static BaseAuthAccount create(String username, String password) {
        BaseAuthAccount account = new BaseAuthAccount();
        account.username = username;
        account.password = password;
        return account;
    }
}
