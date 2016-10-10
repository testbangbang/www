package com.onyx.kreader.reflow;

import android.graphics.Bitmap;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderBitmapList;
import com.onyx.kreader.utils.HashUtils;
import com.onyx.kreader.utils.ImageUtils;

import java.io.File;
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

    private String documentMd5;
    private File cacheRoot;
    private ImageReflowSettings settings;

    private ReflowedSubPageCache subPageCache;
    private ReflowedSubPageIndex subPageIndex;
    private ConcurrentHashMap<String, Object> pageListLock = new ConcurrentHashMap<>();

    private String pageBeingReflowed;
    private Lock reflowLock = new ReentrantLock(); // to be hold for a short time to check if a page is being reflowed or not
    private Condition reflowReadyCondition = reflowLock.newCondition();

    private ExecutorService reflowExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    });

    public ImageReflowManager(final String documentMd5, final File cacheRoot, int dw, int dh) {
        super();

        this.documentMd5 = documentMd5;
        this.cacheRoot = cacheRoot;
        settings = ImageReflowSettings.createSettings();
        settings.dev_width = dw;
        settings.dev_height = dh;
        subPageCache = ReflowedSubPageCache.create(cacheRoot);
        loadSubPageIndex();
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
        loadSubPageIndex();
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

    private void waitUntilAllSubPagesAvailable(final String pageName) {
        try {
            reflowLock.lock();
            try {
                while (isPageBeingReflowed(pageName)) {
                    reflowReadyCondition.await();
                }
            } finally {
                reflowLock.unlock();
            }
        } catch (InterruptedException ex) {
        }
    }

    private void waitUntilNextSubPageAvailable(final String pageName) {
        try {
            reflowLock.lock();
            try {
                while (isPageBeingReflowed(pageName)) {
                    if (!getSubPageList(pageName).atEnd()) {
                        return;
                    }
                    reflowReadyCondition.await();
                }
            } finally {
                reflowLock.unlock();
            }
        } catch (InterruptedException ex) {
        }
    }

    private void waitUntilSubPageAvailable(final String pageName, final int subPage) {
        try {
            reflowLock.lock();
            try {
                while (isPageBeingReflowed(pageName)) {
                    if (getSubPageList(pageName).getCount() > subPage) {
                        return;
                    }
                    reflowReadyCondition.await();
                }
            } finally {
                reflowLock.unlock();
            }
        } catch (InterruptedException ex) {
        }
    }

    public int getCurrentSubPageIndex(final String pageName) {
        return getSubPageList(pageName).getCurrent();
    }

    public boolean atFirstSubPage(final String pageName) {
        return getSubPageList(pageName).atBegin();
    }

    public boolean atLastSubPage(final String pageName) {
        waitUntilAllSubPagesAvailable(pageName);
        return getSubPageList(pageName).atEnd();
    }

    public void moveToFirstSubPage(final String pageName) {
        getSubPageList(pageName).moveToBegin();
    }

    public void moveToLastSubPage(final String pageName) {
        waitUntilAllSubPagesAvailable(pageName);
        getSubPageList(pageName).moveToEnd();
    }

    public void previousSubPage(final String pageName) {
        getSubPageList(pageName).prev();
    }

    public void nextSubPage(final String pageName) {
        waitUntilNextSubPageAvailable(pageName);
        if (!getSubPageList(pageName).atEnd()) {
            getSubPageList(pageName).next();
        }
    }

    public void moveToSubSPage(final String pageName, final int index) {
        waitUntilSubPageAvailable(pageName, index);
        if (getSubPageList(pageName).getCount() > index) {
            getSubPageList(pageName).moveToScreen(index);
        }
    }

    private void loadSubPageIndex() {
        File file = subPageIndexFilePath(cacheRoot, documentMd5, settings, INDEX_FILE_NAME);
        subPageIndex = ReflowedSubPageIndex.load(file);
    }

    private void savePageMap() {
        subPageIndex.saveSubPageIndex();
    }

    private ReaderBitmapList getSubPageList(final String pageName) {
        return subPageIndex.getSubPageList(pageName);
    }

    private void clearSubPageList(final String pageName) {
        getSubPageList(pageName).clear();
    }

    public String getSubPageKey(final String pageName, final int subPage) {
        return getKeyOfSubPage(documentMd5, settings, pageName, subPage);
    }

    public Bitmap getSubPageBitmap(final String pageName, final int subPage) {
        waitUntilSubPageAvailable(pageName, subPage);
        if (getSubPageList(pageName).getCount() <= subPage) {
            return null;
        }
        return getSubPageBitmapFromCache(pageName, subPage);
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

    private void saveSubPageBitmapToCache(String pageName, int subPage, Bitmap bitmap) {
        final String pageKey = getKeyOfSubPage(documentMd5, settings, pageName, subPage);
        putBitmapCache(pageKey, bitmap);
    }

    private Bitmap getSubPageBitmapFromCache(final String pageName, final int subPage) {
        return getBitmapCache(getKeyOfSubPage(documentMd5, settings, pageName, subPage));
    }

    public void clearAllCacheFiles() {
        documentMd5 = null;
    }

    private void putBitmapCache(final String key, Bitmap bitmap) {
        subPageCache.put(key, bitmap);
    }

    private Bitmap getBitmapCache(final String key) {
        return subPageCache.get(key);
    }

    private static File subPageIndexFilePath(final File root, final String documentMd5, final ImageReflowSettings settings, final String fileName) {
        if (settings == null || root == null) {
            return null;
        }
        File file = new File(root, md5OfDocumentWithSettings(documentMd5, settings) + "-" + fileName);
        return file;
    }
}
