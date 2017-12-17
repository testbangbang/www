package com.neverland.engbook.allstyles;

import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level2.AlFormat;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.InternalFunc;

import java.io.UnsupportedEncodingException;

public class AlCSSHtml extends AlCSSStyles {

    public static String DEFAULT_CSS_HTML =
            "h1, title, .title, .book-title {font-family:fantasy; text-indent:0; text-shadow:1; font-size:1.44rem; text-align:center; margin-left:10%; margin-right:10%; hyphens:none;}" +
            "h2, .subtitle {font-family:fantasy; text-indent:0; font-size:1.2rem; text-align:center; margin-left:10%; margin-right:10%;}" +
            "h3, h4, h5, h6 {font-size:1.1rem; text-indent:0; text-align:center; margin-left:5%; margin-right:5%;}" +
            "q, blockquote, cite, .cite {font-size:0.92rem; text-align:justify; margin-left:5%; margin-right:5%;}" +
            "pre, code, tt {font-family:monospace; text-indent:0; font-size:0.83rem; text-align:left; white-space:pre; hyphens:none;}" +
            ".author, .text-author, .date {font-size:69%; text-indent:0; text-align:right; margin-right:10%;}" +
            "annotation {font-size:0.83rem; font-style:italic; text-align:left; margin-left:45%; margin-right:5%;}" +
            ".epigraph, .epigraf {font-size:0.83rem; text-align:right; text-indent:0; margin-left:55%;}" +
            ".poem, .stanza, .v {font-size:1rem; text-align:left; text-indent:0; --alreader-align-poem:1;}" +
            "p {margin-top:1%;}" +
            "table, tr {text-align:justify;}" +
            "body {text-indent:4em;}";

    public static String  DEFAULT_CSS_MOBI = DEFAULT_CSS_HTML;

    public static String  DEFAULT_CSS_EPUB = DEFAULT_CSS_HTML;

    public static String DEFAULT_CSS_FB3 =
            "title, book-title {font-family:fantasy; text-shadow:1; text-indent:0; font-size:1.44rem; text-align:center; margin-left:10%; margin-right:10%; hyphens:none;}" +
            "subtitle {font-family:fantasy; font-size:1.2rem; text-indent:0; text-align:center; margin-left:10%; margin-right:10%;}" +
            "h3, h4, h5, h6 {font-size:1.1rem; text-align:center; text-indent:0; margin-left:5%; margin-right:5%;}" +
            "cite {font-size:0.92rem; text-align:justify; margin-left:5%; margin-right:5%;}" +
            "pre, code {font-family:monospace; text-indent:0; font-size:0.83rem; text-align:left; white-space:pre; hyphens:none;}" +
            "subscription, author, text-author {font-size:69%; text-align:right; margin-right:10%; text-indent:0;}" +
            "annotation {font-size:0.83rem; font-style:italic; text-align:left; margin-left:45%; margin-right:5%;}" +
            "epigraph {font-size:0.83rem; text-align:right; margin-left:55%; text-indent:0;}" +
            "stanza {margin-bottom:2%; white-space:pre; text-indent:0;}" +
            "poem {font-size:1rem; text-align:left; text-indent:0; --alreader-align-poem:1; white-space:pre;}" +
            "p {margin-top:1%;}" +
            "table, tr {text-align:justify;}" +
            "body {text-indent:4em;}";

    public static String DEFAULT_CSS_FB2 =
            "title, book-title {font-family:fantasy; text-indent:0; text-shadow:1; font-size:1.44rem; text-align:center; margin-top:3%; margin-bottom:3%; margin-left:10%; margin-right:10%; hyphens:none;}" +
            "subtitle {font-size:1.2rem; text-align:center; text-indent:0; margin-top:2%; margin-bottom:2%; margin-left:10%; margin-right:10%;}" +
            "cite {font-size:0.92rem; text-align:justify; margin-top:1%; margin-bottom:1%; margin-left:5%; margin-right:5%;}" +
            "pre {font-family:monospace; font-size:0.83rem; text-indent:0; text-align:left; white-space:pre; margin-top:2%; margin-bottom:2%; hyphens:none;}" +
            "code {font-family:monospace; font-size:0.83rem; white-space:pre; hyphens:none;}" +
            "date, author, text-author {font-size:69%; letter-spacing:1; font-style:italic; text-indent:0; text-align:right; margin-top:1%; margin-bottom:1%; margin-right:5%;}" +
            "annotation {font-size:0.83rem; text-indent:4em; text-align:justify; margin-top:2%; margin-bottom:2%; margin-left:35%; margin-right:2%;}" +
            "epigraph {font-size:0.83rem; text-indent:0; text-align:right; margin-top:2%; margin-bottom:2%; margin-left:50%; margin-right:2%;}" +
            "poem {font-size:1rem; text-indent:0; margin-top:1%; margin-bottom:1%; text-align:left; white-space:pre; --alreader-align-poem:1;}" +
            "stanza {margin-bottom:2%; white-space:pre; text-indent:0;}" +
            "v {font-size:1rem; margin-top:0; white-space:pre; text-indent:0;}" +
            "p {margin-top:1%;}" +
            "table, tr {text-align:justify;}" +
            "body {text-indent:4%;}" +
            "sequence {font-size:0.83rem; margin-top:2%; letter-spacing:1; margin-bottom:2%; margin-left:35%; margin-right:2%; text-indent:0; text-align:right;}" +
            "image {font-size:0.83rem; text-align:center; letter-spacing:1; margin-bottom:1%; text-indent:0;}";

