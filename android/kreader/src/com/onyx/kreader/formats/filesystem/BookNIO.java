package com.onyx.kreader.formats.filesystem;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by zengzhu on 2/28/16.
 */
public class BookNIO {

    private String path;
    private RandomAccessFile randomAccessFile = null;
    private FileChannel readChannel;


    public BookNIO(final String p) {
        path = p;
    }

    public boolean open() {
        try {
            if (randomAccessFile == null) {
                randomAccessFile = new RandomAccessFile(path, "r");
            }
            readChannel = randomAccessFile.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int read(final ByteBuffer data) {
        try {
            return readChannel.read(data);
        } catch (Exception e) {
            return -1;
        }
    }
}
