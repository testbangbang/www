package com.onyx.kreader.formats.txt;

import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.BookReader;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * Created by zengzhu on 2/28/16.
 */
public class TxtReader implements BookReader {

    private static String TAG = TxtReader.class.getSimpleName();
    private final int limit = 2 * 1024;
    private ByteBuffer fileData = ByteBuffer.allocate(limit);
    private CharBuffer decodeResult = CharBuffer.allocate(limit);
    private StringBuilder stringBuilder = new StringBuilder();

    static public final char CR = '\r';
    static public final char NL = '\n';
    static public final char TAB = '\t';
    static public final char SPACE = ' ';

    public boolean open(final BookModel bookModel) {
        if (bookModel.getModelHelper() != null) {
            return bookModel.getModelHelper().open();
        }
        return true;
    }

    public boolean processNext(final BookModel bookModel) {
        if (bookModel.getModelHelper().read(fileData) <= 0) {
            return false;
        }

        if (!bookModel.getModelHelper().isEncodingDetected()) {
            detectEncoding(bookModel);
        }
        return processNextData(bookModel);
    }

    public boolean close(final BookModel bookModel) {
        fileData.clear();
        decodeResult.clear();
        return  bookModel.getModelHelper().close();
    }

    private void detectEncoding(final BookModel bookModel) {
        bookModel.getModelHelper().detectEncoding(fileData);
        fileData.rewind();
    }

    private boolean processNextData(final BookModel bookModel) {
        bookModel.getModelHelper().decodeBuffer(fileData, decodeResult, fileData.limit() < limit);
        final char[] array = decodeResult.array();
        int start = 0;
        int end = array.length;
        int ptr = start;
        while (ptr < end) {
            char value = array[ptr];
            if (value == NL || value == CR) {
                boolean skipNewLine = false;
                if (value == CR && ptr + 1 < end && array[ptr + 1] == NL) {
                    skipNewLine = true;
                    array[ptr] = NL;
                }
                if (start != ptr) {
                    processData(decodeResult, start, ptr + 1);
                }
                if (skipNewLine) {
                    ++ptr;
                }
                start = ptr + 1;
                processNewLine();
            } else if ((value & 0x80) == 0 && Character.isSpaceChar(value)) {
                if (value != TAB) {
                    array[ptr] = SPACE;
                }
            } else {
            }
            ++ptr;
        }
        if (start != end) {
            processData(decodeResult, start, end);
        }
        return true;
    }

    private void processNewLine() {
       // Log.i(TAG, "on new line");
    }

    private int processData(final CharBuffer buffer, int start, int end) {
        //String string = stringBuilder.append(buffer.subSequence(start, end)).toString();
        //Log.i(TAG, "get string:" + string);
        //stringBuilder.setLength(0);
        return 0;
    }






}
