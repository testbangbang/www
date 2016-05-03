package com.onyx.kreader.host.wrapper;

import android.util.Log;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhuzeng on 8/12/14.
 */
public class ReaderCacheManager {

    private Map<String, ReaderBitmapImpl> pageMap = new ConcurrentHashMap<String, ReaderBitmapImpl>();

    public ReaderCacheManager() {
        super();
    }

    public void clear() {
        for(Map.Entry<String, ReaderBitmapImpl> entry: pageMap.entrySet()) {
            entry.getValue().recycleBitmap();
        }
        pageMap.clear();
    }

    public void dumpKeys(final String tag) {
        for(String key : pageMap.keySet()) {
            Log.i(tag, key);
        }
    }

    public ReaderBitmapImpl getBitmap(final String key) {
        return pageMap.get(key);
    }

    public String addBitmap(final String key,  ReaderBitmapImpl bitmap) {
        try {
            ReaderBitmapImpl cache = new ReaderBitmapImpl();
            cache.copyFrom(bitmap);
            pageMap.put(key, cache);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return key;
        }
    }


}