    public static final int TAG_TEXT_ALIGN	=						746232421;
    public static final int TAG_FONT_STYLE	=						-1923578189;
    public static final int TAG_FONT_WEIGHT	=						598800822;
    public static final int TAG_FONT_SIZE	=						-1586082113;
    public static final int TAG_FONT_FAMILY	=						108532386;
    public static final int TAG_PAGE_BREAK_BEFORE	=				649569931;
    public static final int TAG_TEXT_DECORATION	=					431477072;
    public static final int TAG_VERTICAL_ALIGN	=					-1559879186;
    public static final int TAG_PAGE_BREAK_AFTER	=				574262608;
    public static final int TAG_MARGIN	=							-1081309778;
    public static final int TAG_MARGIN_LEFT	=						941004998;
    public static final int TAG_MARGIN_RIGHT	=					-887955139;
    public static final int TAG_MARGIN_TOP	=						1970025654;
    public static final int TAG_MARGIN_BOTTOM	=					2086035242;
    public static final int TAG_VISIBILITY	=						1941332754;
    public static final int TAG_TEXT_SHADOW	=						-2125209152;
    public static final int TAG_WHITE_SPACE	=						-2006495646;
    public static final int TAG_HYPHENS	=							1381862785;
    public static final int TAG_TEXT_DECORATION_LINE	=			-1867296175;
    public static final int TAG_FONT_STRETCH	=					-1729357945;
    public static final int TAG_TEXT_INDENT	=						1889098060;
    public static final int TAG_LETTER_SPACING	=					-1988401764;
    public static final int TAG___ALREADER_ALIGN_POEM	=			1820069262;


    public static final int CSSHTML_STATE_SELECTOR	=			0x0000;
    public static final int CSSHTML_STATE_AT		=			0x0001;
    public static final int CSSHTML_STATE_VALUE_NAME =			   100;
    public static final int CSSHTML_STATE_VALUE_LIST =			0x0003;
    public static final int CSSHTML_STATE_AT_BODY	=			0x0004;
    public static final int CSSHTML_STATE_AT_END	=			0x0005;
    public static final int CSSHTML_STATE_MASK		=			0x00ff;

    public static final int CSSHTML_STATE_QUOTE		=			0x0100;
    public static final int CSSHTML_STATE_DQUOTE	=			0x0200;
    public static final int CSSHTML_STATE_QUOTE_MASK	=		0x0f00;

    public static final int CSSHTML_SET_EMPTY	=					0;
    public static final int CSSHTML_SET_FB2	=						1;
    public static final int CSSHTML_SET_EPUB	=					2;
    public static final int CSSHTML_SET_MOBI	=					3;
    public static final int CSSHTML_SET_HTML	=					4;
    public static final int CSSHTML_SET_DOCX	=					5;
    public static final int CSSHTML_SET_ODT	=						6;
    public static final int CSSHTML_SET_FB3	=						7;

    AlOneCSSPair internalCSSValue = new AlOneCSSPair();
    AlCSSControl control = new AlCSSControl();

    public AlCSSHtml() {

    }

