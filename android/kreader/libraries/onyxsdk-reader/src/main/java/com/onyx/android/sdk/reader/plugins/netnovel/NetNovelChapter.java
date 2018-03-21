package com.onyx.android.sdk.reader.plugins.netnovel;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 3/19/18.
 */

public class NetNovelChapter {
    public String id;
    public String title;
    public int words;
    public int vip_flag;
    public float price;
    public boolean buy;

    private NetNovelChapter() {

    }

    public static List<NetNovelChapter> createFromJSONArray(JSONArray jsonArray) {
        List<NetNovelChapter> chapters = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            NetNovelChapter chapter = jsonArray.getObject(i, NetNovelChapter.class);
            if (chapter != null) {
                chapters.add(chapter);
            }
        }
        return chapters;
    }
}
