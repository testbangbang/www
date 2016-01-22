package com.onyx.reader.text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.Pair;


public class OnyxHyphen {

    public static final int WORD_LEN = 512;

    public static final char HYPH_RESULT_ADDHYPH = '-';
    public static final char HYPH_RESULT_SILENTHYPH = 'B';

    private static int		word_count;
    private static final byte[]  word_hyph = new byte[WORD_LEN + 4];
    private static final char[]  out_res = new char[WORD_LEN + 3];


    private static int LetterCount = 0;
    private static int PatternLen = 0;

    private static int UseLang = 0;

    private static HashMap<Character, Integer> allLetter;
    private static char[] Pattern = null;
    private static byte[] Mask = null;

    private static char[] WordIn = null;
    private static int[] WordAdr = null;

    public static final int HYPH_NONE = 0;
    public static final int HYPH_RUENG = 1;
    public static final int HYPH_BULGARIAN = 2;
    public static final int HYPH_CZECH = 3;
    public static final int HYPH_DANISH = 4;
    public static final int HYPH_ENGLISH = 5;
    public static final int HYPH_FINNISH = 6;
    public static final int HYPH_FRENCH = 7;
    public static final int HYPH_GERMAN = 8;
    public static final int HYPH_HUNGARIAN = 9;
    public static final int HYPH_ICELANDIC = 10;
    public static final int HYPH_IRISH = 11;
    public static final int HYPH_ITALIAN = 12;
    public static final int HYPH_POLISH = 13;
    public static final int HYPH_PORTUGUESE = 14;
    public static final int HYPH_ROMAN = 15;
    public static final int HYPH_RUSSIAN = 16;
    public static final int HYPH_SLOVAK = 17;
    public static final int HYPH_SLOVENIAN = 18;
    public static final int HYPH_SPANISH = 19;
    public static final int HYPH_SWEDISH = 20;
    public static final int HYPH_UKRAINIAN = 21;
    public static final int HYPH_ANCIENT_GREEK = 22;
    public static final int HYPH_DUTCH = 23;

    private static int space_adr = -1;

    private static final int HYPH_NUMBER = 22;
    private static final HashMap<Integer, String> allHyphStr = new HashMap<Integer, String>(
            HYPH_NUMBER);

    static {
        allHyphStr.put(HYPH_NONE, "None");
        allHyphStr.put(HYPH_RUENG, "Russian-English");
        allHyphStr.put(HYPH_BULGARIAN, "Bulgarian");
        allHyphStr.put(HYPH_CZECH, "Czech");
        allHyphStr.put(HYPH_DANISH, "Danish");
        allHyphStr.put(HYPH_ENGLISH, "English");
        allHyphStr.put(HYPH_FINNISH, "Finnish");
        allHyphStr.put(HYPH_FRENCH, "French");
        allHyphStr.put(HYPH_GERMAN, "German");
        allHyphStr.put(HYPH_HUNGARIAN, "Hungarian");
        allHyphStr.put(HYPH_ICELANDIC, "Icelandic");
        allHyphStr.put(HYPH_IRISH, "Irish");
        allHyphStr.put(HYPH_ITALIAN, "Italian");
        allHyphStr.put(HYPH_POLISH, "Polish");
        allHyphStr.put(HYPH_PORTUGUESE, "Portuguese");
        allHyphStr.put(HYPH_ROMAN, "Roman");
        allHyphStr.put(HYPH_RUSSIAN, "Russian");
        allHyphStr.put(HYPH_SLOVAK, "Slovak");
        allHyphStr.put(HYPH_SLOVENIAN, "Slovenian");
        allHyphStr.put(HYPH_SPANISH, "Spanish");
        allHyphStr.put(HYPH_SWEDISH, "Swedish");
        allHyphStr.put(HYPH_UKRAINIAN, "Ukrainian");
        allHyphStr.put(HYPH_ANCIENT_GREEK, "Ancient_Greek");
        allHyphStr.put(HYPH_DUTCH, "Dutch");

        WordIn = new char[WORD_LEN * 2 + 3];
        WordAdr = new int[WORD_LEN * 2 + 3];
    }

    public final static int getCountLangHyph() {
        return HYPH_NUMBER;
    }

    private final static String getStr(int num) {
        if (num < 0 || num > HYPH_NUMBER)
            return allHyphStr.get(1);
        return allHyphStr.get(num);
    }

    public static int GetLang() {
        return UseLang;
    }

    private OnyxHyphen(int lang) {

    }

