package com.onyx.android.sdk.data.request.cloud.v2;

import android.net.Uri;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

/**
 * Created by suicheng on 2017/6/1.
 */
public class AccountLoadFromLocalRequest<T extends NeoAccountBase> extends BaseCloudRequest {

    private Uri providerUri;
    private T account;

    private Class<T> clazzType;

    public AccountLoadFromLocalRequest(Uri providerUri, Class<T> clazz) {
        this.providerUri = providerUri;
        this.clazzType = clazz;
    }

    public T getAccount() {
        return account;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        account = ContentUtils.querySingle(providerUri, clazzType, ConditionGroup.clause(), null);
        NeoAccountBase.parseName(account);
    }
}
