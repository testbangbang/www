package com.onyx.kreader.plugins.comic;

import com.onyx.kreader.utils.FileUtils;
import com.onyx.kreader.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 3/16/16.
 */
public class ComicArchiveRar implements ComicArchive {

    private String arcPath = null;
    ArrayList<String> pageList = new ArrayList<String>();
    UnrarJniWrapper wrapper = new UnrarJniWrapper();

    @Override
    public boolean isEncrypted() {
        return wrapper.isEncrypted();
    }

    @Override
    public void setPassword(String password) {
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
        return true;
    }

    @Override
    public List<String> getPageList() {
        String[] entries = wrapper.getEntries();
        if (entries == null) {
            return pageList;
        }
        for (String s : entries) {
            if (FileUtils.isImageFile(s)) {
                pageList.add(s);
            }
        }
        return pageList;
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
        arcPath = null;
    }

    private  boolean isOpened() {
        return arcPath != null;
    }
}
