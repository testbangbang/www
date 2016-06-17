package com.onyx.kreader.host.impl;

import com.onyx.kreader.api.ReaderTextSplitter;

/**
 * Created by zhuzeng on 5/24/15.
 * javap -s com.onyx.reader.host.impl.ReaderTextSplitter
 */

public class ReaderTextSplitterImpl implements ReaderTextSplitter {

    // TODO, use json later.
    static private final String  Splitters[] = {"。", "？", "！", ".", "!"};
    static private ReaderTextSplitterImpl instance;
    static boolean hasSpace = false;
    private ReaderTextSplitterImpl() {
        super();
    }

    public static ReaderTextSplitterImpl sharedInstance() {
        if (instance == null) {
            instance = new ReaderTextSplitterImpl();
        }
        return instance;
    }

    // reverse string.
    public int getTextLeftBoundary(final String word, final String left, final String right) {
        hasSpace = hasSpace(left);
        if (!hasSpace) {
            return 0;
        }
        return searchSpaceBoundaryForLatinFromRight(word, left);
    }

    public int getTextRightBoundary(final String word, final String left, final String right) {
        hasSpace = hasSpace(right);
        if (!hasSpace) {
            return 0;
        }
        return searchSpaceBoundaryForLatinFromLeft(word, right);
    }

    boolean hasSpace(final String string) {
        return (string.indexOf(' ') > 0);
    }

    String normalizeString(final String string) {
        if (string.length() <= 0 || string.charAt(string.length() - 1) != '\0') {
            return string;
        }
        return string.substring(0, string.length() - 1); // excluding trailing terminator
    }

    int searchSpaceBoundaryForLatinFromLeft(String word, String string) {
        word = normalizeString(word);
        if (word.length() > 0 && !Character.isLetter(word.charAt(word.length() - 1))) {
            return 0;
        }

        string = normalizeString(string);
        int index = 0;
        while (index < string.length() - 1) {
            if (!Character.isLetter(string.charAt(index))) {
                break;
            }
            ++index;
        }
        if (index == 0 || index >= string.length()) {
            return 0;
        }
        final int lastNonSpace = index - 1;
        return lastNonSpace;
    }

    int searchSpaceBoundaryForLatinFromRight(String word, String string) {
        word = normalizeString(word);
        if (word.length() > 0 && !Character.isLetter(word.charAt(0))) {
            return 0;
        }

        string = normalizeString(string);
        int index = string.length() - 1;
        while (index >= 0) {
            if (!Character.isLetter(string.charAt(index))) {
                break;
            }
            --index;
        }
        if (index < 0 || index == (string.length() - 1)) {
            return 0;
        }
        final int lastNonSpace = index + 1;
        return string.length() - 1 - lastNonSpace;
    }

    int nextSentence(final String text, int start) {
        int position = start;
        while (position == start) {
            ++start;
            position = text.indexOf("。", start);
        }
        return position;
    }

    int isSentenceBoundary(final String text) {
        for(String splitter: Splitters) {
            if (text.endsWith(splitter)) {
                return 1;
            }
        }
        return 0;
    }

}