    public static void reinit_hyph(Context context, int lang) {
        if (lang == UseLang)
            return;

        UseLang = lang;
        if (UseLang < 0 || UseLang > HYPH_NUMBER)
            UseLang = HYPH_NONE;

        if (UseLang != HYPH_NONE) {

            InputStream is = null;
            int tmp;
            try {
                String hyphFile = getStr(UseLang) + ".pattern";
                is = context.getAssets().open(hyphFile);

                tmp = is.read();
                tmp += is.read() << 8;
                tmp += is.read() << 16;
                tmp += is.read() << 24;

                if (tmp == 0x30686c61) {

                    PatternLen = is.read();
                    PatternLen += is.read() << 8;
                    PatternLen += is.read() << 16;
                    PatternLen += is.read() << 24;

                    LetterCount = is.read();
                    LetterCount += is.read() << 8;
                    LetterCount += is.read() << 16;
                    LetterCount += is.read() << 24;

                    if ((PatternLen > 0) && (PatternLen <= 0x000fffff)
                            && (LetterCount > 0) && (LetterCount <= 255)) {

                        allLetter = new HashMap<Character, Integer>(LetterCount);
                        char c = ' ';
                        int i;
                        for (i = 0; i < LetterCount; i++) {
                            c = (char) is.read();
                            c += (char) is.read() << 8;

                            tmp = is.read();
                            tmp += is.read() << 8;
                            tmp += is.read() << 16;
                            tmp += is.read() << 24;

                            allLetter.put(c, tmp);
                        }

                        Pattern = new char[PatternLen + 1];
                        Mask = new byte[PatternLen + 1];

                        for (i = 0; i < PatternLen; i++) {
                            c = (char) is.read();
                            c += (char) is.read() << 8;
                            Pattern[i] = c;
                        }
                        Pattern[PatternLen] = 0x00;

                        is.read(Mask);

                        Mask[PatternLen] = 0x00;
                    }
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Pattern = null;
            Mask = null;
            if (allLetter != null)
                allLetter.clear();
            allLetter = null;
        }

        space_adr = getAdrHyphPattern(' ');
    }

    private final static int getAdrHyphPattern(char let) {
        if (allLetter == null)
            return -1;
        Integer adr = allLetter.get(let);
        if (adr == null)
            adr = -1;
        return adr;
    }

    private final static boolean isLetter(char ch) {
        return ((((1 << Character.UPPERCASE_LETTER) |
                (1 << Character.LOWERCASE_LETTER) |
                (1 << Character.TITLECASE_LETTER) |
                (1 << Character.MODIFIER_LETTER) |
                (1 << Character.OTHER_LETTER)
        ) >> Character.getType((int)ch)
        ) & 1
        ) != 0;
    }

    public final static boolean isLetterOrDigit(char ch) {
        return ((((1 << Character.UPPERCASE_LETTER) |
                (1 << Character.LOWERCASE_LETTER) |
                (1 << Character.TITLECASE_LETTER) |
                (1 << Character.MODIFIER_LETTER) |
                (1 << Character.OTHER_LETTER) |
                (1 << Character.DECIMAL_DIGIT_NUMBER)
        ) >> Character.getType((int)ch)
        ) & 1
        ) != 0;
    }

    public final static boolean isStartPunctuation(char ch) {
        return ((((1 << Character.START_PUNCTUATION)
        ) >> Character.getType((int)ch)
        ) & 1
        ) != 0;
    }

    public final static boolean isSpaceSeparator(char ch) {
        return ((((1 << Character.SPACE_SEPARATOR)
        ) >> Character.getType((int)ch)
        ) & 1
        ) != 0;
    }

    public final static boolean isModifer(char ch) {
        return ((((1 << Character.MODIFIER_SYMBOL)
        ) >> Character.getType((int)ch)
        ) & 1
        ) != 0;
    }

    private final static boolean isHyphWordChar(char ch) {
        switch (ch) {
            case 0x2b: // +
            case 0x2d: // -
            case 0x2f: // /
            case 0x5c: // \
            case 0x2013: case 0x2014: case 0x2015: // -
                return true;
        }
        return !isLetterOrDigit(ch) && !isStartPunctuation(ch) && !isModifer(ch);
    }

    public final static boolean isSpace2(char ch) {
        return (ch <= 0x0020) &&
                (((((1L << 0x0009) |
                        (1L << 0x000A) |
                        (1L << 0x000C) |
                        (1L << 0x000D) |
                        (1L << 0x0020)
                ) >> ch
                ) & 1L
                ) != 0);
    }


    public static final char[] getHyph(final String word) {
        return getHyph(word, true);
    }

    public synchronized static final char[] getHyph(final String word, boolean enable) {

        word_count = word.length();
        if (word_count < 4 || word_count > WORD_LEN)
            return null;

        int i;
        WordIn[0] = 0x20;
        WordAdr[0] = space_adr;
        word_hyph[0] = 'A';
        for (i = 0; i < word_count; i++) {
            if (word.charAt(i) >= 0x3000) {
                WordIn[i + 1] = 'A';
                WordAdr[i + 1] = space_adr;
                if (((i == word_count - 1) || isLetter(word.charAt(i + 1)))) {
                    word_hyph[i + 1] = 'B';
                } else {
                    if (i < word_count - 2
                            && (word.charAt(i + 1) == 8220 || word.charAt(i + 1) == 8221)) {
                        word_hyph[i + 1] = 'B';
                    } else
                        word_hyph[i + 1] = '8';
                }
            } else if (isLetter(word.charAt(i))) {
                word_hyph[i + 1] = '0';
                WordIn[i + 1] = Character.toLowerCase(word.charAt(i));
                WordAdr[i + 1] = getAdrHyphPattern(WordIn[i + 1]);
            } else if (isHyphWordChar(word.charAt(i))) {
                WordIn[i + 1] = ' ';
                WordAdr[i + 1] = space_adr;
                word_hyph[i + 1] = 'B';
            } else {
                word_hyph[i + 1] = '8';
                WordIn[i + 1] = ' ';
                WordAdr[i + 1] = space_adr;
            }
        }
        WordIn[word_count + 1] = 0x20;
        WordAdr[word_count + 1] = space_adr;
        word_hyph[1] = 'A';
        for (i = 1; i <= word_count + 1; i++) {
            if (WordIn[i] == ' ') {
                if (i + 1 <= word_count && word_hyph[i + 1] != 'B')
                    word_hyph[i + 1] = 'A';
                if (i - 1 > 0 && word_hyph[i - 1] != 'B')
                    word_hyph[i - 1] = 'A';
                if (i - 2 > 0 && word_hyph[i - 2] != 'B'/* && tword.text */)
                    word_hyph[i - 2] = 'A';
            }
        }

        if (Pattern != null && enable) {
            int start_adr = 0;
            int letter_num;
            int len_pattern;
            int j;

            next_letter: for (letter_num = word_count; letter_num >= 0; letter_num--) {
                start_adr = WordAdr[letter_num];
                if (start_adr == -1)
                    continue;
                //
                len_pattern = Pattern[start_adr];
                if (len_pattern == 1) {
                    if (letter_num > 0
                            && Mask[start_adr] > word_hyph[letter_num - 1])
                        word_hyph[letter_num - 1] = Mask[start_adr];
                    if (Mask[start_adr + 1] > word_hyph[letter_num])
                        word_hyph[letter_num] = Mask[start_adr + 1];
                }
                //

                while (true) {
                    if (start_adr >= PatternLen)
                        break;

                    len_pattern = Pattern[start_adr];
                    if (Pattern[start_adr + 1] != WordIn[letter_num])
                        continue next_letter;

                    if (len_pattern <= word_count - letter_num + 2) {
                        for (i = 1; i < len_pattern; i++) {
                            if (Pattern[start_adr + i + 1] > WordIn[letter_num
                                    + i]) {
                                continue next_letter;
                            } else if (Pattern[start_adr + i + 1] < WordIn[letter_num
                                    + i]) {
                                break;
                            } else if (i == len_pattern - 1) {
                                for (j = (letter_num == 0 ? 1 : 0); j <= len_pattern; j++) {
                                    if (Mask[start_adr + j] > word_hyph[letter_num
                                            + j - 1])
                                        word_hyph[letter_num + j - 1] = Mask[start_adr + j];
                                }
                            }
                        }
                    }

                    start_adr += len_pattern + 1;
                }
            }
        }

        for (i = 1; i <= word_count; i++) {
            switch (word_hyph[i]) {
                case '1':
                case '3':
                case '5':
                case '7':
                case '9':
                    word_hyph[i] = HYPH_RESULT_ADDHYPH;
                    break;
                case '0':
                case '2':
                case '4':
                case '6':
                case '8':
                    word_hyph[i] = '0';
                    break;
            }
        }

        for (i = 0; i < word_count; i++) {
            out_res[i] = (char) word_hyph[i + 1];
            if (isSpace2(word.charAt(i)))
                out_res[i] = HYPH_RESULT_SILENTHYPH;
        }

        return out_res;
    }

    public static final List<Pair<String, String>> getHyphList(final String word) {
        char [] data = getHyph(word, true);
        if (data == null) {
            return null;
        }
        List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
        for(int i = 1; i < data.length && i < word.length(); ++i) {
            if (data[i] == '\u0000') {
                break;
            } else if (data[i] == '-') {
                String a = word.substring(0, i);
                String b = word.substring(i);
                list.add(new Pair<String, String>(a, b));
            }
        }
        return list;
    }

}
