package com.neverland.engbook.util;

import com.neverland.engbook.unicode.AlUnicode;

import java.util.ArrayList;

public class AlArabicReverse {

    private final AlOneItem arabicItem = new AlOneItem();
    private final ArrayList<AlArabicWord> token = new ArrayList<>();
    private final StringBuilder sbtest = new StringBuilder();

    private void reverseArabicWord(AlOneItem item, int start, int stop) {

        int token_length, place_new_start, place_old_start;
        //long new_style;
        int last_position = token.get(stop).end;

        boolean needStyle = true;
        for (int i = start; i <= stop; i++) {

            AlArabicWord a = token.get(i);
            for (int j = a.start; j <= a.end; j++) {

                if (needStyle) {
                    item.style[j] |= AlStyles.SL_CHINEZEADJUST;
                } else {
                    item.style[j] &= ~AlStyles.SL_CHINEZEADJUST;
                }
            }

            /*if (a.type == AlArabicWord.ARABIC_WORD_TYPE_ARABIC) {
                new_style = item.style[a.start] & 0x03;
                for (int j = a.start + 1; j <= a.end; j++) {
                    item.style[j] &= ~0x03;
                    item.style[j] |= new_style;
                }
            }*/

            if (a.type == AlArabicWord.ARABIC_WORD_TYPE_PUNCTO) {
                switch (item.text[a.start]) {
                    case '(': item.text[a.start] = ')'; break;
                    case ')': item.text[a.start] = '('; break;
                    case '[': item.text[a.start] = ']'; break;
                    case ']': item.text[a.start] = '['; break;
                    case '{': item.text[a.start] = '}'; break;
                    case '}': item.text[a.start] = '{'; break;
                    case 0x0ab: item.text[a.start] = 0x0bb; break;
                    case 0x0bb: item.text[a.start] = 0x0ab; break;
                }
            }

            needStyle = !needStyle;
        }

        for (int i = start; i <= stop; i++) {
            token_length = token.get(i).end - token.get(i).start + 1;
            place_new_start = last_position - token_length + 1;
            place_old_start = token.get(i).start;

            if (token_length == 1) {
                arabicItem.text[place_new_start] = item.text[place_old_start];
                arabicItem.pos[place_new_start] = item.pos[place_old_start];
                arabicItem.style[place_new_start] = item.style[place_old_start];
                arabicItem.width[place_new_start] = item.width[place_old_start];
            } else {
                System.arraycopy(item.text, place_old_start, arabicItem.text, place_new_start, token_length);
                System.arraycopy(item.pos, place_old_start, arabicItem.pos, place_new_start, token_length);
                System.arraycopy(item.style, place_old_start, arabicItem.style, place_new_start, token_length);
                System.arraycopy(item.width, place_old_start, arabicItem.width, place_new_start, token_length);
            }

            last_position -= token_length;
        }

        place_new_start = token.get(start).start;
        token_length = token.get(stop).end - token.get(start).start + 1;

        System.arraycopy(arabicItem.text, place_new_start, item.text, place_new_start, token_length);
        System.arraycopy(arabicItem.pos, place_new_start, item.pos, place_new_start, token_length);
        System.arraycopy(arabicItem.style, place_new_start, item.style, place_new_start, token_length);
        System.arraycopy(arabicItem.width, place_new_start, item.width, place_new_start, token_length);

        for (int i = 0; i < token_length; i++) {
            item.style[place_new_start + i] &= ~AlStyles.SL_MARKFIRTSTLETTER0;
        }
    }


    private void scanArabicWord(AlOneItem item) {
        int start = -1, stop, arabic_count = 0, last_space = -1;
        for (int i = 0; i < token.size(); i++) {

            if (start != -1 && token.get(i).type == AlArabicWord.ARABIC_WORD_TYPE_ARABIC) {
                arabic_count++;
                last_space = -1;
            }

            if (start == -1 && token.get(i).type == AlArabicWord.ARABIC_WORD_TYPE_ARABIC) {
                start = i;
                f1: while (start > 0) {
                    switch (token.get(start - 1).type) {
                        case AlArabicWord.ARABIC_WORD_TYPE_SPACE:
                        case AlArabicWord.ARABIC_WORD_TYPE_NORMAL:
                            break f1;
                        case AlArabicWord.ARABIC_WORD_TYPE_PUNCTO:
                            switch (item.text[token.get(start - 1).start]) {
                                case '>' : case '<':
                                    break f1;
                            }
                            break;
                    }
                    start--;
                }
                arabic_count = 1;
            } else
            if (start != -1 && token.get(i).type == AlArabicWord.ARABIC_WORD_TYPE_SPACE) {
                last_space = i;
            } else
            if (start != -1 && token.get(i).type == AlArabicWord.ARABIC_WORD_TYPE_PUNCTO) {
                switch (item.text[token.get(i).start]) {
                    case '>' : case '<':
                        if (last_space == -1)
                            last_space = i;
                        break;
                }
            } else
            if (start != -1 && token.get(i).type == AlArabicWord.ARABIC_WORD_TYPE_NORMAL) {
                stop = last_space != -1 ? last_space - 1 : i - 1;
                if (arabic_count > 1)
                    reverseArabicWord(item, start, stop);
                start = -1;
            }

        }

        if (start != -1) {
            stop = token.size() - 1;
            if (arabic_count > 1)
                reverseArabicWord(item, start, stop);
        }

    }