    public void init(AlFormat f, int cp4files, int useSet) {

        format = f;
        cp4f = cp4files;
        supportFontSize = true;
        AlSetCSS a = new AlSetCSS(this);
        a.name = "::default";
        allSets.add(a);

        AlOneCSSPair p;

        switch (useSet) {
            case CSSHTML_SET_FB2:
                try {
                    parse(0, DEFAULT_CSS_FB2.getBytes("UTF-8"), -1, 0, TAL_CODE_PAGES.CP65001, null, -1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case CSSHTML_SET_FB3:
                try {
                    parse(0, DEFAULT_CSS_FB3.getBytes("UTF-8"), -1, 0, TAL_CODE_PAGES.CP65001, null, -1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case CSSHTML_SET_EPUB:
                try {
                    parse(0, DEFAULT_CSS_EPUB.getBytes("UTF-8"), -1, 0, TAL_CODE_PAGES.CP65001, null, -1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case CSSHTML_SET_HTML:
                try {
                    parse(0, DEFAULT_CSS_HTML.getBytes("UTF-8"), -1, 0, TAL_CODE_PAGES.CP65001, null, -1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case CSSHTML_SET_MOBI:
                isKindle = true;
                try {
                    parse(0, DEFAULT_CSS_MOBI.getBytes("UTF-8"), -1, 0, TAL_CODE_PAGES.CP65001, null, -1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }

        try {
            parse(0, DEFAULT_CSS_ALL.getBytes("UTF-8"), -1, 0, TAL_CODE_PAGES.CP65001, null, -1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        lastUsedSet = -1;
        enable = true;

    }

    protected void		applySelector(int usesets, AlCSSControl	control) {
        String[] arr_sign = control.signature.toString().split(",");

        if (arr_sign.length > 0) {
            for (int i = 0; i < arr_sign.length; i++) {
                applyOneSelector(usesets, arr_sign[i].trim(), control);
            }
        } else {
            applyOneSelector(usesets, control.signature.toString().trim(), control);
        }
    }

    protected void applyOneSelector(int usesets, String sign, AlCSSControl	control) {
        String work;
        int pos = control.selector.indexOf(",");
        if (pos != -1) {
            work = control.selector.substring(0, pos).trim();
            control.selector.delete(0, pos + 1);
        } else {
            work = control.selector.toString().trim();
        }

        work = work.trim();

        if (sign.contentEquals("s:s") && (work.contentEquals("p:first-letter") || work.contentEquals("p:notes"))) {
            sign = sign.substring(1);
            work = work.substring(1);
        }

        int tag = 0x00;
        if ("s".contentEquals(sign)) {
            for (int i = 0; i < work.length(); i++)
                tag = (tag * 31) + AlUnicode.toLower(work.charAt(i));
            allSets.get(usesets).addTAG(tag, work, control.css_value);
            return;
        } else
        if (sign.contentEquals(":s") && (work.contentEquals(":first-letter") || work.contentEquals(":notes"))) {
            for (int i = 0; i < work.length(); i++)
                tag = (tag * 31) + AlUnicode.toLower(work.charAt(i));
            allSets.get(usesets).addTAG(tag, work, control.css_value);
            return;
        }

        if (sign.indexOf(' ') != -1) {
            StringBuilder s = new StringBuilder(sign);

            for (int i = s.length() - 2; i >= 0; i--) {
                if (s.charAt(i) == 0x20) {
                    switch (s.charAt(i + 1)) {
                        case 0x20:
                        case '+':
                        case '>':
                            s.deleteCharAt(i);
                    }
                } else
                if (s.charAt(i) == '+' || s.charAt(i) == '>') {
                    if (s.charAt(i + 1) == 0x20)
                        s.deleteCharAt(i + 1);
                }
            }

            sign = s.toString();
        }

        //dirty, but effective
        if (sign.contentEquals(".s s.s")) {
            pos = work.indexOf(' ');
            work = work.substring(pos + 1);
            sign = "s.s";
        }

        if ("s.s".contentEquals(sign)) {
            pos = work.indexOf('.');
            for (int i = 0; i < pos; i++)
                tag = (tag * 31) + AlUnicode.toLower(work.charAt(i));
            allSets.get(usesets).addTAGCLASS(tag, work.substring(0, pos), work.substring(pos + 1), control.css_value);
            return;
        } else
        if (("*.s").contentEquals(sign) || (".s").contentEquals(sign)) {
            pos = work.indexOf('.');
            allSets.get(usesets).addCLASS(work.substring(pos + 1), control.css_value);
            return;
        }
    }

    @Override
    protected AlOneCSSPair parse(int usesets, byte[] data, int len, int state, int cp, String cFile, int setNum) {
        char ch, ch1;
        int saved = 0;
        //StringBuilder selector = new StringBuilder();
        char[] comment = new char[3];
        AlIntHolder j = new AlIntHolder(0);

        boolean useHTMLComment = false;

        control.clear();
        AlOneCSSPair tmp;

        if (len == -1)
            len = data.length;


        while (j.value < len) {
            ch = AlUnicode.byte2Wide(cp, data, j);
            if (ch == 0xfeff)
                continue;

            if ((state & CSSHTML_STATE_QUOTE_MASK) == 0) {
                switch (ch) {
                    case '@':
                        switch (state & CSSHTML_STATE_MASK) {
                            case CSSHTML_STATE_SELECTOR:
                                if (control.wordIndex == 0)
                                    state = CSSHTML_STATE_AT;
                            case CSSHTML_STATE_AT:
                                control.addChar(ch);
                                break;
                            case CSSHTML_STATE_AT_BODY:
                                control.addChar(ch);
                                break;
                            case CSSHTML_STATE_AT_END:
                                break;
                            case CSSHTML_STATE_VALUE_NAME:
                                control.addChar(ch);
                                break;
                            case CSSHTML_STATE_VALUE_LIST:
                                control.addChar(ch);
                                break;
                        }
                        break;
                    case '[':
                        control.countQuad++;
                        control.addChar(ch);
                        if ((state & CSSHTML_STATE_MASK) == CSSHTML_STATE_SELECTOR)
                            control.makeSignature(ch);
                        break;
                    case ']':
                        control.countQuad--;
                        if (control.countQuad < 0)
                            state = CSSHTML_STATE_AT_END;
                        control.addChar(ch);
                        if ((state & CSSHTML_STATE_MASK) == CSSHTML_STATE_SELECTOR)
                            control.makeSignature(ch);
                        break;
                    case '(':
                        control.countStandart++;
                        control.addChar(ch);
                        if ((state & CSSHTML_STATE_MASK) == CSSHTML_STATE_SELECTOR)
                            control.makeSignature(ch);
                        break;
                    case ')':
                        control.countStandart--;
                        if (control.countStandart < 0)
                            state = CSSHTML_STATE_AT_END;
                        control.addChar(ch);
                        if ((state & CSSHTML_STATE_MASK) == CSSHTML_STATE_SELECTOR)
                            control.makeSignature(ch);
                        break;
                    case '{':
                        control.countFiqure++;
                        switch (state & CSSHTML_STATE_MASK) {
                            case CSSHTML_STATE_SELECTOR:
                                state = CSSHTML_STATE_VALUE_NAME;
                                //control.selector.append(control.word);
                                control.selector.append(String.copyValueOf(control.word, 0, control.wordIndex));
                                control.clearWord();
                                control.tag = 0x00;
                                break;
                            case CSSHTML_STATE_AT_BODY:
                                control.selector.append((' '));
                            case CSSHTML_STATE_AT:
                                state = CSSHTML_STATE_AT_END;
                                control.selector.append(String.copyValueOf(control.word, 0, control.wordIndex));
                                control.clearWord();
                                break;
                            case CSSHTML_STATE_AT_END:
                                break;
                            case CSSHTML_STATE_VALUE_NAME:
                                break;
                            case CSSHTML_STATE_VALUE_LIST:
                                break;
                        }
                        break;
                    case '}':
                        control.countFiqure--;
                        if (control.countFiqure == 0) {
                            switch (state & CSSHTML_STATE_MASK) {
                                case CSSHTML_STATE_AT_END:
                                    // end at command

                                    //
                                    break;
                                case CSSHTML_STATE_VALUE_LIST:
                                    tmp = getValue(control.tag, control.word, control.wordIndex);
                                    control.css_value.v0 |= tmp.v0;
                                    control.css_value.m0 |= tmp.m0;
                                    control.css_value.v1 |= tmp.v1;
                                    control.css_value.m1 |= tmp.m1;
                                case CSSHTML_STATE_VALUE_NAME:
                                    if ((control.css_value.m0 != 0 || control.css_value.m1 != 0) && usesets != -1)
                                        applySelector(usesets, control);
                                    break;
                            }
                            state = CSSHTML_STATE_SELECTOR;
                            control.clear();
                        }
                        break;
                    case ';':
                        switch (state & CSSHTML_STATE_MASK) {
                            case CSSHTML_STATE_SELECTOR:
                                break;
                            case CSSHTML_STATE_AT:
                                if (control.countFiqure == 0) {
                                    state = CSSHTML_STATE_SELECTOR;
                                    control.clear();
                                }
                                else
                                    break;
                            case CSSHTML_STATE_AT_BODY:
                                // do command in selector and parameters in word
                                if ("@import".contentEquals(control.selector)) {
                                    control.selector.setLength(0);
                                    control.selector.append(control.word, 0, control.wordIndex);
                                    if (cFile != null)
                                        parseFile(control.selector.toString(), cFile, TAL_CODE_PAGES.CP65001, setNum);
                                }
                                //
                                state = CSSHTML_STATE_SELECTOR;
                                control.clear();
                                break;
                            case CSSHTML_STATE_AT_END:
                                break;
                            case CSSHTML_STATE_VALUE_NAME:
                                break;
                            case CSSHTML_STATE_VALUE_LIST:
                                tmp = getValue(control.tag, control.word, control.wordIndex);
                                control.css_value.v0 |= tmp.v0;
                                control.css_value.m0 |= tmp.m0;
                                control.css_value.v1 |= tmp.v1;
                                control.css_value.m1 |= tmp.m1;
                                control.clearWord();
                                control.tag = 0x00;
                                state = CSSHTML_STATE_VALUE_NAME;
                                break;
                        }
                        break;
                    case ':':
                        switch (state & CSSHTML_STATE_MASK) {
                            case CSSHTML_STATE_SELECTOR:
                                control.addChar(ch);
                                control.makeSignature(ch);
                                break;
                            case CSSHTML_STATE_AT:
                                control.addChar(ch);
                                break;
                            case CSSHTML_STATE_AT_BODY:
                                control.addChar(ch);
                                break;
                            case CSSHTML_STATE_AT_END:
                                break;
                            case CSSHTML_STATE_VALUE_NAME:
                                //#ifdef _DEBUG
                                control.prop.append(control.word, 0, control.wordIndex);
                                //#endif
                                control.clearWord();
                                state = CSSHTML_STATE_VALUE_LIST;
                                break;
                            case CSSHTML_STATE_VALUE_LIST:
                                control.addChar(ch);
                                break;
                        }
                        break;

                    case '\'':
                        state |= CSSHTML_STATE_QUOTE;
                        break;
                    case '\"':
                        state |= CSSHTML_STATE_DQUOTE;
                        break;
                    case '\\':
                        if (j.value < len - 1) {
                            char[] tmp0 = new char[7]; int i = 1;
                            ch = tmp0[0] = AlUnicode.byte2Wide(cp, data, j);
                            tmp0[1] = 0x00;
                            if (AlUnicode.isHEXDigit(tmp0[0])) {
                                while (i < 7) {
                                    saved = j.value;
                                    ch = AlUnicode.byte2Wide(cp, data, j);
                                    if (!AlUnicode.isHEXDigit(ch)) {
                                        if (ch > 0x20)
                                            j.value = saved;
                                        break;
                                    }
                                    tmp0[i++] = ch;
                                    tmp0[i] = 0x00;
                                    if (ch <= 0x20)
                                        break;
                                }
                                ch = (char) InternalFunc.str2int(String.copyValueOf(tmp0, 0, i), 16);
                            }
                            if (ch <= 0x20)
                                continue;
                            switch (state & CSSHTML_STATE_MASK) {
                                case CSSHTML_STATE_SELECTOR:
                                    control.addChar(ch);
                                    control.makeSignature(ch);
                                    break;
                                case CSSHTML_STATE_AT:
                                    control.addChar(ch);
                                    break;
                                case CSSHTML_STATE_AT_BODY:
                                    control.addChar(ch);
                                    break;
                                case CSSHTML_STATE_AT_END:
                                    break;
                                case CSSHTML_STATE_VALUE_NAME:
                                    control.addChar(ch);
                                    control.tag = (control.tag * 31) + AlUnicode.toLower(ch);
                                    break;
                                case CSSHTML_STATE_VALUE_LIST:
                                    control.addChar(ch);
                                    break;
                            }

                        } else
                            return null;
                        switch (state & CSSHTML_STATE_MASK) {
                            case CSSHTML_STATE_SELECTOR:
                                break;
                            case CSSHTML_STATE_AT:
                                break;
                            case CSSHTML_STATE_AT_BODY:
                                break;
                            case CSSHTML_STATE_AT_END:
                                break;
                            case CSSHTML_STATE_VALUE_NAME:
                                break;
                            case CSSHTML_STATE_VALUE_LIST:
                                break;
                        }
                        break;
                    case '<':
                        if ((state & CSSHTML_STATE_QUOTE_MASK) == 0 && j.value < len - 1) {
                            saved = j.value;
                            ch1 = AlUnicode.byte2Wide(cp, data, j);
                            if (ch1 == '!' && j.value < len - 1) {
                                ch1 = AlUnicode.byte2Wide(cp, data, j);
                                if (ch1 == '-' && j.value < len - 1) {
                                    ch1 = AlUnicode.byte2Wide(cp, data, j);
                                    if (ch1 == '-' && j.value < len - 1) {
                                        // wait end comment;
                                        comment[0] = comment[1] = comment[2] = 0x00;
                                        while (j.value < len - 1) {
                                            comment[0] = comment[1];
                                            comment[1] = comment[2];
                                            comment[2] = AlUnicode.byte2Wide(cp, data, j);
                                            if (comment[0] == '-' && comment[1] == '-' && comment[2] == '>')
                                                break;
                                        }
                                        continue;
                                    }
                                }
                            }
                            j.value = saved;
                        }
                        break;
                    case '/':
                        if ((state & CSSHTML_STATE_QUOTE_MASK) == 0 && j.value < len - 1) {
                            saved = j.value;
                            ch1 = AlUnicode.byte2Wide(cp, data, j);
                            if (ch1 == '*') {
                                // wait end comment;
                                comment[0] = comment[1] = 0x00;
                                while (j.value < len - 1) {
                                    comment[0] = comment[1];
                                    comment[1] = AlUnicode.byte2Wide(cp, data, j);
                                    if (comment[0] == '*' && comment[1] == '/')
                                        break;
                                }
                                continue;
                            }
                        }

                        switch (state & CSSHTML_STATE_MASK) {
                            case CSSHTML_STATE_SELECTOR:
                                break;
                            case CSSHTML_STATE_AT:
                                break;
                            case CSSHTML_STATE_AT_BODY:
                                break;
                            case CSSHTML_STATE_AT_END:
                                break;
                            case CSSHTML_STATE_VALUE_NAME:
                                break;
                            case CSSHTML_STATE_VALUE_LIST:
                                break;
                        }

                        break;
                    default:
                        if (ch <= 0x20) {
                            ch = 0x20;

                            switch (state & CSSHTML_STATE_MASK) {
                                case CSSHTML_STATE_SELECTOR:
                                    control.addChar(ch);
                                    control.makeSignature(ch);
                                    break;
                                case CSSHTML_STATE_AT:
                                    control.selector.append(String.copyValueOf(control.word, 0, control.wordIndex));
                                    control.clearWord();
                                    state = CSSHTML_STATE_AT_BODY;
                                    break;
                                case CSSHTML_STATE_AT_BODY:
                                    control.addChar(ch);
                                    break;
                                case CSSHTML_STATE_AT_END:
                                    break;
                                case CSSHTML_STATE_VALUE_NAME:
                                    control.addChar(ch);
                                    break;
                                case CSSHTML_STATE_VALUE_LIST:
                                    control.addChar(ch);
                                    break;
                            }
                        } else {
                            switch (state & CSSHTML_STATE_MASK) {
                                case CSSHTML_STATE_SELECTOR:
                                    control.addChar(ch);
                                    control.makeSignature(ch);
                                    break;
                                case CSSHTML_STATE_AT:
                                    control.addChar(ch);
                                    break;
                                case CSSHTML_STATE_AT_BODY:
                                    control.addChar(ch);
                                    break;
                                case CSSHTML_STATE_AT_END:
                                    break;
                                case CSSHTML_STATE_VALUE_NAME:
                                    control.addChar(ch);
                                    control.tag = (control.tag * 31) + AlUnicode.toLower(ch);
                                    break;
                                case CSSHTML_STATE_VALUE_LIST:
                                    control.addChar(ch);
                                    break;
                            }
                        }

                        break;
                }
            } else {

                switch (ch) {
                    case '\'':
                        if ((state & CSSHTML_STATE_QUOTE) != 0) {
                            state &= ~CSSHTML_STATE_QUOTE;
                            continue;
                        }
                        break;
                    case '\"':
                        if ((state & CSSHTML_STATE_DQUOTE) != 0) {
                            state &= ~CSSHTML_STATE_DQUOTE;
                            continue;
                        }
                }

                control.addCharAlways(ch);
                if ((state & CSSHTML_STATE_MASK) == CSSHTML_STATE_SELECTOR)
                    control.makeSignature(ch);
            }

        }

        if ((state & CSSHTML_STATE_MASK) == CSSHTML_STATE_VALUE_LIST) {
            tmp = getValue(control.tag, control.word, control.wordIndex);
            control.css_value.v0 |= tmp.v0;
            control.css_value.m0 |= tmp.m0;
            control.css_value.v1 |= tmp.v1;
            control.css_value.m1 |= tmp.m1;
        }

        return control.css_value;
    }

    protected void setMarginTToInternal(AlOneCSSNumberValue a) {
        if (a.tp == AlOneCSSNumberValue.CSS_NUM_PERCENT) {

        } else
        if (a.tp == AlOneCSSNumberValue.CSS_NUM_PX) {
            a.dval /= 16;
        } else {
            //a.dval *= 2;
        }

        if (a.dval < 0) {
            if (a.dval > AlOneCSS.MARG_MIN_VALUE)
                a.dval = AlOneCSS.MARG_MIN_VALUE;
            a.dval = AlOneCSS.MARG_MAX_VALUE - a.dval;

            return;
        } else
        if (a.dval > 0) {
            if (a.dval > AlOneCSS.MARG_MAX_VALUE)
                a.dval = AlOneCSS.MARG_MAX_VALUE;
        }

        long v = (long)(a.dval + 0.5f);

        internalCSSValue.m1 |= AlOneCSS.MARGTOP_MASK;
        internalCSSValue.v1 |= (v << AlOneCSS.MARGTOP_SHIFT);
    }

    protected void setMarginBToInternal(AlOneCSSNumberValue a) {
        if (a.tp == AlOneCSSNumberValue.CSS_NUM_PERCENT) {

        } else
        if (a.tp == AlOneCSSNumberValue.CSS_NUM_PX) {
            a.dval /= 16;
        } else {
            //a.dval *= 2;
        }

        if (a.dval < 0) {
            if (a.dval > AlOneCSS.MARG_MIN_VALUE)
                a.dval = AlOneCSS.MARG_MIN_VALUE;
            a.dval = AlOneCSS.MARG_MAX_VALUE - a.dval;

            return;
        } else
        if (a.dval > 0) {
            if (a.dval > AlOneCSS.MARG_MAX_VALUE)
                a.dval = AlOneCSS.MARG_MAX_VALUE;
        }

        long v = (long)(a.dval + 0.5f);

        internalCSSValue.m1 |= AlOneCSS.MARGBOTTOM_MASK;
        internalCSSValue.v1 |= (v << AlOneCSS.MARGBOTTOM_SHIFT);
    }

    protected void setMarginLToInternal(AlOneCSSNumberValue a) {
        if (a.tp == AlOneCSSNumberValue.CSS_NUM_PERCENT) {

        } else
        if (a.tp == AlOneCSSNumberValue.CSS_NUM_PX) {
            a.dval /= 8;
        } else {
            a.dval *= 2;
        }

        if (a.dval < 0) {
            if (a.dval > AlOneCSS.MARG_MIN_VALUE)
                a.dval = AlOneCSS.MARG_MIN_VALUE;
            a.dval = AlOneCSS.MARG_MAX_VALUE - a.dval;

            return;
        } else
        if (a.dval > 0) {
            if (a.dval > AlOneCSS.MARG_MAX_VALUE)
                a.dval = AlOneCSS.MARG_MAX_VALUE;
        }

        long v = (long)(a.dval + 0.5f);
        switch (a.tp) {
            case AlOneCSSNumberValue.CSS_NUM_PERCENT:
                internalCSSValue.m1 |= AlOneCSS.MARGLEFT_PERCENT_MASK;
                internalCSSValue.v1 |= (v << AlOneCSS.MARGLEFT_PERCENT_SHIFT);
                break;
            case AlOneCSSNumberValue.CSS_NUM_PX:
            case AlOneCSSNumberValue.CSS_NUM_SIMPLE:
            case AlOneCSSNumberValue.CSS_NUM_EM:
                internalCSSValue.m1 |= AlOneCSS.MARGLEFT_EM_MASK;
                internalCSSValue.v1 |= (v << AlOneCSS.MARGLEFT_EM_SHIFT);
                break;
        }
    }

    protected void setMarginRToInternal(AlOneCSSNumberValue a) {
        if (a.tp == AlOneCSSNumberValue.CSS_NUM_PERCENT) {

        } else
        if (a.tp == AlOneCSSNumberValue.CSS_NUM_PX) {
            a.dval /= 8;
        } else {
            a.dval *= 2;
        }

        if (a.dval < 0) {
            if (a.dval > AlOneCSS.MARG_MIN_VALUE)
                a.dval = AlOneCSS.MARG_MIN_VALUE;
            a.dval = AlOneCSS.MARG_MAX_VALUE - a.dval;

            return;
        } else
        if (a.dval > 0) {
            if (a.dval > AlOneCSS.MARG_MAX_VALUE)
                a.dval = AlOneCSS.MARG_MAX_VALUE;
        }

        long v = (long)(a.dval + 0.5f);
        switch (a.tp) {
            case AlOneCSSNumberValue.CSS_NUM_PERCENT:
                internalCSSValue.m1 |= AlOneCSS.MARGRIGHT_PERCENT_MASK;
                internalCSSValue.v1 |= (v << AlOneCSS.MARGRIGHT_PERCENT_SHIFT);
                break;
            case AlOneCSSNumberValue.CSS_NUM_PX:
            case AlOneCSSNumberValue.CSS_NUM_SIMPLE:
            case AlOneCSSNumberValue.CSS_NUM_EM:
                internalCSSValue.m1 |= AlOneCSS.MARGRIGHT_EM_MASK;
                internalCSSValue.v1 |= (v << AlOneCSS.MARGRIGHT_EM_SHIFT);
                break;
        }
    }

    protected void setMarginIToInternal(AlOneCSSNumberValue a) {
        if (a.tp == AlOneCSSNumberValue.CSS_NUM_PERCENT) {

        } else
        if (a.tp == AlOneCSSNumberValue.CSS_NUM_PX) {
            a.dval /= 8;
        } else {
            a.dval *= 2;
        }

        if (a.dval < 0) {
            if (a.dval > AlOneCSS.MARG_MIN_VALUE)
                a.dval = AlOneCSS.MARG_MIN_VALUE;
            a.dval = AlOneCSS.MARG_MAX_VALUE - a.dval;

            return;
        } else
        if (a.dval > 0) {
            if (a.dval > AlOneCSS.MARG_MAX_VALUE)
                a.dval = AlOneCSS.MARG_MAX_VALUE;
        }

        long v = (long)(a.dval + 0.5f);
        switch (a.tp) {
            case AlOneCSSNumberValue.CSS_NUM_PERCENT:
                internalCSSValue.m1 |= AlOneCSS.INDENT_MASK;
                internalCSSValue.v1 |= (v << AlOneCSS.INDENT_SHIFT);
                break;
            case AlOneCSSNumberValue.CSS_NUM_PX:
            case AlOneCSSNumberValue.CSS_NUM_SIMPLE:
            case AlOneCSSNumberValue.CSS_NUM_EM:
                internalCSSValue.m1 |= AlOneCSS.INDENT_MASK;
                internalCSSValue.v1 |= (v << AlOneCSS.INDENT_SHIFT) | AlOneCSS.INDENT_VALUE_EM;
                break;
        }
    }

    private final StringBuilder valS = new StringBuilder();
    private final StringBuilder vv = new StringBuilder();
    private final AlOneCSSNumberValue[] adouble = new AlOneCSSNumberValue[4];

    protected AlOneCSSPair getValue(int tag, char[] val, int len) {
        int i, cnt;

        internalCSSValue.clear();

        if (adouble[0] == null) {
            adouble[0] = new AlOneCSSNumberValue();
            adouble[1] = new AlOneCSSNumberValue();
            adouble[2] = new AlOneCSSNumberValue();
            adouble[3] = new AlOneCSSNumberValue();
        }

        while ((len--) > 0) {
            if (val[len] == 0x20)
                val[len] = 0x00; else break;
        }

        valS.setLength(0);
        valS.append(String.copyValueOf(val, 0, len + 1).trim());

        switch (tag) {
            case TAG_VISIBILITY:
                if ("inherit".contentEquals(valS)) {

                } else {
                    internalCSSValue.m0 |= AlOneCSS.HIDDEN_MASK;
                    if ("hidden".contentEquals(valS) ||
                        "collapse".contentEquals(valS)) {
                        internalCSSValue.v1 |= AlOneCSS.HIDDEN_MASK;
                    } else {

                    }
                }
                break;
            case TAG_TEXT_SHADOW:
                internalCSSValue.m0 = AlOneCSS.SHADOW_MASK;
                if ("none".contentEquals(valS)) {
                    internalCSSValue.v0 = 0L;
                } else {
                    internalCSSValue.v0 = AlOneCSS.SHADOW_MASK;
                }
                break;
            case TAG_HYPHENS:
                internalCSSValue.m0 = AlOneCSS.NOHYPH_MASK;
                if ("none".contentEquals(valS)) {
                    internalCSSValue.v0 = AlOneCSS.NOHYPH_MASK;
                } else {
                    internalCSSValue.v0 = 0L;
                }
                break;
            case TAG_TEXT_DECORATION_LINE:		//
            case TAG_TEXT_DECORATION:		//
                if ("none".contentEquals(valS)) {
                    internalCSSValue.m0 = AlOneCSS.UNDER_MASK;
                    internalCSSValue.v0 = 0L;
                } else
                if ("line-through".contentEquals(valS) ||
                    "strike".contentEquals(valS)) {
                    internalCSSValue.m0 = AlOneCSS.STRIKE_MASK;
                    internalCSSValue.v0 = AlOneCSS.STRIKE_MASK;
                } else
                if ("underline".contentEquals(valS)) {
                    internalCSSValue.m0 = AlOneCSS.UNDER_MASK;
                    internalCSSValue.v0 = AlOneCSS.UNDER_MASK;
                } else {

                }
                break;
            case TAG_MARGIN:

                vv.setLength(0);
                vv.append(valS.toString().trim());

                {

                    i = 0;
                    while (true) {
                        if (vv.length() == 0)
                            break;

                        cnt = AlOneCSSNumberValue.scan(adouble[i], vv);
                        if (adouble[i++].tp == AlOneCSSNumberValue.CSS_NUM_UNKNOWN) {
                            i = 0;
                            break;
                        }

                        if (i > 3) {
                            i = 4;
                            break;
                        }

                        if (cnt == vv.length())
                            break;
                        vv.delete(0, cnt);
                    }
                }

                switch (i) {
                    case 1:
                        setMarginLToInternal(adouble[0]);
                        setMarginRToInternal(adouble[0]);
                        setMarginTToInternal(adouble[0]);
                        setMarginBToInternal(adouble[0]);
                        break;
                    case 2:
                        setMarginTToInternal(adouble[0]);
                        setMarginBToInternal(adouble[0]);
                        setMarginLToInternal(adouble[1]);
                        setMarginRToInternal(adouble[1]);
                        break;
                    case 3:
                        setMarginTToInternal(adouble[0]);
                        setMarginLToInternal(adouble[1]);
                        setMarginRToInternal(adouble[1]);
                        setMarginBToInternal(adouble[2]);
                        break;
                    case 4:
                        setMarginTToInternal(adouble[0]);
                        setMarginRToInternal(adouble[1]);
                        setMarginBToInternal(adouble[2]);
                        setMarginLToInternal(adouble[3]);
                        break;
                }
                break;
            case TAG_MARGIN_LEFT:
                vv.setLength(0);
                vv.append(valS.toString().trim());
                AlOneCSSNumberValue.scan(adouble[0], vv);
                setMarginLToInternal(adouble[0]);
                break;
            case TAG_MARGIN_RIGHT:
                vv.setLength(0);
                vv.append(valS.toString().trim());
                AlOneCSSNumberValue.scan(adouble[0], vv);
                setMarginRToInternal(adouble[0]);
                break;
            case TAG_MARGIN_TOP:
                vv.setLength(0);
                vv.append(valS.toString().trim());
                AlOneCSSNumberValue.scan(adouble[0], vv);
                setMarginTToInternal(adouble[0]);
                break;
            case TAG_MARGIN_BOTTOM:
                vv.setLength(0);
                vv.append(valS.toString().trim());
                AlOneCSSNumberValue.scan(adouble[0], vv);
                setMarginBToInternal(adouble[0]);
                break;
            case TAG_LETTER_SPACING:
                if ("inherit".contentEquals(valS)) {

                } else
                if ("normal".contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.RAZR_MASK;
                } else {
                    internalCSSValue.m0 |= AlOneCSS.RAZR_MASK;
                    vv.setLength(0);
                    vv.append(valS.toString().trim());
                    AlOneCSSNumberValue.scan(adouble[0], vv);
                    if (adouble[0].dval >= 0.5) {
                        internalCSSValue.v0 |= AlOneCSS.RAZR_MASK;
                    }
                }
                break;
            case TAG_TEXT_INDENT:
                vv.setLength(0);
                vv.append(valS.toString().trim());
                AlOneCSSNumberValue.scan(adouble[0], vv);
                if (adouble[0].ival >= 0)
                    setMarginIToInternal(adouble[0]);
                break;


            case TAG_PAGE_BREAK_BEFORE:		//649569931
                internalCSSValue.m1 |= AlOneCSS.PAGEBREAKBEFORE_MASK;
                if ("always".contentEquals(valS)) {
                    internalCSSValue.v1 |= AlOneCSS.PAGEBREAKBEFORE_MASK;
                }
                break;
            case TAG_PAGE_BREAK_AFTER:		//649569931
                internalCSSValue.m1 |= AlOneCSS.PAGEBREAKAFTER_MASK;
                if ("always".contentEquals(valS)) {
                    internalCSSValue.v1 |= AlOneCSS.PAGEBREAKAFTER_MASK;
                }
            break;
            case TAG_VERTICAL_ALIGN:
                if ("sub".contentEquals(valS)) {
                    internalCSSValue.v0 |= AlOneCSS.SUB_MASK;
                    internalCSSValue.m0 |= AlOneCSS.SUB_MASK;
                } else
                if ("super".contentEquals(valS)) {
                    internalCSSValue.v0 |= AlOneCSS.SUP_MASK;
                    internalCSSValue.m0 |= AlOneCSS.SUP_MASK;
                } else {

                }
                break;
            case TAG_TEXT_ALIGN:			//746232421
                if ("inherit".contentEquals(valS)) {

                } else
                if ("center".contentEquals(valS)) {
                    internalCSSValue.v1 |= AlOneCSS.JUST_CENTER;
                    internalCSSValue.m1 |= AlOneCSS.JUST_MASK;
                } else
                if ("left".contentEquals(valS) || "start".contentEquals(valS)) {
                    internalCSSValue.v1 |= AlOneCSS.JUST_LEFT;
                    internalCSSValue.m1 |= AlOneCSS.JUST_MASK;
                } else
                if ("right".contentEquals(valS) || "end".contentEquals(valS)) {
                    internalCSSValue.v1 |= AlOneCSS.JUST_RIGHT;
                    internalCSSValue.m1 |= AlOneCSS.JUST_MASK;
                } else {
                    internalCSSValue.v1 |= AlOneCSS.JUST_NONE;
                    internalCSSValue.m1 |= AlOneCSS.JUST_MASK;
                }
                break;
            case TAG_WHITE_SPACE:
                if ("normal".contentEquals(valS) ||
                    "nowrap".contentEquals(valS) ||
                    "pre-line".contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.PRESERVE_SPACE;
                    internalCSSValue.v0 |= 0;
                } else
                if ("pre".contentEquals(valS) ||
                    "pre-wrap".contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.PRESERVE_SPACE;
                    internalCSSValue.v0 |= AlOneCSS.PRESERVE_SPACE;
                }
                break;
            case TAG_FONT_STYLE:			//-1923578189
                if ("inherit".contentEquals(valS)) {

                } else {
                    internalCSSValue.m0 |= AlOneCSS.ITALIC_MASK;
                    if ("italic".contentEquals(valS) ||
                        "oblique".contentEquals(valS)) {
                        internalCSSValue.v0 |= AlOneCSS.ITALIC_MASK;
                    }
                }
                break;
            case TAG_FONT_WEIGHT:			//598800822
                internalCSSValue.m0 |= AlOneCSS.BOLD_MASK;
                if ("bold".contentEquals(valS) ||
                    "700".contentEquals(valS) ||
                    "800".contentEquals(valS) ||
                    "900".contentEquals(valS)) {
                    internalCSSValue.v0 |= AlOneCSS.BOLD_MASK;
                }
                break;
            case TAG_FONT_FAMILY:
                if ("inherit".contentEquals(valS)) {

                } else
                if ("initial".contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.FONTTYPE_MASK;
                    internalCSSValue.v0 |= AlOneCSS.FONTTYPE_TEXT;
                } else
                if ("monospace".contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.CODE_MASK;
                    internalCSSValue.v0 |= AlOneCSS.CODE_MASK;
                } else
                if ("fantasy".contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.FONTTYPE_MASK;
                    internalCSSValue.v0 |= AlOneCSS.FONTTYPE_FLET;
                } else {
                    internalCSSValue.m0 |= AlOneCSS.FONTTYPE_MASK;
                    internalCSSValue.v0 |= AlOneCSS.FONTTYPE_TEXT;
                }
                break;
            case TAG_FONT_STRETCH:
/*
		font-stretch

		<span style="font-stretch: ultra-condensed">Б</span>
		<span style="font-stretch: extra-condensed">Б</span>
		<span style="font-stretch: condensed">Б</span>
		<span style="font-stretch: semi-condensed">Б</span>
		<span style="font-stretch: normal">Б</span>
		<span style="font-stretch: semi-expanded">Б</span>
		<span style="font-stretch: expanded">Б</span>
		<span style="font-stretch: extra-expanded">Б</span>
		<span style="font-stretch: ultra-expanded">Б</span>

		*/
                break;


            case TAG___ALREADER_ALIGN_POEM:
                internalCSSValue.m1 |= AlOneCSS.JUSTPOEM_MASK;
                if ("1".contentEquals(valS)) {
                    internalCSSValue.v1 |= AlOneCSS.JUSTPOEM_MASK;
                }
                break;
            case TAG_FONT_SIZE:
                if (("xx-small").contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                    internalCSSValue.v0 |= AlOneCSS.FONTSIZE_ABSOLUTE | AlOneCSS.FONTSIZE_MINUS3;
                } else
                if (("x-small").contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                    internalCSSValue.v0 |= AlOneCSS.FONTSIZE_ABSOLUTE | AlOneCSS.FONTSIZE_MINUS2;
                } else
                if (("small").contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                    internalCSSValue.v0 |= AlOneCSS.FONTSIZE_ABSOLUTE | AlOneCSS.FONTSIZE_MINUS1;
                } else
                if (("medium").contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                    internalCSSValue.v0 |= AlOneCSS.FONTSIZE_ABSOLUTE | AlOneCSS.FONTSIZE_NORMAL;
                } else
                if (("large").contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                    internalCSSValue.v0 |= AlOneCSS.FONTSIZE_ABSOLUTE | AlOneCSS.FONTSIZE_PLUS1;
                } else
                if (("x-large").contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                    internalCSSValue.v0 |= AlOneCSS.FONTSIZE_ABSOLUTE | AlOneCSS.FONTSIZE_PLUS2;
                } else
                if (("xx-large").contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                    internalCSSValue.v0 |= AlOneCSS.FONTSIZE_ABSOLUTE | AlOneCSS.FONTSIZE_PLUS3;
                } else
                if (("larger").contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                    internalCSSValue.v0 |= AlOneCSS.FONTSIZE_PLUS1;
                } else
                if (("smaller").contentEquals(valS)) {
                    internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                    internalCSSValue.v0 |= AlOneCSS.FONTSIZE_MINUS1;
                } else {
                    vv.setLength(0);
                    vv.append(valS.toString().trim());
                    
                    AlOneCSSNumberValue.scan(adouble[0], vv);

                    switch (adouble[0].tp) {
                        case AlOneCSSNumberValue.CSS_NUM_REM:
                            if (adouble[0].dval > 0) {
                                i = (int)(adouble[0].dval * 100 + 0.5f);
                                if (i < 20) { i = 20; } else if (i > 511) { i = 511; }
                                internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                                internalCSSValue.v0 |= AlOneCSS.FONTSIZE_ABSOLUTE | (((long)i * 100) << AlOneCSS.FONTSIZE_VALUE_SHIFT);
                            }
                            return internalCSSValue;
                        case AlOneCSSNumberValue.CSS_NUM_SIMPLE:
                        case AlOneCSSNumberValue.CSS_NUM_EM:
                            if (adouble[0].dval > 0) {
                                i = (int)(adouble[0].dval * 100 + 0.5f);
                                if (i < 20) { i = 20; } else if (i > 511) { i = 511; }
                                internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                                internalCSSValue.v0 |= /*AlOneCSS.FONTSIZE_ABSOLUTE | */(((long)i * 100) << AlOneCSS.FONTSIZE_VALUE_SHIFT);
                            }
                            return internalCSSValue;
                        case AlOneCSSNumberValue.CSS_NUM_PERCENT:
                            if (adouble[0].dval > 0) {
                                i = (int)(adouble[0].dval + 0.5f);
                                if (i < 20) { i = 20; } else if (i > 511) { i = 511; }
                                internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                                internalCSSValue.v0 |= (((long)i * 100) << AlOneCSS.FONTSIZE_VALUE_SHIFT);
                            }
                            return internalCSSValue;
                        case AlOneCSSNumberValue.CSS_NUM_PX:
                            if (adouble[0].dval > 0) {
                                i = (int)(adouble[0].dval * 100 / 16 + 0.5f);
                                if (i < 20) { i = 20; } else if (i > 511) { i = 511; }
                                internalCSSValue.m0 |= AlOneCSS.FONTSIZE_MASK_ALL;
                                internalCSSValue.v0 |= AlOneCSS.FONTSIZE_ABSOLUTE | (((long)i * 100) << AlOneCSS.FONTSIZE_VALUE_SHIFT);
                            }
                            return internalCSSValue;
                    }
                }

                break;

            //case TAG_TEXT_DECORATION:		//431477072
            //	if (AlUnicode::equalIgnoreCase(U16("line-through"), val) ||
            //		AlUnicode::equalIgnoreCase(U16("strike"), val)) {
            //		res = AlOneCSS::MASK_STRIKE | AlOneCSS::VALUE_BOLD;
            //	} else
            //	if (AlUnicode::equalIgnoreCase(U16("underline"), val)) {
            //		res = AlOneCSS::MASK_UNDER | AlOneCSS::VALUE_UNDER;
            //	} else
            //	if (AlUnicode::equalIgnoreCase(U16("none"), val)) {
            //		res = AlOneCSS::MASK_UNDER | AlOneCSS::MASK_STRIKE;
            //	}
            //	break;
        }

        return internalCSSValue;
    }


}
