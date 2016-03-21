package com.onyx.kreader.plugins.comic;

import android.util.Log;
import com.onyx.kreader.utils.FileUtils;
import com.onyx.kreader.utils.StringUtils;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.BaseInputStream;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 3/15/16.
 */
public class ComicArchiveZip implements ComicArchive {
    private static final String TAG = ComicArchiveZip.class.getSimpleName();

    private ZipFile archive = null;

    @Override
    public boolean isEncrypted() {
        return false;
    }

    @Override
    public void setPassword(String password) {
        if (!isOpened()) {
            return;
        }
        try {
            if (archive.isEncrypted() && !StringUtils.isNullOrEmpty(password)) {
                archive.setPassword(password);
            }
        } catch (ZipException e) {
            Log.w(TAG, e);
        }
    }

    @Override
    public boolean open(String path, String password) {
        try {
            ZipFile zip = new ZipFile(path);
            if (!zip.isValidZipFile()) {
                return false;
            }
            if (zip.isEncrypted() && !StringUtils.isNullOrEmpty(password)) {
                zip.setPassword(password);
            }
            archive = zip;
            return true;
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return false;
    }

    @Override
    public List<String> getPageList() {
        ArrayList<String> pages = new ArrayList<String>();
        if (!isOpened()) {
            return pages;
        }

        try {
            List headers = archive.getFileHeaders();
            for (int i = 0; i < headers.size(); i++) {
                FileHeader header = (FileHeader) headers.get(i);
                if (header.isDirectory()) {
                    continue;
                }
                if (FileUtils.isImageFile(header.getFileName())) {
                    pages.add(header.getFileName());
                }
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }

        return pages;
    }

    @Override
    public InputStream getPageInputStream(String page) {
        if (!isOpened()) {
            return null;
        }
        try {
            FileHeader header = archive.getFileHeader(page);
            if (header == null) {
                return null;
            }
            return archive.getInputStream(header);
        } catch (Exception e) {
            Log.w(TAG, e);
            return null;
        }
    }

    @Override
    public void close() {
        if (!isOpened()) {
            return;
        }
        archive = null;
    }

    private boolean isOpened() {
        return archive != null;
    }
}
