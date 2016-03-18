package com.onyx.kreader.formats.model.zip;


import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zengzhu on 3/17/16.
 */
public class ZipFileReader {

    private ZipFile zipFile;
    private Map<String, ZipFileEntry> entries;

    public boolean open(final String path, final String password) {
        try {
            zipFile = new ZipFile(path);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            entries = new HashMap<String, ZipFileEntry>();
            List<FileHeader> list = zipFile.getFileHeaders();
            for(FileHeader fileHeader : list) {
                entries.put(fileHeader.getFileName(), new ZipFileEntry(zipFile, fileHeader));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean close() {
        if (zipFile == null) {
            return false;
        }
        zipFile = null;
        return true;
    }

    public int entryCount() {
        if (entries == null) {
            return 0;
        }
        return entries.size();
    }

    public boolean contains(final String name) {
        if (entries == null) {
            return false;
        }
        return entries.containsKey(name);
    }

    public final ZipFileEntry getEntry(final String name) {
        if (entries == null) {
            return null;
        }
        return entries.get(name);
    }
}
