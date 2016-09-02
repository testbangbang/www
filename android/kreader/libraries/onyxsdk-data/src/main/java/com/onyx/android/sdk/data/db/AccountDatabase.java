package com.onyx.android.sdk.data.db;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.model.SecurePreferences;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 9/2/16.
 */
public class AccountDatabase {

    private static final String CLOUD_TYPE = "onyxCloud";
    private static final String ACCOUNT_INFO_TAG = "accountInfo";
    private static final String JSON_TAG = "json";

    private LinkedHashMap<String, OnyxAccount> accountMap = new LinkedHashMap(16,0.75F,true);
    private OnyxAccount currentAccount;

    // always use the last one as current account, since it's added to end of list.
    public OnyxAccount getCurrentAccount() {
        if (currentAccount == null) {
            if (accountMap.size() > 0) {
                Iterator<Map.Entry<String,OnyxAccount>> iterator = accountMap.entrySet().iterator();
                for(int i=0;i<accountMap.size()-1;i++){
                    iterator.next();
                }
                currentAccount = iterator.next().getValue();
            }
        }
        return currentAccount;
    }

    public void addAccount(final OnyxAccount account) {
        accountMap.put(account.type, account);
        currentAccount = account;
    }

    public void clear() {
        accountMap.clear();
        currentAccount = null;
    }

    public void saveAccount(final Context context) {
        SecurePreferences preferences = new SecurePreferences(context, CLOUD_TYPE, ACCOUNT_INFO_TAG, true);
        preferences.put(JSON_TAG, JSON.toJSONString(accountMap));
    }

    public OnyxAccount loadAccount(final Context context) {
        SecurePreferences preferences = new SecurePreferences(context, CLOUD_TYPE, ACCOUNT_INFO_TAG, true);
        final String string = preferences.getString(JSON_TAG);
        if (StringUtils.isNullOrEmpty(string)) {
            return null;
        }
        accountMap = JSON.parseObject(string, new TypeReference<LinkedHashMap<String, OnyxAccount>>(){});
        return getCurrentAccount();
    }

    public void clear(final Context context) {
        SecurePreferences preferences = new SecurePreferences(context, CLOUD_TYPE, ACCOUNT_INFO_TAG, true);
        preferences.clear();
        accountMap.clear();
    }


}
