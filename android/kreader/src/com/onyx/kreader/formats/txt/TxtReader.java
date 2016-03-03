package com.onyx.kreader.formats.txt;

import com.onyx.kreader.formats.encodings.Decoder;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.BookReader;
import com.onyx.kreader.formats.model.Paragraph;
import com.onyx.kreader.formats.model.entry.TextParagraphEntry;

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
    private Paragraph paragraph;


    public boolean open(final BookModel bookModel) {
        if (bookModel.getModelHelper() == null) {
            return false;
        }

        if (bookModel.getModelHelper().open()) {
            bookModel.getTextModel().reset();
            return true;
        }
        return false;
    }


    public boolean processNext(final BookModel bookModel) {
        if (bookModel.getModelHelper().read(fileData) <= 0) {
            return false;
        }

        if (fileData.limit() < limit) {
            bookModel.getTextModel().setLoadFinished(true);
        }
        if (!bookModel.getModelHelper().isEncodingDetected()) {
            detectEncoding(bookModel);
        }

        decodeBuffer(bookModel);
        processNextData(bookModel);
        return true;
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

    private void decodeBuffer(final BookModel bookModel) {
        bookModel.getModelHelper().decodeBuffer(fileData, decodeResult, fileData.limit() < limit);
    }

    private void processNextData(final BookModel bookModel) {
        int start = 0;
        int end = decodeResult.length();
        int ptr = start;
        while (ptr < end) {
            char value = decodeResult.get(ptr);
            if (value == Decoder.NL || value == Decoder.CR) {
                boolean skipNewLine = false;
                if (value == Decoder.CR && ptr + 1 < end && decodeResult.get(ptr + 1) == Decoder.NL) {
                    skipNewLine = true;
                    decodeResult.put(ptr, Decoder.NL);
                }
                if (start != ptr) {
                    processData(bookModel, decodeResult, start, ptr + 1);
                }
                if (skipNewLine) {
                    ++ptr;
                }
                start = ptr + 1;
                processNewLine(bookModel);
            } else if ((value & 0x80) == 0 && Character.isSpaceChar(value)) {
                if (value != Decoder.TAB) {
                    decodeResult.put(ptr, Decoder.SPACE);
                }
            } else {
            }
            ++ptr;
        }
        if (start != end) {
            processData(bookModel, decodeResult, start, end);
        }
    }

    /**
     * flush data to current paragraph.
     */
    private void processNewLine(final BookModel bookModel) {
        flushToCurrentParagraph();
        bookModel.getTextModel().addParagraph(currentParagraph());
        resetCurrentParagraph();
    }

    /**
     * add string to current paragraph
     * @param buffer
     * @param start
     * @param end
     * @return
     */
    private int processData(final BookModel bookModel, final CharBuffer buffer, int start, int end) {
        stringBuilder.append(buffer.subSequence(start, end));
        return 0;
    }

    private Paragraph currentParagraph() {
        if (paragraph == null) {
            paragraph = Paragraph.create(Paragraph.ParagraphKind.TEXT_PARAGRAPH);
        }
        return paragraph;
    }

    private void resetCurrentParagraph() {
        paragraph = null;
    }

    private void flushToCurrentParagraph() {
        final String text = stringBuilder.toString();
        TextParagraphEntry entry = new TextParagraphEntry(text);
        currentParagraph().addEntry(entry);
        stringBuilder.setLength(0);
    }




}
