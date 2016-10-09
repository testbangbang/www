package com.onyx.kreader.reflow;

import android.graphics.Bitmap;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderBitmapList;
import com.onyx.kreader.cache.BitmapDiskLruCache;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.utils.HashUtils;
import com.onyx.kreader.utils.ImageUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 3/27/14
 * Time: 10:33 AM
 * cache root
 *   |--- hashcode of settings
 *               |--- index, settings and page index.
 *               |--- page number - sub page.png
 */
public class ImageReflowManager {

    public static final String TAG = ImageReflowManager.class.getSimpleName();

    static private final String INDEX_FILE_NAME = "reflow-index.json";

    /**
     * we must have space big enough to hold at least one page's reflowed bitmaps
     */
    private static final int MAX_DISK_CACHE_SIZE = 20 * 1024 * 1024;

    private String documentMd5;
    private File cacheRoot;
    private BitmapDiskLruCache bitmapCache;
    private ImageReflowSettings settings;

    private Map<String, ReaderBitmapList> pageListMap;
    private ConcurrentHashMap<String, Object> pageListLock = new ConcurrentHashMap<>();

    private String pageBeingReflowed;
    private Lock reflowLock = new ReentrantLock(); // to be hold for a short time to check if a page is being reflowed or not
    private Condition reflowReadyCondition = reflowLock.newCondition();

    // work as memory cache to avoid extra disk cache io
    private volatile String subPageBeingWaited;
    private volatile Bitmap subPageResultBitmap;

