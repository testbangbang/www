package com.onyx.android.sdk.reader.plugins.netnovel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 3/19/18.
 */

public class NetNovelVolume {
    public String id;
    public String title;
    public List<NetNovelChapter> chapters;

    private NetNovelVolume() {

    }

    public static List<NetNovelVolume> createFromJSONArray(JSONArray jsonArray) {
        List<NetNovelVolume> volumes = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            NetNovelVolume volume = createFromJSON(jsonArray.getJSONObject(i));
            if (volume != null) {
                volumes.add(volume);
            }
        }
        return volumes;
    }

    public static NetNovelVolume createFromJSON(JSONObject jsonObject) {
        String id = jsonObject.getString("id");
        if (StringUtils.isNullOrEmpty(id)) {
            return null;
        }
        String title = jsonObject.getString("title");

        JSONArray jsonArray = jsonObject.getJSONArray("chapters");
        if (jsonArray == null) {
            return null;
        }
        List<NetNovelChapter> chapters = NetNovelChapter.createFromJSONArray(jsonArray);

        NetNovelVolume volume = new NetNovelVolume();
        volume.id = id;
        volume.title = title;
        volume.chapters = chapters;

        return volume;
    }
}
