package com.onyx.edu.manager.model;

import android.content.Intent;

/**
 * Created by suicheng on 2017/7/6.
 */

public class FuncItemEntity {
    public int labelIconRes;
    public String labelText;

    public Intent intent;

    public static FuncItemEntity create(int iconRes, String labelText, Intent intent) {
        FuncItemEntity itemEntity = new FuncItemEntity();
        itemEntity.labelIconRes = iconRes;
        itemEntity.labelText = labelText;
        itemEntity.intent = intent;
        return itemEntity;
    }
}
