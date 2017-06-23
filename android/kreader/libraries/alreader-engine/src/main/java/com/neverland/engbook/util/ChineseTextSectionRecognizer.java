package com.neverland.engbook.util;

import java.util.HashSet;

/**
 * Created by joy on 2/14/17.
 */
public class ChineseTextSectionRecognizer {

    private static final HashSet<Character> SECTION_NUMBER_CHARACTER_SET = new HashSet<>();
    private static final HashSet<Character> SECTION_END_CHARACTER_SET = new HashSet<>();

    static {
        SECTION_NUMBER_CHARACTER_SET.add('0');
        SECTION_NUMBER_CHARACTER_SET.add('1');
        SECTION_NUMBER_CHARACTER_SET.add('2');
        SECTION_NUMBER_CHARACTER_SET.add('3');
        SECTION_NUMBER_CHARACTER_SET.add('4');
        SECTION_NUMBER_CHARACTER_SET.add('5');
        SECTION_NUMBER_CHARACTER_SET.add('6');
        SECTION_NUMBER_CHARACTER_SET.add('7');
        SECTION_NUMBER_CHARACTER_SET.add('8');
        SECTION_NUMBER_CHARACTER_SET.add('9');
        SECTION_NUMBER_CHARACTER_SET.add('一');
        SECTION_NUMBER_CHARACTER_SET.add('二');
        SECTION_NUMBER_CHARACTER_SET.add('三');
        SECTION_NUMBER_CHARACTER_SET.add('四');
        SECTION_NUMBER_CHARACTER_SET.add('五');
        SECTION_NUMBER_CHARACTER_SET.add('六');
        SECTION_NUMBER_CHARACTER_SET.add('七');
        SECTION_NUMBER_CHARACTER_SET.add('八');
        SECTION_NUMBER_CHARACTER_SET.add('九');
        SECTION_NUMBER_CHARACTER_SET.add('十');
        SECTION_NUMBER_CHARACTER_SET.add('百');
        SECTION_NUMBER_CHARACTER_SET.add('千');
        SECTION_NUMBER_CHARACTER_SET.add('万');

        SECTION_END_CHARACTER_SET.add('卷');
        SECTION_END_CHARACTER_SET.add('部');
        SECTION_END_CHARACTER_SET.add('篇');
        SECTION_END_CHARACTER_SET.add('章');
        SECTION_END_CHARACTER_SET.add('节');
    }

    private enum SectionState { SECTION_START, SECTION_NUMBER, SECTION_END, SECTION_TITLE }

    private boolean succeed = false;
    private SectionState state = SectionState.SECTION_START;
    private int textCount = 0;
    private char[] textBuffer = new char[100];

    private ChineseTextSectionRecognizer() {

    }

    public static ChineseTextSectionRecognizer create() {
        return new ChineseTextSectionRecognizer();
    }

    public boolean matches() {
        return succeed && (state == SectionState.SECTION_TITLE |
                state == SectionState.SECTION_END);
    }

    public String getSectionText() {
        return String.valueOf(textBuffer, 0, textCount);
    }

    public void reset() {
        succeed = true;
        state = SectionState.SECTION_START;
        textCount = 0;
    }

    public boolean onNewCharacter(char ch) {
        if (!succeed) {
            return false;
        }
        if (textCount >= textBuffer.length) {
            succeed = false;
            return false;
        }
        textBuffer[textCount++] = ch;

        succeed = false;
        switch (state) {
            case SECTION_START:
                succeed = handleSectionStart(ch);
                break;
            case SECTION_NUMBER:
                succeed = handleSectionNumber(ch);
                break;
            case SECTION_END:
                succeed = handleSectionEnd(ch);
                break;
            case SECTION_TITLE:
                succeed = handleSectionTitle(ch);
                break;
            default:
                break;
        }
        return succeed;
    }

    private boolean handleSectionStart(char ch) {
        if (Character.isSpaceChar(ch)) {
            return true;
        }
        if (ch == '第') {
            state = SectionState.SECTION_NUMBER;
            return true;
        }

        return false;
    }

    private boolean handleSectionNumber(char ch) {
        if (SECTION_NUMBER_CHARACTER_SET.contains(ch)) {
            return true;
        }
        if (SECTION_END_CHARACTER_SET.contains(ch)) {
            state = SectionState.SECTION_END;
            return true;
        }
        return false;
    }

    private boolean handleSectionEnd(char ch) {
        if (Character.isSpaceChar(ch)) {
            return true;
        } else {
            state = SectionState.SECTION_TITLE;
            return true;
        }
    }

    private boolean handleSectionTitle(char ch) {
        return true;
    }

}
