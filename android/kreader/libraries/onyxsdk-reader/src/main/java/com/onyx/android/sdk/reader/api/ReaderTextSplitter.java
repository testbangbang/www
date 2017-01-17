package com.onyx.android.sdk.reader.api;

/**
 * Created by zhuzeng on 10/14/15.
 */
public interface ReaderTextSplitter {
    /**
     * boundary offset counting from right to left with param left
     *
     * @param character
     * @param left
     * @param right
     * @return
     */
    @SuppressWarnings("unused")
    int getTextLeftBoundary(final String character, final String left, final String right);

    /**
     * boundary offset counting from left to right with param right
     *
     * @param character
     * @param left
     * @param right
     * @return
     */
    @SuppressWarnings("unused")
    int getTextRightBoundary(final String character, final String left, final String right);

    boolean isAlphaOrDigit(final String ch);

    int getTextSentenceBreakPoint(final String text);

    /**
     * check if the given text is a single word
     *
     * @param text
     * @return
     */
    boolean isWord(String text);
}
