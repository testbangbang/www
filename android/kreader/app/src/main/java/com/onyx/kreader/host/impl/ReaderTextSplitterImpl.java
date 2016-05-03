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
    static int getTextLeftBoundary(final String character, final String left, final String right) {
        hasSpace = hasSpace(left) || hasSpace(right);
        if (!hasSpace) {
            return 1;
        }
        return getSpaceBoundaryForLatin(left);
    }

    static int getTextRightBoundary(final String character, final String left, final String right) {
        if (!hasSpace) {
            return 1;
        }
        return getSpaceBoundaryForLatin(right);
    }

    static boolean hasSpace(final String string) {
        return (string.indexOf(' ') > 0);
    }


    static int getSpaceBoundaryForLatin(final String string) {
        int begin = 0;
        while (begin < string.length()) {
            if (!Character.isLetter(string.charAt(begin))) {
                break;
            }
            ++begin;
        }
        if (begin >= string.length()) {
            return 1;
        }
        return begin + 1;
    }

    static int nextSentence(final String text, int start) {
        int position = start;
        while (position == start) {
            ++start;
            position = text.indexOf("。", start);
        }
        return position;
    }

    static int isSentenceBoundary(final String text) {
        for(String splitter: Splitters) {
            if (text.endsWith(splitter)) {
                return 1;
            }
        }
        return 0;
    }

}
