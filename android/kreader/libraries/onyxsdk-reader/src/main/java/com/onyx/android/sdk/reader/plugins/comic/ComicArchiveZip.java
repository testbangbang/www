package com.onyx.android.sdk.reader.plugins.comic;

import android.util.Log;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.FileUtils;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by joy on 3/15/16.
 */
public class ComicArchiveZip implements ComicArchive {
    private static final String TAG = ComicArchiveZip.class.getSimpleName();

    /**
     * work around the issues of ZipInputStream
     */
    private static class InternalZipInputStream extends InputStream {
        private FileHeader zipHeader = null;
        private ZipInputStream zipInputStream = null;

        public InternalZipInputStream(FileHeader header, ZipInputStream inputStream) {
            zipHeader = header;
            zipInputStream = inputStream;
        }

        @Override
        public int available() throws IOException {
            return zipInputStream.available();
        }

        @Override
        public void close() throws IOException {
            boolean skipCheck = !zipHeader.isEncrypted();
            zipInputStream.close(skipCheck);
        }

        @Override
        public void mark(int readlimit) {
            zipInputStream.mark(readlimit);
        }

        @Override
        public boolean markSupported() {
            boolean b = zipInputStream.markSupported();
            return b;
        }

        @Override
        public int read() throws IOException {
            return zipInputStream.read();
        }

        public int read(byte[] buffer) throws IOException {
            return zipInputStream.read(buffer, 0, buffer.length);
        }

        public int read(byte[] buffer, int offset, int length) throws IOException {
            return zipInputStream.read(buffer, offset, length);
        }

        @Override
        public synchronized void reset() throws IOException {
            zipInputStream.reset();
        }

        @Override
        public long skip(long byteCount) throws IOException {
            for (int i = 0; i < byteCount; i++) {
                if (zipInputStream.read() == -1) {
                    return i;
                }
            }
            return byteCount;
        }
    }

    private ZipFile archive = null;
    private SortedMap<String, FileHeader> pageList = null;

    @Override
    public boolean isEncrypted() {
        if (!isOpened()) {
            return false;
        }

        try {
            return archive.isEncrypted();
        } catch (ZipException e) {
            Log.w(TAG, e);
        }

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
            return loadPageList();
        } catch (Exception e) {
            Log.w(TAG, e);
        }

        return false;
    }

    @Override
    public List<String> getPageList() {
        ArrayList<String> pages = new ArrayList<>();
        if (!isOpened()) {
            return pages;
        }

        pages.addAll(pageList.keySet());
        return pages;
    }

    @Override
    public InputStream getPageInputStream(String page) {
        if (!isOpened()) {
            return null;
        }

        try {
            FileHeader header = pageList.get(page);
            if (header == null) {
                return null;
            }
            return new InternalZipInputStream(header, archive.getInputStream(header));
        } catch (Exception e) {
            Log.w(TAG, e);
        }

        return null;
    }

    @Override
    public void close() {
        archive = null;
        pageList = null;
    }

    private boolean isOpened() {
        return archive != null && pageList != null;
    }

    private boolean loadPageList() {
        SortedMap<String, FileHeader> pages = new TreeMap<>();
        try {
            List headers = archive.getFileHeaders();
            for (int i = 0; i < headers.size(); i++) {
                FileHeader header = (FileHeader) headers.get(i);
                if (header.isDirectory()) {
                    continue;
                }
                if (FileUtils.isImageFile(header.getFileName())) {
                    pages.put(header.getFileName(), header);
                }
            }
            pageList = pages;
            return true;
        } catch (Exception e) {
            Log.w(TAG, e);
        }

        return false;
    }
}
