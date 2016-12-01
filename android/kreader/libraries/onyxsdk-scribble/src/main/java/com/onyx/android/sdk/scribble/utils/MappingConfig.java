package com.onyx.android.sdk.scribble.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.BuildConfig;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 10/15/16.
 */

public class MappingConfig {

    static private MappingConfig globalInstance;
    static private String TAG = MappingConfig.class.getSimpleName();

    public static class MappingEntry {
        public int orientation;
        public int tx;
        public int ty;
        public int epd;
        public int etx;
        public int ety;
    }

    public static class PressureEntry {
        public int pressure;
        public float ratio;
    }

    public static class NoteMapping {
        public List<PressureEntry> pressureList;
        public List<MappingEntry> mappingList;
    }

    private Map<String, NoteMapping> noteMapping;

    public static MappingConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            globalInstance = new MappingConfig(context, null);
        }
        return globalInstance;
    }

    public static MappingConfig sharedInstance(Context context, final String prefix) {
        if (globalInstance == null) {
            globalInstance = new MappingConfig(context, prefix);
        }
        return globalInstance;
    }

    public MappingEntry getEntry(int offset) {
        final String model = Build.MODEL.toLowerCase();
        if (noteMapping.containsKey(model)) {
            List<MappingEntry> list = noteMapping.get(model).mappingList;
            return getEntry(list, offset);
        }
        for(Map.Entry<String, NoteMapping> entry : noteMapping.entrySet()) {
            if (model.startsWith(entry.getKey())) {
                return getEntry(entry.getValue().mappingList, offset);
            }
        }
        return null;
    }

    private MappingEntry getEntry(final List<MappingEntry> list, int offset) {
        return list.get(offset % list.size());
    }

    private MappingConfig(Context context, final String prefix) {
        noteMapping = objectFromRawResource(context, prefix + "_" + "mapping");
    }

    private Map<String, NoteMapping> objectFromRawResource(Context context, final String name) {
        Map<String, NoteMapping> object = null;
        try {
            int res = context.getResources().getIdentifier(name.toLowerCase(), "raw", context.getPackageName());
            final String value = RawResourceUtil.contentOfRawResource(context, res);
            object = JSON.parseObject(value, new TypeReference<Map<String, NoteMapping>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return object;
        }
    }

}
