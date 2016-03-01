package com.onyx.kreader.formats.txt;

import android.util.Log;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.BookReader;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * Created by zengzhu on 2/28/16.
 */
public class TxtReader implements BookReader {

    private static String TAG = TxtReader.class.getSimpleName();
    private final int limit = 2048;
    private ByteBuffer data = ByteBuffer.allocate(limit);
    private CharBuffer result = CharBuffer.allocate(limit);
    private StringBuilder stringBuilder = new StringBuilder();

    static public final char CR = '\r';
    static public final char NL = '\n';
    static public final char TAB = '\t';
    static public final char SPACE = ' ';

    public boolean open(final BookModel bookModel) {
        if (bookModel.getBookContext() != null) {
            return bookModel.getBookContext().file.open();
        }
        return true;
    }

    public boolean processNext(final BookModel bookModel) {
        if (bookModel.getBookContext().read(data) <= 0) {
            return false;
        }

        if (!bookModel.getBookContext().isEncodingDetected()) {
            detectEncoding(bookModel);
        }
        return processNextData(bookModel);
    }

    private void detectEncoding(final BookModel bookModel) {
        bookModel.getBookContext().detectEncoding(data);
        data.rewind();
    }

    private boolean processNextData(final BookModel bookModel) {
        bookModel.getBookContext().decode(data, result, data.limit() < limit);
        int start = 0;
        int end = result.length();
        int ptr = start;
        while (ptr < end) {
            char value = result.get(ptr);
            if (value == NL || value == CR) {
                boolean skipNewLine = false;
                if (value == CR && ptr + 1 < end && result.charAt(ptr + 1) == NL) {
                    skipNewLine = true;
                    result.put(ptr, NL);
                }
                if (start != ptr) {
                    processData(result, start, ptr + 1);
                }
                if (skipNewLine) {
                    ++ptr;
                }
                start = ptr + 1;
                processNewLine();
            } else if ((value & 0x80) == 0 && Character.isSpaceChar(value)) {
                if (value != TAB) {
                    result.put(ptr, SPACE);
                }
            } else {
            }
            ++ptr;
        }
        if (start != end) {
            processData(result, start, end);
        }
        return true;
    }

    private void processNewLine() {
        Log.i(TAG, "on new line");
    }

    private int processData(final CharBuffer buffer, int start, int end) {
        String string = stringBuilder.append(buffer.subSequence(start, end)).toString();
        Log.i(TAG, "get string:" + string);
        stringBuilder.setLength(0);
        return 0;
    }






}
