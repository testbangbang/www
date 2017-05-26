package com.onyx.android.sdk.data.model.v2;

/**
 * Created by suicheng on 2017/5/18.
 */
public class ContentAuthAccount {
    public String username;
    public String password;

    public static ContentAuthAccount create(String username, String password) {
        ContentAuthAccount account = new ContentAuthAccount();
        account.username = username;
        account.password = password;
        return account;
    }
}
