package com.neverland.engbook.allstyles;

import com.neverland.engbook.unicode.AlUnicode;

public class AlCSSControl {
    public static final int HTMLCSS_WORD_LEN					= 1024;

    public int			wordIndex;

    public int			tag;

    public int			countFiqure; // {}
    public int			countStandart; // ()
    public int			countQuad; // []

    public AlOneCSSPair	css_value = new AlOneCSSPair();


    public final StringBuilder	prop = new StringBuilder();

    public final StringBuilder	selector = new StringBuilder();
    public final StringBuilder	signature = new StringBuilder();
    public char[]		word = new char [HTMLCSS_WORD_LEN + 1];

    public AlCSSControl() {
        clear();
    }

    public void			clear() {
        clearWord();
        selector.setLength(0);
        signature.setLength(0);

        prop.setLength(0);

        css_value.v0 = css_value.m0 = css_value.v1 = css_value.m1 = 0x00;
        tag = 0x00;
        countFiqure = countStandart = countQuad = 0;
    }

    public void			clearWord() {
        wordIndex = 0x00;
        word[0] = 0x00;
    }

    public void			addCharAlways(char ch) {
        if (wordIndex < HTMLCSS_WORD_LEN) {
            word[wordIndex++] = ch;
            word[wordIndex] = 0x00;
        }
    }

    public void			addChar(char ch) {
        if (ch == 0x20) {
            if (wordIndex == 0x00)
                return;
            if (word[wordIndex - 1] == 0x20)
                return;
        }

        if (wordIndex < HTMLCSS_WORD_LEN - 1) {
            word[wordIndex++] = ch;
            word[wordIndex] = 0x00;
        }
    }

    public void			makeSignature(char ch) {
        if (AlUnicode.isCSSSpecial(ch)) {
            signature.append(ch);
        } else
        if (ch == 0x20) {
            if (signature.length() != 0 && signature.charAt(signature.length() - 1) != 0x20)
                signature.append(ch);
        } else {
            if (signature.length() == 0 || AlUnicode.isCSSSpecial(signature.charAt(signature.length() - 1)) || signature.charAt(signature.length() - 1) == 0x20)
            signature.append('s');
        }
    }
}
