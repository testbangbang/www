package com.onyx.android.sdk.ui;

/*
 * Copyright (C) 2011 Micah Hainline
 * Copyright (C) 2012 Triposo
 * Copyright (C) 2013 Paul Imhoff
 * Copyright (C) 2014 Shahin Yousefi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A {@link android.widget.TextView} that ellipsizes more intelligently.
 * This class supports ellipsizing multiline text through setting {@code android:ellipsize}
 * and {@code android:maxLines}.
 * <p/>
 * Note: {@link android.text.TextUtils.TruncateAt#MARQUEE} ellipsizing type is not supported.
 */
public class EllipsizingTextView extends TextView {
    private static final CharSequence ELLIPSIS = "\u2026";
    private static final Pattern DEFAULT_END_PUNCTUATION
            = Pattern.compile("[\\.!?,;:\u2026]*$", Pattern.DOTALL);
    private final List<EllipsizeListener> mEllipsizeListeners = new ArrayList<EllipsizeListener>();
    private EllipsizeStrategy mEllipsizeStrategy;
    private boolean isEllipsized;
    private boolean isStale;
    private boolean programmaticChange;
    private CharSequence mFullText;
    private int mMaxLines;
    private static int defaultMaxLines = 3;
    private float mLineSpacingMult = 1.0f;
    private float mLineAddVertPad = 0.0f;

    /**
     * The end punctuation which will be removed when appending {@link #ELLIPSIS}.
     */
    private Pattern mEndPunctPattern;

    public EllipsizingTextView(Context context) {
        this(context, null);
    }


    public EllipsizingTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }


    public EllipsizingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,
                new int[]{android.R.attr.maxLines, android.R.attr.ellipsize}, defStyle, 0);
