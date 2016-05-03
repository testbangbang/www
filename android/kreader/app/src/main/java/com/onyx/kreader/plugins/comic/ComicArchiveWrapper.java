package com.onyx.kreader.plugins.comic;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.onyx.kreader.plugins.images.ImagesAndroidWrapper;
import com.onyx.kreader.plugins.images.ImagesWrapper;
import com.onyx.kreader.utils.BitmapUtils;
import com.onyx.kreader.utils.FileUtils;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Comic archive is a container of images, so we can reuse some code of image plugin
 *
 * Created by joy on 3/15/16.
 */
public class ComicArchiveWrapper {
    private static final String TAG = ComicArchiveWrapper.class.getSimpleName();

    private static final HashMap<Integer, ImagesWrapper.ImageInformation> infoCache = new HashMap<Integer, ImagesWrapper.ImageInformation>();

    private ComicArchive archive;

    public boolean open(String path, String password) {
        if (FileUtils.isZipFile(path)) {
            archive = new ComicArchiveZip();
        } else if (FileUtils.isRarFile(path)) {
            archive = new ComicArchiveRar();
        }
        return archive.open(path, password);
    }

    public int getPageCount() {
        if (!isOpened()) {
            return 0;
        }
        return archive.getPageList().size();
    }

    public ImagesWrapper.ImageInformation imageInfo(int page) {
        if (!infoCache.containsKey(page)) {
            ImagesWrapper.ImageInformation imageInformation = new ImagesWrapper.ImageInformation();
            if (!getPageInfo(page, imageInformation)) {
                return null;
            }
            saveImageInformation(page, imageInformation);
        }
        return loadBitmapInformation(page);
    }

    public boolean drawPage(final int page, final float scale, int rotation, final RectF displayRect, final RectF positionRect, final RectF visibleRect, final Bitmap bitmap) {
        InputStream stream = archive.getPageInputStream(archive.getPageList().get(page));
        if (stream == null) {
            return false;
        }
        try {
            return ImagesAndroidWrapper.drawImage(stream, scale, rotation, displayRect, positionRect, visibleRect, bitmap);
        } finally {
            FileUtils.closeQuietly(stream);
        }
    }

    public void close() {
        if (isOpened()) {
            archive.close();
        }
    }

    private boolean isOpened() {
        return archive != null;
    }

    private void saveImageInformation(int page, ImagesWrapper.ImageInformation imageInformation) {
        infoCache.put(page, imageInformation);
    }

    private ImagesWrapper.ImageInformation loadBitmapInformation(int page) {
        return infoCache.get(page);
    }

    private boolean getPageInfo(int page, ImagesWrapper.ImageInformation info) {
        if (!isOpened()) {
            return false;
        }
        String p = archive.getPageList().get(page);
        InputStream stream = archive.getPageInputStream(p);
        if (stream == null) {
            return false;
        }
        try {
            if (!BitmapUtils.decodeBitmapSize(stream, info)) {
                return false;
            }
        } finally {
            FileUtils.closeQuietly(stream);
        }
        return true;
    }
}
