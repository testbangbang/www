package com.onyx.android.sdk.reader.plugins.netnovel;

import com.alibaba.fastjson.JSON;

/**
 * Created by joy on 3/20/18.
 */

public class NetNovelLocation {
    public String type = "netnovel";
    public String bookId;
    public String chapterId;
    public int chapterIndex;
    public int positionInChapter;

    private NetNovelLocation() {

    }

    public NetNovelLocation(String bookId, String chapterId, int chapterIndex, int positionInChapter) {
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.chapterIndex = chapterIndex;
        this.positionInChapter = positionInChapter;
    }

    public static NetNovelLocation createFromJSON(String json) {
        return JSON.parseObject(json, NetNovelLocation.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public int toIntegerPosition() {
        return (chapterIndex & 0xFFFF) << 16 | (positionInChapter & 0xFFFF);
    }

}
