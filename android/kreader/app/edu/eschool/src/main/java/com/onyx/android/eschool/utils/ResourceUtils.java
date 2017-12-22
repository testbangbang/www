package com.onyx.android.eschool.utils;

import android.content.Context;

import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2016/11/23.
 */

public class ResourceUtils {

    static public int getResourceIdByName(Context context, final String resourceType, final String resourceName) {
        if (StringUtils.isNotBlank(resourceName)) {
            String packageName = context.getPackageName();
            return context.getResources().getIdentifier(resourceName, resourceType, packageName);
        }
        return 0;
    }

    static public int getStringResIdByName(Context context, final String resourceName) {
        return getResourceIdByName(context, "string", resourceName);
    }

    static public int getDrawableResIdByName(Context context, final String resourceName) {
        return getResourceIdByName(context, "drawable", resourceName);
    }

    static public int getMipmapResIdByName(Context context, final String resourceName) {
        return getResourceIdByName(context, "mipmap", resourceName);
    }

    static public int getLayoutResIdByName(Context context, final String resourceName) {
        return getResourceIdByName(context, "layout", resourceName);
    }

    static public int getIdByName(Context context, final String resourceName) {
        return getResourceIdByName(context, "id", resourceName);
    }
}