    public final boolean scanArabic(AlOneItem oi) {
        boolean res = false;

        int i, len = oi.count, newtp, curtp = AlArabicWord.ARABIC_WORD_TYPE_NONE;
        char ch;
        boolean normalPresent = false;

        token.clear();
        AlArabicWord a = null;

        for (i = 0; i < len; i++) {
            ch = oi.text[i];

            if (AlUnicode.isArabic(ch)) {
                newtp = AlArabicWord.ARABIC_WORD_TYPE_ARABIC;
            } else
            if (AlUnicode.isLetter(ch)) {
                newtp = AlArabicWord.ARABIC_WORD_TYPE_NORMAL;
                normalPresent = true;
            } else
            if (AlUnicode.isDecDigit(ch)) {
                newtp = AlArabicWord.ARABIC_WORD_TYPE_DIGIT;
            } else
            if (ch < 0x20) {
                newtp = AlArabicWord.ARABIC_WORD_TYPE_OTHER;
            } else
            if (ch == 0x20) {
                newtp = AlArabicWord.ARABIC_WORD_TYPE_SPACE;
            } else
            {
                newtp = AlArabicWord.ARABIC_WORD_TYPE_PUNCTO;
            }

            if (curtp != newtp) {
                if (curtp != AlArabicWord.ARABIC_WORD_TYPE_NONE)
                    token.add(a);
                a = new AlArabicWord();
                a.start = i;
                a.end = i;
                a.type = newtp;
            } else {
                a.end = i;
            }


            switch (newtp) {
                case AlArabicWord.ARABIC_WORD_TYPE_ARABIC:
                case AlArabicWord.ARABIC_WORD_TYPE_NORMAL:
                case AlArabicWord.ARABIC_WORD_TYPE_DIGIT:
                    if (i == len - 1)
                        token.add(a);
                    curtp = newtp;
                    break;
                default:
                    token.add(a);
                    curtp = AlArabicWord.ARABIC_WORD_TYPE_NONE;
                    break;
            }
        }

        if (token.size() > 0) {
            while (oi.count >= arabicItem.realLength)
                AlOneItem.incItemLength(arabicItem);


            while (true) {
                sbtest.setLength(0);
                for (i = 0; i < token.size(); i++) {
                    switch (token.get(i).type) {
                        case AlArabicWord.ARABIC_WORD_TYPE_ARABIC: sbtest.append('a'); break;
                        case AlArabicWord.ARABIC_WORD_TYPE_DIGIT: sbtest.append('d'); break;
                        case AlArabicWord.ARABIC_WORD_TYPE_NORMAL: sbtest.append('n'); break;
                        case AlArabicWord.ARABIC_WORD_TYPE_OTHER: sbtest.append('o'); break;
                        case AlArabicWord.ARABIC_WORD_TYPE_PUNCTO:
                            switch (oi.text[token.get(i).start]) {
                                case '%' : sbtest.append('%'); break;
                                case '.' : sbtest.append('.'); break;
                                case '-' : sbtest.append('-'); break;
                                case '/' : sbtest.append('/'); break;
                                case ',' : sbtest.append(','); break;
                                default : sbtest.append('!'); break;
                            }
                            break;
                        case AlArabicWord.ARABIC_WORD_TYPE_SPACE: sbtest.append(' '); break;
                    }
                }

                i = sbtest.indexOf("d%");
                if (i != -1) {
                    token.get(i).end = token.get(i + 1).end;
                    token.remove(i + 1);
                    continue;
                }

                i = sbtest.indexOf("d.d");
                if (i != -1) {
                    token.get(i).end = token.get(i + 2).end;
                    token.remove(i + 1);
                    token.remove(i + 1);
                    continue;
                }

                i = sbtest.indexOf("d/d");
                if (i != -1) {
                    token.get(i).end = token.get(i + 2).end;
                    token.remove(i + 1);
                    token.remove(i + 1);
                    continue;
                }

                i = sbtest.indexOf("d-d");
                if (i != -1) {
                    token.get(i).end = token.get(i + 2).end;
                    token.remove(i + 1);
                    token.remove(i + 1);
                    continue;
                }

                i = sbtest.indexOf("d,d");
                if (i != -1) {
                    token.get(i).end = token.get(i + 2).end;
                    token.remove(i + 1);
                    token.remove(i + 1);
                    continue;
                }
                break;
            }

            if (normalPresent) {
                scanArabicWord(oi);
            } else {
                if (token.size() > 0) {
                    reverseArabicWord(oi, 0, token.size() - 1);
                    res = true;
                }
            }
        }

        return res;
    }

}
