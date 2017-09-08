package com.onyx.einfo.events;

import com.onyx.android.sdk.data.model.v2.NeoAccountBase;

/**
 * Created by suicheng on 2017/5/22.
 */

public class AccountAvailableEvent {
    private NeoAccountBase account;

    public AccountAvailableEvent(final NeoAccountBase info) {
        account = info;
    }

    public NeoAccountBase getAccount() {
        return account;
    }
}
