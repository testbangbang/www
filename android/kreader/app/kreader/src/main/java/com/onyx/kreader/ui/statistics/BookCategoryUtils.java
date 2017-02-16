package com.onyx.kreader.ui.statistics;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.reader.common.Debug;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ming on 2017/2/16.
 */

public class BookCategoryUtils {

    public static Map<String, String> getCategoryMap(final Context context) {
        Map<String, String> categoryMap = new HashMap<>();
        String content = RawResourceUtil.contentFromRawResource(context, "book_category");
        if (!StringUtils.isNullOrEmpty(content)) {
            categoryMap = JSON.parseObject(content, new TypeReference<Map<String, String>>() {});
        }
        return categoryMap;
    }

    public static BookCategory getBookCategory(final Map<String, String> categoryMap, final String categoryId) {
        BookCategory category = BookCategory.unknown;
        String value = categoryMap.get(categoryId);
        if (!StringUtils.isNullOrEmpty(value)) {
            category = BookCategory.valueOf(value);
        }
        return category;
    }

    public static String getCategoryName(final Context context, final BookCategory category) {
        switch (category) {
            case science:
                return context.getString(R.string.science);
            case art:
                return context.getString(R.string.art);
            case social_science:
                return context.getString(R.string.social_science);
            case life:
                return context.getString(R.string.life);
            case education:
                return context.getString(R.string.education);
            case magazine:
                return context.getString(R.string.magazine);
            case fiction:
                return context.getString(R.string.fiction);
            case finance_and_economics:
                return context.getString(R.string.finance_and_economics);
            case children_book:
                return context.getString(R.string.children_book);
            case unknown:
                return context.getString(R.string.other);
        }
        return context.getString(R.string.other);
    }

}
