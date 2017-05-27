package com.onyx.android.eschool.request;

import com.onyx.android.eschool.model.StudentAccount;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;

/**
 * Created by suicheng on 2017/5/26.
 */

public class LoadAccountFromLocalRequest extends BaseCloudRequest {

    private StudentAccount studentAccount;

    @Override
    public void execute(CloudManager parent) throws Exception {
        studentAccount = StudentAccount.loadAccount(getContext());
    }

    public StudentAccount getStudentAccount() {
        return studentAccount;
    }
}
