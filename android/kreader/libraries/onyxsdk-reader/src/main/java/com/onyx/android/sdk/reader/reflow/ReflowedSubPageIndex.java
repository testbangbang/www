package com.onyx.android.sdk.reader.reflow;

import android.graphics.Bitmap;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.reader.api.ReaderBitmapList;
import com.onyx.android.sdk.utils.Debug;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by joy on 10/10/16.
 */
public class ReflowedSubPageIndex {
    private final Class TAG = ReflowedSubPageIndex.class;

    private File indexFile;

    private Map<String, ReaderBitmapList> pageListMap;
    private Set<String> subPageListReflowCompleteCollection = new HashSet<>();
    private Object pageListLock = new Object();

    private ReflowedSubPageIndex(final File indexFile) {
        this.indexFile = indexFile;
        loadSubPageIndex();
    }

    public static ReflowedSubPageIndex load(File indexFile) {
        ReflowedSubPageIndex index = new ReflowedSubPageIndex(indexFile);
        return index;
    }

    public void addSubPageBitmap(final String pageName, final Bitmap bitmap) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                list = new ReaderBitmapList();
                pageListMap.put(pageName, list);
            }
            list.addBitmap(bitmap);
        }
    }

    public void markSubPageListReflowComplete(final String pageName) {
        synchronized (pageListLock) {
            subPageListReflowCompleteCollection.add(pageName);
        }
    }

    public boolean isSubPageListReflowComplete(final String pageName) {
        synchronized (pageListLock) {
            return subPageListReflowCompleteCollection.contains(pageName);
        }
    }

    public int getSubPageCount(final String pageName) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                return 0;
            }
            return list.getCount();
        }
    }

    public int getSubPageCurrentIndex(final String pageName) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                return 0;
            }
            return list.getCurrent();
        }
    }

    public boolean atFirstSubPage(final String pageName) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                return true;
            }
            return list.atBegin();
        }
    }

    public boolean atLastSubPage(final String pageName) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                return true;
            }
            return list.atEnd();
        }
    }

    public void moveToFirstSubPage(final String pageName) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                return;
            }
            list.moveToBegin();
        }
    }

    public void moveToLastSubPage(final String pageName) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                return;
            }
            list.moveToEnd();
        }
    }

    public void previousSubPage(final String pageName) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                return;
            }
            list.prev();
        }
    }

    public void nextSubPage(final String pageName) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                return;
            }
            list.next();
        }
    }

    public void moveToSubPage(final String pageName, final int subPage) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                return;
            }
            list.moveToScreen(subPage);
        }
    }

    public void clearSubPageList(final String pageName) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                return;
            }
            list.clear();
            subPageListReflowCompleteCollection.remove(pageName);
        }
    }

    public void clearPageMap() {
        synchronized (pageListLock) {
            if (pageListMap == null) {
                return;
            }
            pageListMap.clear();
            subPageListReflowCompleteCollection.clear();
            saveSubPageIndex();
        }
    }

    public void saveSubPageIndex() {
        synchronized (pageListLock) {
            try {
                Map<String, ReaderBitmapList> map = new HashMap<>();
                for (String page : subPageListReflowCompleteCollection) {
                    map.put(page, pageListMap.get(page));
                }
                File file = indexFile;
                String json = JSON.toJSONString(map);
                FileUtils.saveContentToFile(json, file);
            } catch (Exception e) {
                Debug.w(TAG, e);
            }
        }
    }

    private void loadSubPageIndex() {
        synchronized (pageListLock) {
            try {
                File file = indexFile;
                if (file != null && FileUtils.fileExist(file.getAbsolutePath())) {
                    String json = FileUtils.readContentOfFile(file);
                    pageListMap = JSON.parseObject(json, new TypeReference<Map<String, ReaderBitmapList>>() {
                    });
                    if (pageListMap != null) {
                        subPageListReflowCompleteCollection.addAll(pageListMap.keySet());
                    }
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
