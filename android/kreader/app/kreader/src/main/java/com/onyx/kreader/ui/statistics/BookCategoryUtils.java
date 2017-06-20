package com.onyx.kreader.ui.statistics;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;

import java.util.HashMap;
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
        BookCategory category = BookCategory.UNKNOWN;
        String value = categoryMap.get(categoryId);
        if (!StringUtils.isNullOrEmpty(value)) {
            category = BookCategory.valueOf(value);
        }
        return category;
    }

    public static String getCategoryName(final Context context, final BookCategory category) {
        switch (category) {
            case SCIENCE:
                return context.getString(R.string.science);
            case ART:
                return context.getString(R.string.art);
            case SOCIAL_SCIENCE:
                return context.getString(R.string.social_science);
            case LIFE:
                return context.getString(R.string.life);
            case EDUCATION:
                return context.getString(R.string.education);
            case MAGAZINE:
                return context.getString(R.string.magazine);
            case FICTION:
                return context.getString(R.string.fiction);
            case FINANCE_AND_ECONOMICS:
                return context.getString(R.string.finance_and_economics);
            case CHILDREN_BOOK:
                return context.getString(R.string.children_book);
            case UNKNOWN:
                return context.getString(R.string.other);
        }
        return context.getString(R.string.other);
    }

}
