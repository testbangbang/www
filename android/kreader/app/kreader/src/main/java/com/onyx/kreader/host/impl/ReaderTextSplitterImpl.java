package com.onyx.kreader.host.impl;

import com.alibaba.fastjson.JSON;
import com.onyx.kreader.api.ReaderTextSplitter;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.ui.KReaderApp;
import org.apache.lucene.analysis.cn.AnalyzerAndroidWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by zhuzeng on 5/24/15.
 * javap -s com.onyx.reader.host.impl.ReaderTextSplitter
 */

public class ReaderTextSplitterImpl implements ReaderTextSplitter {

    private static class SentenceAnalyzeResult {
        private String word, left, right;
        private int leftBoundaryOfWord, rightBoundaryOfWord;

        private SentenceAnalyzeResult(String word, String left, String right,
                                      int leftBoundary, int rightBoundary) {
            this.word = word;
            this.left = left;
            this.right = right;
            this.leftBoundaryOfWord = leftBoundary;
            this.rightBoundaryOfWord = rightBoundary;
        }

        public static SentenceAnalyzeResult analyze(String word, String left, String right) {
            String sentence = (left + word + right).toLowerCase(Locale.getDefault());
            ArrayList<String> result = AnalyzerAndroidWrapper.analyze(sentence);
            Debug.d("analyze result: " + JSON.toJSONString(result));
            if (result.size() <= 0) {
                return new SentenceAnalyzeResult(word, left, right, 0, 0);
            }

            String target = "";
            int leftIndex = 0;
            for (String w : result) {
                int i = sentence.indexOf(w, leftIndex);
                if (i >= left.length()) {
                    if (target.indexOf(word) < 0) {
                        leftIndex = left.length();
                        target = w;
                    }
                    break;
                }
                leftIndex = i;
                target = w;
            }
            int rightIndex = leftIndex + target.length();
            int leftBoundary = Math.max(0, left.length() - leftIndex);
            int rightBoundary = rightIndex - left.length() - word.length();
            return new SentenceAnalyzeResult(word, left, right, leftBoundary, rightBoundary);
        }

        public boolean isSameSentence(String word, String left, String right) {
            return this.word.compareTo(word) == 0 &&
                    this.left.compareTo(left) == 0 &&
                    this.right.compareTo(right) == 0;
        }

        public int getLeftBoundaryOfWord() {
            return leftBoundaryOfWord;
        }

        public int getRightBoundaryOfWord() {
            return rightBoundaryOfWord;
        }
    }

    // TODO, use json later.
    static private final HashSet<Character> Splitters;
    static private ReaderTextSplitterImpl instance;
    static boolean hasSpace = false;

    static {
        Splitters = new HashSet<>();
        Splitters.add('。');
        Splitters.add('？');
        Splitters.add('！');
        Splitters.add('.');
        Splitters.add('!');
    }

    private SentenceAnalyzeResult analyzeResult = null;

    private ReaderTextSplitterImpl() {
        super();
    }

    public static ReaderTextSplitterImpl sharedInstance() {
        if (instance == null) {
            instance = new ReaderTextSplitterImpl();
            AnalyzerAndroidWrapper.init(KReaderApp.instance());
        }
        return instance;
    }

    // reverse string.
    public int getTextLeftBoundary(final String word, final String left, final String right) {
        final String w = normalizeString(word);
        final String l = normalizeString(left);
        final String r = normalizeString(right);
        if (analyzeResult == null || !analyzeResult.isSameSentence(w, l, r)) {
            analyzeResult = SentenceAnalyzeResult.analyze(w, l, r);
        }
        return analyzeResult.getLeftBoundaryOfWord();
    }

    public int getTextRightBoundary(final String word, final String left, final String right) {
        final String w = normalizeString(word);
        final String l = normalizeString(left);
        final String r = normalizeString(right);
        if (analyzeResult == null || !analyzeResult.isSameSentence(w, l, r)) {
            analyzeResult = SentenceAnalyzeResult.analyze(w, l, r);
        }
        return analyzeResult.getRightBoundaryOfWord();
    }

    @Override
    public int getTextSentenceBreakPoint(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Splitters.contains(text.charAt(i))) {
                Debug.d("getTextSentenceBreakPoint, find: " + text.charAt(i));
                return i;
            }
        }
        Debug.d("getTextSentenceBreakPoint, find nothing!");
        return text.length() - 1;
    }

    boolean hasSpace(final String string) {
        return (string.indexOf(' ') > 0);
    }

    private static String normalizeString(final String string) {
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
            if (!isAlpha(string.charAt(index))) {
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
            if (!isAlpha(string.charAt(index))) {
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

//    int isSentenceBoundary(final String text) {
//        for(String splitter: Splitters) {
//            if (text.endsWith(splitter)) {
//                return 1;
//            }
//        }
//        return 0;
//    }

    private static boolean isAlpha(char ch) {
        /**
         * The following defines which characters are included in these sets. The values are Unicode code points.
         * - ALPHA
         *		- 0x0041 - 0x007A Basic Latin
         *		- 0x00C0 - 0x00D6 Latin-1 Supplement
         *		- 0x00D8 - 0x00F6 Latin-1 Supplement
         *		- 0x00F8 - 0x00FF Latin-1 Supplement
         *		- 0x0100 - 0x017F Latin Extended-A
         *		- 0x0180 - 0x024F Latin Extended-B
         *		- 0x0386          Greek
         *		- 0x0388 - 0x03FF Greek
         *		- 0x0400 - 0x0481 Cyrillic
         *		- 0x048A - 0x04FF Cyrillic
         *		- 0x0500 - 0x052F Cyrillic Supplement
         *		- 0x1E00 - 0x1EFF Latin Extended Additional
         */
        int codepoint = (int)ch;
        return (0x0041 <= codepoint && codepoint <= 0x007A) ||
                (0x00C0 <= codepoint && codepoint <= 0x00D6) ||
                (0x00D8 <= codepoint && codepoint <= 0x00F6) ||
                (0x00F8 <= codepoint && codepoint <= 0x00FF) ||
                (0x0100 <= codepoint && codepoint <= 0x017F) ||
                (0x0180 <= codepoint && codepoint <= 0x024F) ||
                (0x0386 == codepoint) ||
                (0x0388 <= codepoint && codepoint <= 0x03FF) ||
                (0x0400 <= codepoint && codepoint <= 0x0481) ||
                (0x048A <= codepoint && codepoint <= 0x04FF) ||
                (0x0500 <= codepoint && codepoint <= 0x052F) ||
                (0x1E00 <= codepoint && codepoint <= 0x1EFF);

    }

}
