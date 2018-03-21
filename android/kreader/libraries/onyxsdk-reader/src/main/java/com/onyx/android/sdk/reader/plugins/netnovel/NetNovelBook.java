package com.onyx.android.sdk.reader.plugins.netnovel;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 3/19/18.
 */

public class NetNovelBook {
    public boolean has_volume;
    public List<NetNovelVolume> volumes;
    public List<NetNovelChapter> chapters;

    private NetNovelBook() {

    }

    public static NetNovelBook createFromJSON(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        if (!jsonObject.containsKey("has_volume")) {
            return null;
        }
        boolean has_volume = jsonObject.getBooleanValue("has_volume");

        List<NetNovelVolume> volumes = readVolumes(jsonObject);
        List<NetNovelChapter> chapters = readChapters(jsonObject);

        NetNovelBook book = new NetNovelBook();
        book.has_volume = has_volume;
        book.volumes = volumes;
        book.chapters = chapters;

        return book;
    }

    public static List<NetNovelChapter> readChapters(JSONObject jsonObject) {
        List<NetNovelChapter> chapters = new ArrayList<>();
        JSONArray jsonChapters = jsonObject.getJSONArray("chapters");
        if (jsonChapters != null) {
            chapters.addAll(NetNovelChapter.createFromJSONArray(jsonChapters));
        }
        return chapters;
    }

    private static List<NetNovelVolume> readVolumes(JSONObject jsonObject) {
        List<NetNovelVolume> volumes = new ArrayList<>();
        JSONArray jsonVolumes = jsonObject.getJSONArray("volumes");
        if (jsonVolumes != null) {
           volumes.addAll(NetNovelVolume.createFromJSONArray(jsonVolumes));
        }
        return volumes;
    }

    public List<NetNovelChapter> getChapterFlattenList() {
        ArrayList<NetNovelChapter> list = new ArrayList<>();
        for (NetNovelVolume vol : volumes) {
            if (vol.chapters == null) {
                continue;
            }
            for (NetNovelChapter ch : vol.chapters) {
                list.add(ch);
            }
        }
        for (NetNovelChapter ch : chapters) {
            list.add(ch);
        }
        return list;
    }

}
