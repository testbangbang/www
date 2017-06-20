package com.onyx.android.sdk.data.model.v2;

import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.data.converter.ListStringConverter;
import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.Column;

import java.util.Date;
import java.util.List;

/**
 * Created by suicheng on 2017/5/18.
 */
public class NeoAccountBase extends BaseData {

    public static final String DELIMITER = ",";

    public String name;
    public String orgName;
    @Column(typeConverter = ListStringConverter.class)
    public List<String> groups;

    @Column
    public String token;
    @Column
    public long tokenExpiresIn; //unit s
    public String phone;

    public String info;
    public String library;

    @JSONField(serialize = false, deserialize = false)
    public String getFirstGroup() {
        if(CollectionUtils.isNullOrEmpty(groups)) {
            return "";
        }
        return groups.get(0).replaceAll(DELIMITER, "");
    }

    public String getPhone() {
        return StringUtils.getBlankStr(phone);
    }

    public String getName() {
        return StringUtils.getBlankStr(name);
    }

    @JSONField(serialize = false, deserialize = false)
    public static boolean isValid(NeoAccountBase account) {
        return account != null && StringUtils.isNotBlank(account.token);
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isTokenTimeExpired() {
        if (getCreatedAt() == null) {
            return true;
        }
        return getCreatedAt().getTime() + getTokenExpiresIn() < new Date().getTime();
    }

    public long getTokenExpiresIn() {
        return tokenExpiresIn * 1000;
    }

    public static void parseName(NeoAccountBase account) {
        if (account != null) {
            AccountCommon common = JSONObjectParseUtils.parseObject(account.info, AccountCommon.class);
            if (common != null) {
                account.name = common.name;
            }
        }
    }
}
