package com.onyx.android.sdk.reader.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.reader.plugins.netnovel.NetNovelLocation;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class PagePositionUtils {

    public static float getProgress(int currentPage, int totalPage) {
        return ((currentPage + 1) / (float)totalPage) * 100;
    }

    public static int getPageNumber(final String pageName) {
        return Integer.parseInt(pageName);
    }

    /**
     * increase page number with 1 for display
     * @param pageName
     * @return
     */
    public static String getPageNumberForDisplay(final String pageName) {
        int page = getPageNumber(pageName);
        return page < 0 ? "" : String.valueOf(page + 1);
    }

    public static String fromPageNumber(int pageNumber) {
        return String.valueOf(pageNumber);
    }

    public static int getPosition(final String position) {
        if (StringUtils.isInteger(position)) {
            return Integer.parseInt(position);
        }

        int pos = getIntegerPositionFromJSON(position);
        if (pos >= 0) {
            return pos;
        }

        return 0;
    }

    public static String fromPosition(int position) {
        return String.valueOf(position);
    }

    public static boolean isValidPosition(final String position) {
        return StringUtils.isNotBlank(position);
    }

    private static int getIntegerPositionFromJSON(String json) {
        if (StringUtils.isNullOrEmpty(json)) {
            return -1;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            if (jsonObject.containsKey("type")) {
                String type = jsonObject.getString("type");
                if (type.compareTo("netnovel") == 0) {
                    NetNovelLocation location = NetNovelLocation.createFromJSON(json);
                    if (location != null) {
                        return location.toIntegerPosition();
                    }
                }
            }
        } catch (Throwable tr) {
        }
        return -1;
    }

}
