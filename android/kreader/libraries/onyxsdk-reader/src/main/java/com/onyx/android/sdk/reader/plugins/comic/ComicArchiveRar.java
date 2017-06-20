package com.onyx.android.sdk.reader.plugins.comic;


import com.onyx.android.sdk.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by joy on 3/16/16.
 */
public class ComicArchiveRar implements ComicArchive {

    UnrarJniWrapper wrapper = new UnrarJniWrapper();
    private String arcPath = null;
    String[] entries = null;

    @Override
    public boolean isEncrypted() {
        return wrapper.isEncrypted();
    }

    @Override
    public void setPassword(String password) {
        if (!isOpened()) {
            return;
        }
        if (!StringUtils.isNullOrEmpty(password)) {
            wrapper.setPassword(password);
        }
    }

    @Override
    public boolean open(String path, String password) {
        if (!wrapper.open(path)) {
            return false;
        }
        if (!StringUtils.isNullOrEmpty(password)) {
            wrapper.setPassword(password);
        }
        arcPath = path;
        entries = wrapper.getEntries();
        return entries != null;
    }

    @Override
    public List<String> getPageList() {
        ArrayList<String> pages = new ArrayList<String>();
        if (!isOpened()) {
            return pages;
        }
        pages.addAll(Arrays.asList(entries));
        return pages;
    }

    @Override
    public InputStream getPageInputStream(String page) {
        if (!isOpened()) {
            return null;
        }
        byte[] data = wrapper.extractEntryData(page);
        if (data == null) {
            return null;
        }
        return new ByteArrayInputStream(data);
    }

    @Override
    public void close() {
        wrapper.close();
        arcPath = null;
        entries = null;
    }

    private  boolean isOpened() {
        return arcPath != null && entries != null;
    }
}
