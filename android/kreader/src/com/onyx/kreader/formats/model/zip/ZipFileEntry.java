package com.onyx.kreader.formats.model.zip;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.io.InputStream;

/**
 * Created by zengzhu on 3/17/16.
 */
public class ZipFileEntry {

    private ZipFile zipFile;
    private FileHeader fileHeader;

    public ZipFileEntry(final ZipFile parent, final FileHeader header) {
        zipFile = parent;
        fileHeader = header;
    }

    public final InputStream getInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = zipFile.getInputStream(fileHeader);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return inputStream;
        }
    }

}
