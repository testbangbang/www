package com.onyx.kreader.reflow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.api.ReaderBitmapList;
import com.onyx.kreader.common.Debug;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

/**
 * Created by joy on 10/10/16.
 */
public class ReflowedSubPageIndex {

    static private final String INDEX_FILE_NAME = "reflow-index.json";

    private File indexFile;

    private Map<String, ReaderBitmapList> pageListMap;
    private ConcurrentHashMap<String, Object> pageListLock = new ConcurrentHashMap<>();

    private ReflowedSubPageIndex(final File indexFile) {
        this.indexFile = indexFile;
    }

    public static ReflowedSubPageIndex load(File indexFile) {
        ReflowedSubPageIndex index = new ReflowedSubPageIndex(indexFile);
        index.loadSubPageIndex();
        return index;
    }

    public ReaderBitmapList getSubPageList(final String pageName) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                list = new ReaderBitmapList();
                pageListMap.put(pageName, list);
            }
            return list;
        }
    }

    public void clearSubPageList(final String pageName) {
        getSubPageList(pageName).clear();
    }

    public void clearPageMap() {
        synchronized (pageListLock) {
            if (pageListMap == null) {
                return;
            }
            pageListMap.clear();
            saveSubPageIndex();
        }
    }

    public void saveSubPageIndex() {
        synchronized (pageListLock) {
            try {
                File file = indexFile;
                String json = JSON.toJSONString(pageListMap);
                FileUtils.saveContentToFile(json, file);
            } catch (Exception e) {
                Debug.w(TAG, e);
            }
        }
    }

    private void loadSubPageIndex() {
        synchronized (pageListLock) {
            if (pageListMap != null) {
                pageListMap.clear();
            }
            try {
                File file = indexFile;
                if (file != null && FileUtils.fileExist(file.getAbsolutePath())) {
                    String json = FileUtils.readContentOfFile(file);
                    pageListMap = JSON.parseObject(json, new TypeReference<Map<String, ReaderBitmapList>>() {
                    });
                }
            } catch (Exception e) {
                Debug.w(TAG, e);
            }

            if (pageListMap == null) {
                pageListMap = new HashMap<>();
            }
        }
    }
}