//        Todo: use DefaultMaxLines to replace Integer.MAX_VALUE,which avoid NullPointerException when user forget to set MaxLine param.
//        setMaxLines(a.getInt(0, Integer.MAX_VALUE));
        setMaxLines(a.getInt(0, defaultMaxLines));
        a.recycle();
        setEndPunctuationPattern(DEFAULT_END_PUNCTUATION);
    }

    public void setEndPunctuationPattern(Pattern pattern) {
        mEndPunctPattern = pattern;
    }

    public void addEllipsizeListener(EllipsizeListener listener) {
        if (listener == null) throw new NullPointerException();
        mEllipsizeListeners.add(listener);
    }

    public void removeEllipsizeListener(EllipsizeListener listener) {
        mEllipsizeListeners.remove(listener);
    }

    public boolean isEllipsized() {
        return isEllipsized;
    }

    /**
     * @return The maximum number of lines displayed in this {@link android.widget.TextView}.
     */
    public int getMaxLines() {
        return mMaxLines;
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        mMaxLines = maxLines;
        isStale = true;
    }

    /**
     * Determines if the last fully visible line is being ellipsized.
     *
     * @return {@code true} if the last fully visible line is being ellipsized;
     * otherwise, returns {@code false}.
     */
    public boolean ellipsizingLastFullyVisibleLine() {
        return mMaxLines == Integer.MAX_VALUE;
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        mLineAddVertPad = add;
        mLineSpacingMult = mult;
        super.setLineSpacing(add, mult);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!programmaticChange) {
            mFullText = text instanceof Spanned ? (Spanned) text : text;
            isStale = true;
        }
        super.setText(text, type);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (ellipsizingLastFullyVisibleLine()) isStale = true;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        if (ellipsizingLastFullyVisibleLine()) isStale = true;
    }

    @Override
    protected void onDraw( Canvas canvas) {
        if (isStale) resetText();
        super.onDraw(canvas);
    }

    /**
     * Sets the ellipsized text if appropriate.
     */
    private void resetText() {
        int maxLines = getMaxLines();
        CharSequence workingText = mFullText;
        boolean ellipsized = false;

        if (maxLines != -1) {
            if (mEllipsizeStrategy == null) setEllipsize(null);
            workingText = mEllipsizeStrategy.processText(mFullText);
            ellipsized = !mEllipsizeStrategy.isInLayout(mFullText);
        }

        if (!workingText.equals(getText())) {
            programmaticChange = true;
            try {
                setText(workingText);
            } finally {
                programmaticChange = false;
            }
        }

        isStale = false;
        if (ellipsized != isEllipsized) {
            isEllipsized = ellipsized;
            for (EllipsizeListener listener : mEllipsizeListeners) {
                listener.ellipsizeStateChanged(ellipsized);
            }
        }
    }

    /**
     * Causes words in the text that are longer than the view is wide to be ellipsized
     * instead of broken in the middle. Use {@code null} to turn off ellipsizing.
     * <p/>
     * Note: Method does nothing for {@link android.text.TextUtils.TruncateAt#MARQUEE}
     * ellipsizing type.
     *
     * @param where part of text to ellipsize
     */
    @Override
    public void setEllipsize(TruncateAt where) {
        if (where == null) {
            mEllipsizeStrategy = new EllipsizeNoneStrategy();
            return;
        }

        switch (where) {
            case END:
                mEllipsizeStrategy = new EllipsizeEndStrategy();
                break;
            case START:
                mEllipsizeStrategy = new EllipsizeStartStrategy();
                break;
            case MIDDLE:
                mEllipsizeStrategy = new EllipsizeMiddleStrategy();
                break;
            case MARQUEE:
            default:
                mEllipsizeStrategy = new EllipsizeNoneStrategy();
                break;
        }
    }

    /**
     * A listener that notifies when the ellipsize state has changed.
     */
    public interface EllipsizeListener {
        void ellipsizeStateChanged(boolean ellipsized);
    }

    /**
     * A base class for an ellipsize strategy.
     */
    private abstract class EllipsizeStrategy {
        /**
         * Returns ellipsized text if the text does not fit inside of the layout;
         * otherwise, returns the full text.
         *
         * @param text text to process
         * @return Ellipsized text if the text does not fit inside of the layout;
         * otherwise, returns the full text.
         */
        public CharSequence processText(CharSequence text) {
            return !isInLayout(text) ? createEllipsizedText(text) : text;
        }

        /**
         * Determines if the text fits inside of the layout.
         *
         * @param text text to fit
         * @return {@code true} if the text fits inside of the layout;
         * otherwise, returns {@code false}.
         */
        public boolean isInLayout(CharSequence text) {
            Layout layout = createWorkingLayout(text);
            return layout.getLineCount() <= getLinesCount();
        }

        /**
         * Creates a working layout with the given text.
         *
         * @param workingText text to create layout with
         * @return {@link android.text.Layout} with the given text.
         */
        protected Layout createWorkingLayout(CharSequence workingText) {
            return new StaticLayout(workingText, getPaint(),
                    getWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight(),
                    Alignment.ALIGN_NORMAL, mLineSpacingMult,
                    mLineAddVertPad, false /* includepad */);
        }

        /**
         * Get how many lines of text we are allowed to display.
         */
        protected int getLinesCount() {
            if (ellipsizingLastFullyVisibleLine()) {
                int fullyVisibleLinesCount = getFullyVisibleLinesCount();
                return fullyVisibleLinesCount == -1 ? 1 : fullyVisibleLinesCount;
            } else {
                return mMaxLines;
            }
        }

        /**
         * Get how many lines of text we can display so their full height is visible.
         */
        protected int getFullyVisibleLinesCount() {
            Layout layout = createWorkingLayout("");
            int height = getHeight() - getCompoundPaddingTop() - getCompoundPaddingBottom();
            int lineHeight = layout.getLineBottom(0);
            return height / lineHeight;
        }

        /**
         * Creates ellipsized text from the given text.
         *
         * @param fullText text to ellipsize
         * @return Ellipsized text
         */
        protected abstract CharSequence createEllipsizedText(CharSequence fullText);
    }

    /**
     * An {@link EllipsizingTextView.EllipsizeStrategy} that
     * does not ellipsize text.
     */
    private class EllipsizeNoneStrategy extends EllipsizeStrategy {
        @Override
        protected CharSequence createEllipsizedText(CharSequence fullText) {
            return fullText;
        }
    }

    /**
     * An {@link EllipsizingTextView.EllipsizeStrategy} that
     * ellipsizes text at the end.
     */
    private class EllipsizeEndStrategy extends EllipsizeStrategy {
        @Override
        protected CharSequence createEllipsizedText(CharSequence fullText) {
            Layout layout = createWorkingLayout(fullText);
            int cutOffIndex = layout.getLineEnd(mMaxLines - 1);
            int textLength = fullText.length();
            int cutOffLength = textLength - cutOffIndex;
            if (cutOffLength < ELLIPSIS.length()) {
                cutOffLength = ELLIPSIS.length();
            }
            String workingText = TextUtils.substring(fullText, 0, textLength - cutOffLength).trim();

            while (!isInLayout(stripEndPunctuation(workingText) + ELLIPSIS)) {
                int lastSpace = workingText.lastIndexOf(' ');
                if (lastSpace == -1) break;
                workingText = workingText.substring(0, lastSpace).trim();
            }

            workingText = stripEndPunctuation(workingText) + ELLIPSIS;
            SpannableStringBuilder dest = new SpannableStringBuilder(workingText);

            if (fullText instanceof Spanned) {
                TextUtils.copySpansFrom((Spanned) fullText, 0, workingText.length(), null, dest, 0);
            }
            return dest;
        }

        /**
         * Strips the end punctuation from a given text according to {@link #mEndPunctPattern}.
         *
         * @param workingText text to strip end punctuation from
         * @return Text without end punctuation.
         */
        public String stripEndPunctuation(CharSequence workingText) {
            return mEndPunctPattern.matcher(workingText).replaceFirst("");
        }
    }

    /**
     * An {@link EllipsizingTextView.EllipsizeStrategy} that
     * ellipsizes text at the start.
     */
    private class EllipsizeStartStrategy extends EllipsizeStrategy {
        @Override
        protected CharSequence createEllipsizedText(CharSequence fullText) {
            Layout layout = createWorkingLayout(fullText);
            int cutOffIndex = layout.getLineEnd(mMaxLines - 1);
            int textLength = fullText.length();
            int cutOffLength = textLength - cutOffIndex;
            if (cutOffLength < ELLIPSIS.length()) cutOffLength = ELLIPSIS.length();
            String workingText = TextUtils.substring(fullText, cutOffLength, textLength).trim();

            while (!isInLayout(ELLIPSIS + workingText)) {
                int firstSpace = workingText.indexOf(' ');
                if (firstSpace == -1) break;
                workingText = workingText.substring(firstSpace, workingText.length()).trim();
            }

            workingText = ELLIPSIS + workingText;
            SpannableStringBuilder dest = new SpannableStringBuilder(workingText);

            if (fullText instanceof Spanned) {
                TextUtils.copySpansFrom((Spanned) fullText, textLength - workingText.length(),
                        textLength, null, dest, 0);
            }
            return dest;
        }
    }

    static public int searchMostRight(final String text, final String pattern, int left, int right) {
        int position = text.indexOf(pattern, left);
        while (position > 0 && position < right) {
            int temp = text.indexOf(pattern, position + pattern.length());
            if (temp > 0 && temp < right) {
                position = temp;
                continue;
            }
            return position;
        }
        return right;
    }

    static public int next(final String text, final String pattern, int left) {
        int position = text.indexOf(pattern, left);
        if (position > 0) {
            return position;
        }
        return -1;
    }

    /**
     * An {@link EllipsizingTextView.EllipsizeStrategy} that
     * ellipsizes text in the middle of last line.
     */
    private class EllipsizeMiddleStrategy extends EllipsizeStrategy {
        @Override
        protected CharSequence createEllipsizedText(CharSequence fullText) {
            Layout layout = createWorkingLayout(fullText);
            int lineStart = layout.getLineStart(mMaxLines - 1);
            int lineEnd = layout.getLineEnd(mMaxLines - 1);
            int textLength = fullText.length();
            String workingText = fullText.toString();
            if (lineEnd + 1 >= textLength) {
                return destText(workingText, fullText);
            }

            // search most right space [lineStart, lineMiddle]
            // search most left space [end, textLength]
            int lineMiddle = (lineEnd + lineStart) / 2;
            int end = textLength - (lineEnd - lineStart);

            final String pattern = " ";
            lineMiddle = searchMostRight(workingText, pattern, lineStart, lineMiddle);
            int lastEnd = next(workingText, pattern, end);
            if (lastEnd >= textLength || lastEnd < 0) {
                lastEnd = end;
            }

            final String left = workingText.substring(0, lineMiddle);
            String right = TextUtils.substring(fullText, lastEnd, textLength);
            String text = left + ELLIPSIS + right;
            boolean hasSpace = true;
            int next = lastEnd;

            // remove some characters
            while (!isInLayout(text)) {
                // try to find next space, if not found, use character instead.
                if (hasSpace) {
                    next = next(workingText, pattern, lastEnd);
                }
                if (next > 0) {
                    lastEnd = next + pattern.length();
                } else {
                    hasSpace = false;
                    lastEnd++;
                }
                right = TextUtils.substring(fullText, lastEnd, textLength);
                text = left + ELLIPSIS + right;
            }
            return destText(text, fullText);
        }

        public CharSequence destText(final String workingText, CharSequence fullText) {
            SpannableStringBuilder dest = new SpannableStringBuilder(workingText);
            if (fullText instanceof Spanned) {
                TextUtils.copySpansFrom((Spanned) fullText, 0, workingText.length(), null, dest, 0);
            }
            return dest;
        }


    }
}


