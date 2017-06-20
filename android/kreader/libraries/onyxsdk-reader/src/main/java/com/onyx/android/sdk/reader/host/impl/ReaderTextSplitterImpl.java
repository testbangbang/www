package com.onyx.android.sdk.reader.host.impl;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.api.ReaderTextSplitter;
import org.apache.lucene.analysis.cn.AnalyzerAndroidWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by zhuzeng on 5/24/15.
 * javap -s com.onyx.reader.host.impl.ReaderTextSplitter
 */

public class ReaderTextSplitterImpl implements ReaderTextSplitter {

    public static final Class<?> TAG = ReaderTextSplitterImpl.class;

    private static class SentenceAnalyzeResult {
        private static class WordMatchLocation {
            int matchIndex;
            int offsetInMatchedWord;

            WordMatchLocation(int index, int offset) {
                matchIndex = index;
                offsetInMatchedWord = offset;
            }
        }

        private static class WordMatchResult {
            WordMatchLocation leftLocation;
            WordMatchLocation rightLocation;

            WordMatchResult(WordMatchLocation left, WordMatchLocation right) {
                leftLocation = left;
                rightLocation = right;
            }
        }

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

        private static WordMatchResult locateWordInAnalyzeResult(List<String> analyzeResult, String word, String left, String right) {
            // analyzed result will not contain white spaces, so we can use the trick of string matching
            // to directly locate word in analyzed result
            String condensedLeft = getMergedAnalyzeResultText(left);
            int leftIndex = condensedLeft.length();
            WordMatchLocation leftLocation = locateIndexInAnalyzeResult(analyzeResult, leftIndex);
            assert leftLocation != null;

            String condensedWord = getMergedAnalyzeResultText(word);
            int rightIndex = leftIndex + condensedWord.length() - 1;
            WordMatchLocation rightLocation = locateIndexInAnalyzeResult(analyzeResult, rightIndex);
            assert rightLocation != null;

            return new WordMatchResult(leftLocation, rightLocation);
        }

        private static String getMergedAnalyzeResultText(String text) {
            return mergeStringList(AnalyzerAndroidWrapper.analyze(text));
        }

        private static String mergeStringList(List<String> list) {
            StringBuilder merged = new StringBuilder();
            for (String s : list) {
                merged.append(s);
            }
            return merged.toString();
        }

        private static WordMatchLocation locateIndexInAnalyzeResult(List<String> result, int index) {
            int length = 0;
            for (int i = 0; i < result.size(); i++) {
                String str = result.get(i);
                length += str.length();
                if (length > index) {
                    return new WordMatchLocation(i, index - (length - str.length()));
                }
            }
            return null;
        }

        public static SentenceAnalyzeResult analyze(String word, String left, String right) {
            word = word.toLowerCase(Locale.getDefault());
            left = left.toLowerCase(Locale.getDefault());
            right = right.toLowerCase(Locale.getDefault());
            String sentence = left + word + right;
            ArrayList<String> analyzeResult = AnalyzerAndroidWrapper.analyze(sentence);
            if (analyzeResult.size() <= 0) {
                return new SentenceAnalyzeResult(word, left, right, 0, 0);
            }

            WordMatchResult matchResult = locateWordInAnalyzeResult(analyzeResult, word, left, right);
            int leftBoundary = Math.max(0, matchResult.leftLocation.offsetInMatchedWord);
            int rightIndexOfResult = matchResult.rightLocation.matchIndex;
            int rightBoundary = analyzeResult.get(rightIndexOfResult).length() - matchResult.rightLocation.offsetInMatchedWord - 1;
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
        }
        return instance;
    }

    // reverse string.
    public int getTextLeftBoundary(final String word, final String left, final String right) {
        final String w = normalizeString(word);
        final String l = normalizeString(left);
        final String r = normalizeString(right);

        if (word.length() > 0 && isAlphaOrDigit(String.valueOf(word.charAt(0))) ||
                !AnalyzerAndroidWrapper.isInitialized()) {
            return searchSpaceBoundaryForLatinFromRight(word, left);
        }

        if (analyzeResult == null || !analyzeResult.isSameSentence(w, l, r)) {
            analyzeResult = SentenceAnalyzeResult.analyze(w, l, r);
        }
        return analyzeResult.getLeftBoundaryOfWord();
    }

    public int getTextRightBoundary(final String word, final String left, final String right) {
        final String w = normalizeString(word);
        final String l = normalizeString(left);
        final String r = normalizeString(right);

        if (word.length() > 0 && isAlphaOrDigit(String.valueOf(word.charAt(word.length() - 1))) ||
                !AnalyzerAndroidWrapper.isInitialized()) {
            return searchSpaceBoundaryForLatinFromLeft(word, right);
        }

        if (analyzeResult == null || !analyzeResult.isSameSentence(w, l, r)) {
            analyzeResult = SentenceAnalyzeResult.analyze(w, l, r);
        }
        return analyzeResult.getRightBoundaryOfWord();
    }

    @Override
    public boolean isAlphaOrDigit(String ch) {
        if (StringUtils.isBlank(ch)) {
            return false;
        }
        boolean res = isAlpha(ch.charAt(0)) || Character.isDigit(ch.charAt(0));
        return res;
    }

    @Override
    public int getTextSentenceBreakPoint(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Splitters.contains(text.charAt(i))) {
                return i;
            }
        }
        return text.length() - 1;
    }

    @Override
    public boolean isWord(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        ArrayList<String> result = AnalyzerAndroidWrapper.analyze(normalizeString(text));
        return result.size() == 1;
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
        final int lastNonSpace = index;
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
        return string.length() - lastNonSpace;
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

    public static boolean isSentenceBreakSplitter(char ch) {
        return Splitters.contains(ch);
    }

    public static boolean isAlpha(char ch) {
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