    private ExecutorService reflowExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    });

    public ImageReflowManager(final String documentMd5, final File root, int dw, int dh) {
        super();
        this.documentMd5 = documentMd5;
        cacheRoot = root;
        settings = ImageReflowSettings.createSettings();
        settings.dev_width = dw;
        settings.dev_height = dh;

        bitmapCache = BitmapDiskLruCache.create(root, MAX_DISK_CACHE_SIZE);
    }

    public ImageReflowSettings getSettings() {
        return settings;
    }

    public void updateViewportSize(int dw, int dh) {
        ImageReflowSettings copy = ImageReflowSettings.copy(settings);
        copy.dev_width = dw;
        copy.dev_height = dh;
        updateSettings(copy);
    }

    public void updateSettings(final ImageReflowSettings s) {
        settings.update(s);
        loadSubPageListMap();
    }

    public void reflowBitmapAsync(final Bitmap bitmap, final String pageName) {
        synchronized (pageListLock) {
            if (!pageListLock.containsKey(pageName)) {
                clearSubPageList(pageName);
                pageListLock.put(pageName, new Object());
            }
        }
        setPageBeingReflowedWithLock(pageName);
        reflowExecutor.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (pageListLock.get(pageName)) {
                    if (isReflowed(pageName)) {
                        setPageBeingReflowedWithLock(null);
                        return;
                    }
                    reflow(bitmap, pageName);
                    setPageBeingReflowedWithLock(null);
                }
            }
        });
    }

    public void loadSubPageListMap() {
        synchronized (pageListLock) {
            if (pageListMap != null) {
                pageListMap.clear();
            }
            try {
                File file = cacheFilePath(cacheRoot, documentMd5, settings, INDEX_FILE_NAME);
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

    public int getCurrentSubPageIndex(final String pageName) {
        return getSubPageList(pageName).getCurrent();
    }

    public boolean atFirstSubPage(final String pageName) {
        return getSubPageList(pageName).atBegin();
    }

    public boolean atLastSubPage(final String pageName) {
        try {
            reflowLock.lock();
            try {
                while (isPageBeingReflowed(pageName)) {
                    if (!getSubPageList(pageName).atEnd()) {
                        return false;
                    }
                    reflowReadyCondition.await();
                }
                return getSubPageList(pageName).atEnd();
            } finally {
                reflowLock.unlock();
            }
        } catch (InterruptedException ex) {
            return false;
        }
    }

    public void moveToFirstSubPage(final String pageName) {
        getSubPageList(pageName).moveToBegin();
    }

    public void moveToLastSubPage(final String pageName) {
        try {
            reflowLock.lock();
            try {
                while (isPageBeingReflowed(pageName)) {
                    reflowReadyCondition.await();
                }
                getSubPageList(pageName).moveToEnd();
            } finally {
                reflowLock.unlock();
            }
        } catch (InterruptedException ex) {
        }
    }

    public void previousSubPage(final String pageName) {
        getSubPageList(pageName).prev();
    }

    public void nextSubPage(final String pageName) {
        try {
            reflowLock.lock();
            try {
                while (isPageBeingReflowed(pageName)) {
                    if (!getSubPageList(pageName).atEnd()) {
                        getSubPageList(pageName).next();
                        return;
                    }
                    reflowReadyCondition.await();
                }
                if (!getSubPageList(pageName).atEnd()) {
                    getSubPageList(pageName).next();
                }
            } finally {
                reflowLock.unlock();
            }
        } catch (InterruptedException ex) {
        }
    }

    public void moveToSubSPage(final String pageName, final int index) {
        try {
            reflowLock.lock();
            try {
                while (isPageBeingReflowed(pageName)) {
                    if (getSubPageList(pageName).getCount() > index) {
                        getSubPageList(pageName).moveToScreen(index);
                        return;
                    }
                    reflowReadyCondition.await();
                }
                if (getSubPageList(pageName).getCount() > index) {
                    getSubPageList(pageName).moveToScreen(index);
                }
            } finally {
                reflowLock.unlock();
            }
        } catch (InterruptedException ex) {
        }
    }

    private ReaderBitmapList getSubPageList(final String pageName) {
        synchronized (pageListLock) {
            ReaderBitmapList list = pageListMap.get(pageName);
            if (list == null) {
                list = new ReaderBitmapList();
                pageListMap.put(pageName, list);
            }
            return list;
        }
    }

    private void clearSubPageList(final String pageName) {
        getSubPageList(pageName).clear();
    }

    public String getSubPageKey(final String pageName, final int subPage) {
        return getKeyOfSubPage(documentMd5, settings, pageName, subPage);
    }

    public Bitmap getSubPageBitmap(final String pageName, final int subPage) {
        try {
            reflowLock.lock();
            try {
                final String pageKey = getKeyOfSubPage(documentMd5, settings, pageName, subPage);
                subPageBeingWaited = null;
                while (isPageBeingReflowed(pageName)) {
                    if (getSubPageList(pageName).getCount() > subPage) {
                        return getSubPageBitmapFromCache(pageName, subPage);
                    }
                    subPageBeingWaited = pageKey;
                    reflowReadyCondition.await();
                }

                if (getSubPageList(pageName).getCount() <= subPage) {
                    return null;
                }
                return getSubPageBitmapFromCache(pageName, subPage);
            } finally {
                reflowLock.unlock();
            }
        } catch (InterruptedException ex) {
            return null;
        }
    }

    /**
     * will be called from jni
     *
     * @param pageName
     * @param subPage
     * @param bitmap
     */
    @SuppressWarnings("unused")
    public void addBitmap(final String pageName, final int subPage, final Bitmap bitmap) {
        reflowLock.lock();
        try {
            ReaderBitmapList list = getSubPageList(pageName);
            list.addBitmap(bitmap);
            saveSubPageBitmapToCache(pageName, subPage, bitmap);
            reflowReadyCondition.signal();
        } finally {
            reflowLock.unlock();
        }
    }

    private static final String getKeyOfSubPage(final String documentMd5, final ImageReflowSettings settings, final String pageName, int index) {
        return String.format("%s-%s-%d", md5OfDocumentWithSettings(documentMd5, settings), pageName, index);
    }

    private static final String md5OfDocumentWithSettings(final String documentMd5, final ImageReflowSettings settings) {
        return HashUtils.md5(documentMd5 + "-" + settings.md5());
    }

    private boolean isPageBeingReflowed(final String pageName) {
        return !StringUtils.isNullOrEmpty(pageBeingReflowed) && pageBeingReflowed.equals(pageName);
    }

    private void setPageBeingReflowedWithLock(final String pageName) {
        reflowLock.lock();
        try {
            pageBeingReflowed = pageName;
            if (StringUtils.isNullOrEmpty(pageBeingReflowed)) {
                reflowReadyCondition.signal();
            }
        } finally {
            reflowLock.unlock();
        }
    }

    private void saveSubPageBitmapToCache(String pageName, int subPage, Bitmap bitmap) {
        final String pageKey = getKeyOfSubPage(documentMd5, settings, pageName, subPage);
        putBitmapCache(pageKey, bitmap);
        if (!StringUtils.isNullOrEmpty(subPageBeingWaited) && subPageBeingWaited.equals(pageKey)) {
            if (subPageResultBitmap != null && !subPageResultBitmap.isRecycled()) {
                subPageResultBitmap.recycle();
            }
            // keep the bitmap as we'll need it immediately
            subPageResultBitmap = bitmap;
        } else {
            bitmap.recycle();
        }
    }

    private Bitmap getSubPageBitmapFromCache(final String pageName, final int subPage) {
        if (!StringUtils.isNullOrEmpty(subPageBeingWaited) && subPageResultBitmap != null) {
            Bitmap bitmap = subPageResultBitmap;
            subPageBeingWaited = null;
            subPageResultBitmap = null;
            return bitmap;
        }
        Bitmap bitmap = getBitmapCache(getKeyOfSubPage(documentMd5, settings, pageName, subPage));
        return bitmap;
    }

    public void clearAllCacheFiles() {
        documentMd5 = null;
//        clearBitmapCache();
//        clearPageMap();
    }

    private void savePageMap() {
        synchronized (pageListLock) {
            try {
                File file = cacheFilePath(cacheRoot, documentMd5, settings, INDEX_FILE_NAME);
                String json = JSON.toJSONString(pageListMap);
                FileUtils.saveContentToFile(json, file);
            } catch (Exception e) {
                Debug.w(TAG, e);
            }
        }
    }

    private void clearPageMap() {
        synchronized (pageListLock) {
            if (pageListMap == null) {
                return;
            }
            pageListMap.clear();
            savePageMap();
        }
    }

    private boolean isReflowed(final String pageName) {
        ReaderBitmapList list = getSubPageList(pageName);
        if (list == null || list.isEmpty()) {
            return false;
        }
        Bitmap bitmap = getSubPageBitmap(pageName, list.getCurrent());
        if (bitmap == null) {
            return false;
        }
        bitmap.recycle();
        return true;
    }

    private void reflow(Bitmap bitmap, final String pageName) {
        if (ImageUtils.reflowScannedPage(bitmap, pageName, this)) {
            savePageMap();
        }
    }

    private void putBitmapCache(final String key, Bitmap bitmap) {
        bitmapCache.put(key, bitmap);
    }

    private Bitmap getBitmapCache(final String key) {
        return bitmapCache.get(key);
    }

    private void clearBitmapCache() {
        bitmapCache.clear();
    }

    private static File cacheFilePath(final File root, final String documentMd5, final ImageReflowSettings settings, final String fileName) {
        if (settings == null || root == null) {
            return null;
        }
        File file = new File(root, md5OfDocumentWithSettings(documentMd5, settings) + "-" + fileName);
        return file;
    }
}
