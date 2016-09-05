package com.onyx.android.sdk;

import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.UUID;

/**
 * Created by zhuzeng on 9/4/16.
 */
public class AccountUtils {

    private static OnyxAccount currentAccount;

    public static OnyxAccount getCurrentAccount() {
        if (currentAccount == null) {
            currentAccount = new OnyxAccount(UUID.randomUUID().toString(),
                    TestUtils.randString(), TestUtils.randomEmail());
        }
        return currentAccount;
    }

    public static OnyxAccount generateRandomAccount() {
        return new OnyxAccount(UUID.randomUUID().toString(),
                TestUtils.randString(), TestUtils.randomEmail());
    }
}
