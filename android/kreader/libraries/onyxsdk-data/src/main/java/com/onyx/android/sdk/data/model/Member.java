package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.converter.AccountConverter;
import com.onyx.android.sdk.data.db.OnyxCloudDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by suicheng on 2016/9/20.
 */
@Table(database = OnyxCloudDatabase.class)
public class Member extends BaseData {

    @Column(typeConverter = AccountConverter.class)
    public OnyxAccount account;
    @Column
    public String groupNickname;
    @Column
    public int role;

    public Member() {
    }

    public Member(String nickname) {
        this.groupNickname = nickname;
    }
}
