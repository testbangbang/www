package com.onyx.android.sdk.data.request.cloud.v2;

import android.net.Uri;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

/**
 * Created by suicheng on 2017/5/31.
 */
public class AccountSaveToLocalRequest extends BaseCloudRequest {

    private Uri providerUri;
    private NeoAccountBase neoAccountBase;

    public AccountSaveToLocalRequest(Uri providerUri, NeoAccountBase neoAccountBase) {
        this.providerUri = providerUri;
        this.neoAccountBase = neoAccountBase;
    }

    public void setNeoAccountBase(NeoAccountBase neoAccountBase) {
        this.neoAccountBase = neoAccountBase;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        FlowManager.getContext().getContentResolver().delete(providerUri, null, null);
        ContentUtils.insert(providerUri, neoAccountBase);
    }

    public NeoAccountBase getNeoAccountBase() {
        return neoAccountBase;
    }
}
