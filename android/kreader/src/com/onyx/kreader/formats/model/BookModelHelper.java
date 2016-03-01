package com.onyx.kreader.formats.model;

import android.util.Log;
import com.onyx.kreader.formats.encodings.Decoder;
import com.onyx.kreader.formats.filesystem.FileNIO;
import com.onyx.kreader.utils.StringUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;


/**
 * Created by zengzhu on 2/28/16.
 */
public class BookModelHelper {

    private FileNIO file;
    private String encoding;
    private String fallbackEncoding = "UTF-8";
    private Decoder decoder;

    public BookModelHelper(final String path) {
        file = new FileNIO(path);
    }

    public boolean isEncodingDetected() {
        return StringUtils.isNotBlank(encoding);
    }

    public final String getEncoding() {
        return encoding;
    }

    public boolean detectEncoding(final ByteBuffer data) {
        boolean detected = true;
        encoding = Decoder.detectEncoding(data);
        if (StringUtils.isNullOrEmpty(encoding)) {
            encoding = fallbackEncoding;
            detected = false;
        }
        decoder = Decoder.createInstance(encoding);
        return detected;
    }

    public boolean open() {
        return file.open();
    }

    public int read(final ByteBuffer data) {
        data.clear();
        int count = file.read(data);
        data.flip();
        return count;
    }

    public void decodeBuffer(final ByteBuffer inBuffer, final CharBuffer outBuffer, boolean finished) {
        if (decoder != null) {
            decoder.decodeBuffer(inBuffer, outBuffer, finished);
        }
    }

}
