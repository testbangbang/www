package com.neverland.engbook.level2;

import android.util.Log;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.unicode.CP932;
import com.neverland.engbook.unicode.CP936;
import com.neverland.engbook.unicode.CP949;
import com.neverland.engbook.unicode.CP950;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlOneLink;
import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.InternalFunc;

import java.util.HashMap;

public class AlFormatRTF extends AlFormat {

    private static final int RTF_TEST_BUF_LENGTH = 16;
    private static final String RTF_TEST_STR = "\\rtf";



    public static boolean isRTF(AlFiles a) {

        char[] buf_uc = new char[RTF_TEST_BUF_LENGTH];
        String s;

        if (getTestBuffer(a, TAL_CODE_PAGES.CP1251, buf_uc, RTF_TEST_BUF_LENGTH, true)) {
            s = String.copyValueOf(buf_uc);
            if  (s.contains(RTF_TEST_STR))
                return true;
        }

        return false;
    }

    private int pict_level = 0;

    private String fnt_name = null;
    private int    fnt_charset = -1;
    private int	   fnt_level = -1;
    private final HashMap<String, Integer> fnt_charset_mode = new HashMap<>();


    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref, AlStylesOptions stl) {
        ident = "RTF";

        fnt_charset_mode.clear();

        aFiles = myParent;
        preference = pref;
        styles = stl;

        if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG) != 0)
            aFiles.needUnpackData();

        size = 0;

        switch (bookOptions.codePageDefault) {
            case TAL_CODE_PAGES.CP1200:
            case TAL_CODE_PAGES.CP1201:
            case TAL_CODE_PAGES.CP65001:
            case TAL_CODE_PAGES.AUTO:
                use_cpR0 = TAL_CODE_PAGES.CP1252;
                break;
            default:
                use_cpR0 = bookOptions.codePageDefault;
                break;
        }

        autoCodePage = true;
        styleStack.buffer[styleStack.position].cp = use_cpR0;
        returnLang0();

        allState.state_parser = STATE_RTF0_TEXT;

        parser(0, aFiles.getSize());
    }

    private static final int STATE_RTF0_TEXT = 0x00;
    private static final int STATE_RTF0_TAG_START = 0x01;
    private static final int STATE_RTF0_TAG_PROCESS = 0x02;
    private static final int STATE_RTF0_TAG_END = 0x03;
    private static final int STATE_RTF0_TWO_HEX1 = 0x04;
    private static final int STATE_RTF0_TWO_HEX2 = 0x05;
    private static final int STATE_RTF0_SKIP_COUNTBYTES = 0x06;

    private static final int STATE_RTF2_TEXT = 0x00;
    private static final int STATE_RTF2_COMMENT = 0x10;
    private static final int STATE_RTF2_MAYBEPICT = 0x20;
    private static final int STATE_RTF2_WAITPICT = 0x30;
    private static final int STATE_RTF2_PICT = 0x40;
    private static final int STATE_RTF2_SKIP = 0x50;
    private static final int STATE_RTF2_SKIPONE = 0x60;
    private static final int STATE_RTF2_SKIPFNT = 0x70;
    private static final int STATE_RTF2_READSTRING = 0x80;

    private static final int RTF_READSTRING_FLDINST = 0x01;
    private static final int RTF_READSTRING_BKMKSTART = 0x02;

    //protected int	rtf_level = 0;
    protected int	rtf_skip = 0;
    protected char[]    data_cp0 = null;

    @Override
    protected final void doTextChar(char ch, boolean addSpecial) {
        if (allState.skipped_flag > 0) {
            if (allState.state_special_flag && addSpecial)
                specialBuff.add(ch);
        } else {

            if (parText.length > 0) {
                if (ch == 0xad) {
                    softHyphenCount++;
                }
                else
                if (ch == 0x20 && (styleStack.buffer[styleStack.position].paragraph & AlStyles.SL_PRESERVE_SPACE) != 0) {
                    if (parText.buffer[parText.length - 1] == 0x20)
                        ch = 0xa0;
                }
                parText.add(ch);

                size++;
                parText.positionE = allState.start_position;
                parText.haveLetter = parText.haveLetter || (ch != 0xa0 && ch != 0x20
                        && (ch & AlStyles.STYLE_MASK_4CODECONVERT) != AlStyles.STYLE_BASE_4CODECONVERT);
                if (parText.length > EngBookMyType.AL_MAX_PARAGRAPH_LEN) {
                    if (!AlUnicode.isLetterOrDigit(ch) && !allState.insertFromTag)
                    newParagraph();
                }
            } else {
                if (ch == 0x20 && (styleStack.buffer[styleStack.position].paragraph & AlStyles.SL_PRESERVE_SPACE) == 0) {

                } else {
                    parText.positionS = parText.positionE = allState.start_position_par;

                    parText.paragraph = styleStack.getActualParagraph();
                    parText.prop = styleStack.getActualProp();
                    parText.sizeStart = size;
                    parText.tableStart = currentTable.start;
                    parText.tableCounter = currentTable.counter;

                    if (ch == 0x20)
                        ch = 0xa0;

                    parText.haveLetter = (ch != 0xa0 && (ch & AlStyles.STYLE_MASK_4CODECONVERT) != AlStyles.STYLE_BASE_4CODECONVERT);
                    size++;

                    parText.add(ch);
                }
            }

            if (allState.state_special_flag && addSpecial)
                specialBuff.add(ch);
        }
    }

    protected int rtf_tag = 0;
    protected final StringBuilder rtf_param = new StringBuilder();


    protected void resetTAGCRC() {
        rtf_tag = 0x00;
        rtf_param.setLength(0);
        allState.start_position_par = allState.start_position;
    }

    private int getCodePageByCharset(int cs) {
        switch (cs) {
            case 0 		: return 1252;
            case 1		: return 1252;// special case ???
            case 2		: return 1252;// special case ???
            case 102 	: return 936; // abiword
            case 128 	: return 932;
            case 129 	: return 949;
            case 130 	: return 949; //CP1361 ??? abiword
            case 134 	: return 936;
            case 136 	: return 950;
            case 161 	: return 1253;
            case 162 	: return 1254;
            case 163 	: return 1258;
            case 177 	: return 1255;
            case 178 	: return 1256;
            case 179 	: return 1256;
            case 180 	: return 1256;
            case 181 	: return 1255;
            case 186 	: return 1257;
            case 204 	: return 1251;
            case 222 	: return 874;
            case 238 	: return 1250;
            case 254 	: return 437;
        }
        return use_cpR0;
    }

    private int chinese_num_prv = 0;
    private int chinese_num_hex = 0;
    protected void addTwoHex() {
        char ch = 0x00;
        if (rtf_param.length() == 2) {

            //char c0 = Character.toLowerCase(rtf_param.charAt(0));
            //char c1 = Character.toLowerCase(rtf_param.charAt(1));
            switch (rtf_param.charAt(0)) {//c0) {
                case '0': ch = 0x00; break; case '1': ch = 0x10; break; case '2': ch = 0x20; break; case '3': ch = 0x30; break;
                case '4': ch = 0x40; break; case '5': ch = 0x50; break; case '6': ch = 0x60; break; case '7': ch = 0x70; break;
                case '8': ch = 0x80; break; case '9': ch = 0x90; break; case 'a': ch = 0xa0; break; case 'b': ch = 0xb0; break;
                case 'c': ch = 0xc0; break; case 'd': ch = 0xd0; break; case 'e': ch = 0xe0; break; case 'f': ch = 0xf0; break;
                case 'A': ch = 0xa0; break; case 'B': ch = 0xb0; break;
                case 'C': ch = 0xc0; break; case 'D': ch = 0xd0; break; case 'E': ch = 0xe0; break; case 'F': ch = 0xf0; break;
                default:
                    chinese_num_hex = 0;
                    return;
            }
            switch (rtf_param.charAt(1)) {//c1) {
                case '0': ch |= 0x0; break; case '1': ch |= 0x1; break; case '2': ch |= 0x2; break; case '3': ch |= 0x3; break;
                case '4': ch |= 0x4; break; case '5': ch |= 0x5; break; case '6': ch |= 0x6; break; case '7': ch |= 0x7; break;
                case '8': ch |= 0x8; break; case '9': ch |= 0x9; break; case 'a': ch |= 0xa; break; case 'b': ch |= 0xb; break;
                case 'c': ch |= 0xc; break; case 'd': ch |= 0xd; break; case 'e': ch |= 0xe; break; case 'f': ch |= 0xf; break;
                case 'A': ch |= 0xa; break; case 'B': ch |= 0xb; break;
                case 'C': ch |= 0xc; break; case 'D': ch |= 0xd; break; case 'E': ch |= 0xe; break; case 'F': ch |= 0xf; break;
                default:
                    chinese_num_hex = 0;
                    return;
            }

            //if (ch >= 0x80) {

                switch (styleStack.buffer[styleStack.position].cp >> 16) {
                    case 932:
                        switch (chinese_num_hex) {
                            case 0 :
                                switch (ch) {
                                    case 0x80 :
                                    case 0xfd :
                                    case 0xfe :
                                    case 0xff : ch = 0x0000; break;
                                    default :
                                        chinese_num_prv = ch;
                                        ch = (char) (parText.length == 0 ? 0xa0 : 0x00);
                                        chinese_num_hex = 1;
                                        break;
                                }
                                break;
                            case 1 :
                                char ch1 = ch;
                                ch = (char) chinese_num_prv;
                                if (ch >= 0xa1 && ch <= 0xdf) {
                                    ch = (char) (ch + 0xfec0);
                                    break;
                                }
                                ch = (ch1 >= 0x40 && ch1 <= 0xfc) ? CP932.getChar(ch, ch1) : 0x00;
                                chinese_num_hex = 0;
                                break;
                        }
                        break;
                    case 949:
                        switch (chinese_num_hex) {
                            case 0 :
                                switch (ch) {
                                    case 0x80 :
                                    case 0xff : ch = 0x0000; break;
                                    default:
                                        chinese_num_prv = ch;
                                        ch = (char) (parText.length == 0 ? 0xa0 : 0x00);
                                        chinese_num_hex = 1;
                                        break;
                                }
                                break;
                            case 1 :
                                char ch1 = ch;
                                ch = (char) chinese_num_prv;
                                ch = (ch1 >= 0x41 && ch1 <= 0xfe) ? CP949.getChar(ch, ch1) : 0x00;
                                chinese_num_hex = 0;
                                break;
                        }
                        break;
                    case 950:
                        switch (chinese_num_hex) {
                            case 0 :
                                switch (ch) {
                                    case 0x80 :
                                    case 0xff : ch = 0x0000; break;
                                    default:
                                        chinese_num_prv = ch;
                                        ch = (char) (parText.length == 0 ? 0xa0 : 0x00);
                                        chinese_num_hex = 1;
                                        break;
                                }
                                break;
                            case 1 :
                                char ch1 = ch;
                                ch = (char) chinese_num_prv;
                                ch = (ch1 >= 0x40 && ch1 <= 0xfe) ? CP950.getChar(ch, ch1) : 0x00;
                                chinese_num_hex = 0;
                                break;
                        }
                        break;
                    case 936:
                        switch (chinese_num_hex) {
                            case 0 :
                                switch (ch) {
                                    case 0x80 : ch = 0x20AC; break;
                                    case 0xff : ch = 0x0000; break;
                                    default:
                                        chinese_num_prv = ch;
                                        ch = (char) (parText.length == 0 ? 0xa0 : 0x00);
                                        chinese_num_hex = 1;
                                        break;
                                }
                                break;
                            case 1 :
                                char ch1 = ch;
                                ch = (char) chinese_num_prv;
                                ch = (ch1 >= 0x40 && ch1 <= 0xfe) ? CP936.getChar(ch, ch1) : 0x00;
                                chinese_num_hex = 0;
                                break;
                        }
                        break;

                    /*case 437:
                    case 874:
                    case 1258:
                    case 1257:
                    case 1256:
                    case 1255:
                    case 1254:
                    case 1253:
                    case 1252:
                    case 1251:
                    case 1250:*/
                    default:
                        if (ch > 0x80) {
                            if (data_cp0 != null) {
                                ch = data_cp0[ch - 0x80];
                            } else {
                                ch = 0x00;
                            }
                        }
                        chinese_num_hex = 0;
                        break;
                    /*default:
                        ch = data_cp0[ch - 0x80];
                        chinese_num_hex = 0;
                        break;*/
                }
            /*} else {
                chinese_num_hex = 0;
            }*/

            if (ch >= 0x20) {
                acceptChar(ch);
            } else
            if (ch == 0x0a || ch == 0x09) {
                acceptChar(ch);
            }
        }
    }

    @Override
    protected void newParagraph() {
        if (parText.length > 0) {

        } else {
            setPropStyle(AlParProperty.SL2_EMPTY_BEFORE);
        }


        super.newParagraph();
    }

    /*private int		image_start = -1;
    private int		image_stop = -1;
    private int		image_type = 0;
    private String	image_name = null;*/

    public  boolean addImages() {
        allState.image_name = String.format("://$$$%d.image", allState.start_position);//_par);
        //addTextFromTag((char)AlStyles.CHAR_IMAGE_S + image_name + (char)AlStyles.CHAR_IMAGE_E, false);
        addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
        addTextFromTag(allState.image_name, false);
        addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);

        //if (allState.isOpened) {
        allState.image_start = -1;
        allState.image_stop = -1;
        //}

        return true;
    }

    /*private void incLevel() {
        if (paragraph_level < MAX_STACK_STYLES) {
            paragraph_level++;

            stack_styles[paragraph_level] = stack_styles[paragraph_level - 1];

            stack_cp0[paragraph_level] = stack_cp0[paragraph_level - 1];

            rtf_tag = AlFormatTag.TAG_RTFINCLEVEL;
            acceptChar((char)0x00);
        }
    }

    private void decLevel() {
        if (paragraph_level > 0) {
            paragraph_level--;

            if (stack_styles[paragraph_level] != stack_styles[paragraph_level + 1])
                returnTextStyle();

            if (stack_cp0[paragraph_level] != stack_cp0[paragraph_level + 1])
                returnLang0();

            rtf_tag = AlFormatTag.TAG_RTFDECLEVEL;
            acceptChar((char) 0x00);
        }
    }

    private char stack_styles[] = new char[MAX_STACK_STYLES + 1];
    private int  stack_cp0[] = new int[MAX_STACK_STYLES + 1];

    @Override
    protected final void setTextStyle(int tag) {
        paragraph |= tag;

        stack_styles[paragraph_level] = (char) (paragraph & AlStyles.PAR_STYLE_MASK);

        if (allState.text_present)
            doTextChar(getTextStyle(), false);
    }

    protected final void returnTextStyle() {
        paragraph &= ~((long)AlStyles.PAR_STYLE_MASK);
        paragraph |= stack_styles[paragraph_level];
        if (allState.text_present)
            doTextChar(getTextStyle(), false);
    }

    @Override
    protected final void clearTextStyle(int tag) {
        paragraph &= (~tag);

        stack_styles[paragraph_level] = (char) (paragraph & AlStyles.PAR_STYLE_MASK);

        if (allState.text_present)
            doTextChar(getTextStyle(), false);
    }

    @Override
    protected 	void doSpecialGetParagraph(long iType, int addon, long level, long[] stk, int[] cpl) {
        paragraph = iType;
        allState.state_parser = 0;

        paragraph_level = (int) (level & LEVEL2_MASK_FOR_LEVEL);
        stack_cp0[paragraph_level] = addon;
        returnLang0();

        rtf_skip = 0;

        if (paragraph_level < MAX_STACK_STYLES)
            stack_styles[paragraph_level] = (char) (iType & AlStyles.PAR_STYLE_MASK);

        int i;
        if (stk == null) {
            for (i = paragraph_level - 1; i >= 0; i--)
                stack_styles[i] = 0x00;
        } else {
            for (i = paragraph_level - 1; i >= 0; i--)
                stack_styles[i] = (char) stk[i];
        }

        if (cpl == null) {
            for (i = paragraph_level - 1; i > 0; i--)
                stack_cp0[i] = stack_cp0[paragraph_level];
            stack_cp0[0] = use_cpR0;
        } else {
            for (i = paragraph_level - 1; i >= 0; i--)
                stack_cp0[i] = cpl[i];
        }

        allState.state_skipped_flag = (level & LEVEL2_FRM_ADDON_SKIPPEDTEXT) != 0;
        allState.state_code_flag = (level & LEVEL2_FRM_ADDON_CODETEXT) != 0;
    }


    @Override
    protected  void formatAddonInt() {
        pariType = paragraph;
        parAddon = stack_cp0[paragraph_level];

        parLevel = paragraph_level;

        int i;

        boolean needArray = false;
        for (i = paragraph_level - 1; i >= 0; i--)
            if (stack_styles[i] != 0) {
                needArray = true;
                break;
            }

        if (needArray) {
            parStack = new long [paragraph_level];
            for (i = paragraph_level - 1; i >= 0; i--)
                parStack[i] = stack_styles[i];
        } else {
            parStack = null;
        }

        needArray = false;
        for (i = paragraph_level - 1; i > 0; i--)
            if (stack_cp0[i] != stack_cp0[paragraph_level]) {
                needArray = true;
                break;
            }

        if (!needArray)
            needArray = stack_cp0[0] != use_cpR0;

        if (needArray) {
            parCP = new int [paragraph_level];
            for (i = paragraph_level - 1; i >= 0; i--)
                parCP[i] = stack_cp0[i];
        } else {
            parCP = null;
        }

        if (allState.state_skipped_flag)
            parLevel += LEVEL2_FRM_ADDON_SKIPPEDTEXT;
        if (allState.state_code_flag)
            parLevel += LEVEL2_FRM_ADDON_CODETEXT;
    }
    */

    private void incLevel() {
        styleStack.push();
        rtf_tag = AlFormatTag.TAG_RTFINCLEVEL;
        acceptChar((char)0x00);
    }

    private void decLevel() {
        styleStack.pop();
        //if (parText.length)
        doTextChar(getTextStyle(), false);
        returnLang0();

        rtf_tag = AlFormatTag.TAG_RTFDECLEVEL;
        acceptChar((char)0x00);
    }

    //private final long stack_styles[] = new long[MAX_STACK_STYLES + 1];
    //private final int  stack_cp0[] = new int[MAX_STACK_STYLES + 1];

    /*@Override
    protected final void setTextStyle(int tag) {
        paragraph |= tag;

        stack_styles[paragraph_level] = paragraph;

        if (allState.text_present)
            doTextChar(getTextStyle(), false);
    }*/

    /*protected final void returnTextStyle() {
        //paragraph &= ~((long)AlStyles.PAR_STYLE_MASK);
        paragraph = stack_styles[paragraph_level];
        if (allState.text_present)
            doTextChar(getTextStyle(), false);
    }
*/
    /*@Override
    protected final void clearTextStyle(int tag) {
        paragraph &= (~tag);

        stack_styles[paragraph_level] = paragraph;

        if (allState.text_present)
            doTextChar(getTextStyle(), false);
    }

    @Override
    void setParagraphStyle(long tag) {
        paragraph |= tag;
        stack_styles[paragraph_level] = paragraph;
    }

    @Override
    void clearParagraphStyle(long tag) {
        paragraph &= (~tag);
        stack_styles[paragraph_level] = paragraph;
    }*/

    protected void returnLang0() {
        if (autoCodePage) {
            data_cp = AlUnicode.getDataCP(styleStack.buffer[styleStack.position].cp & 0xffff);
            if ((styleStack.buffer[styleStack.position].cp & 0xffff0000) != 0) {
                data_cp0 = AlUnicode.getDataCP((styleStack.buffer[styleStack.position].cp & 0xffff0000) >> 16);
            } else {
                data_cp0 = AlUnicode.getDataCP(use_cpR0);
            }
            chinese_num_hex = 0;
        }
    }

    protected void processTag0() {
        char ch;

        switch (rtf_tag) {
		    case AlFormatTag.TAG_ANSI:
            case AlFormatTag.TAG_MAC:
                break;
            case AlFormatTag.TAG_PC:
                use_cpR0 = TAL_CODE_PAGES.CP437;
                if (autoCodePage) {
                    for (int i = 0; i <= styleStack.position; i++)
                        styleStack.buffer[i].cp = use_cpR0;
                } else {
                    styleStack.buffer[styleStack.position].cp = use_cpR0;
                }
                returnLang0();
                break;
            case AlFormatTag.TAG_PCA:
                use_cpR0 = TAL_CODE_PAGES.CP850;
                if (autoCodePage) {
                    for (int i = 0; i <= styleStack.position; i++)
                        styleStack.buffer[i].cp = use_cpR0;
                } else {
                    styleStack.buffer[styleStack.position].cp = use_cpR0;
                }
                returnLang0();
                break;
            case AlFormatTag.TAG_ANSICPG:
                if (autoCodePage) {
                    try {
                        ch = (char) Integer.parseInt(rtf_param.toString());
                        //stack_cp0[paragraph_level] &= 0xffff0000;
                        //stack_cp0[paragraph_level] |= ch;

                        int c = AlUnicode.int2cp(ch);
                        switch (c) {
                            case TAL_CODE_PAGES.AUTO:
                            case TAL_CODE_PAGES.CP1200:
                            case TAL_CODE_PAGES.CP1201:
                            case TAL_CODE_PAGES.CP65001:
                                break;
                            default:
                                use_cpR0 = c;
                                for (int i = 0; i <= styleStack.position; i++)
                                    styleStack.buffer[i].cp = use_cpR0;
                                returnLang0();
                                break;
                        }
                        //stack_cp0[paragraph_level] = use_cpR;

                    } catch (Exception e) {
                        use_cpR0 = 1252;
                    }
                }
                break;
            case AlFormatTag.TAG_DEFLANG:
            case AlFormatTag.TAG_LANG:
                if (autoCodePage) {
                    try {
                        ch = (char) Integer.parseInt(rtf_param.toString());
                        styleStack.buffer[styleStack.position].cp &= 0xffff0000;
                        styleStack.buffer[styleStack.position].cp |= getLangToCP(ch);
                        returnLang0();
                    } catch (Exception e) {
                        //use_cpR0 = 1252;
                    }
                }
                break;
            case AlFormatTag.TAG_CELL:
            case AlFormatTag.TAG_PAR:
            case AlFormatTag.TAG_LINE:
                newParagraph();
                break;
            case AlFormatTag.TAG_SECT:
                newParagraph();
                setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
                break;
            // styles
            case AlFormatTag.TAG_PARD:
                newParagraph();
                clearPropStyle(AlParProperty.SL2_JUST_MASK);
                clearTextStyle(AlStyles.STYLE_MASK);
                break;
            case AlFormatTag.TAG_PLAIN:
                clearTextStyle(AlStyles.STYLE_MASK);
                break;
            case AlFormatTag.TAG_B:
                if (rtf_param.length() == 0) {
                    setTextStyle(AlStyles.STYLE_BOLD);
                } else
                if (rtf_param.toString().equalsIgnoreCase("1")) {
                    setTextStyle(AlStyles.STYLE_BOLD);
                } else
                if (rtf_param.toString().equalsIgnoreCase("0")) {
                    clearTextStyle(AlStyles.STYLE_BOLD);
                }
                break;
            case AlFormatTag.TAG_I:
                if (rtf_param.length() == 0) {
                    setTextStyle(AlStyles.STYLE_ITALIC);
                } else
                if (rtf_param.toString().equalsIgnoreCase("1")) {
                    setTextStyle(AlStyles.STYLE_ITALIC);
                } else
                if (rtf_param.toString().equalsIgnoreCase("0")) {
                    clearTextStyle(AlStyles.STYLE_ITALIC);
                }
                break;
            case AlFormatTag.TAG_STRIKE:
                if (rtf_param.length() == 0) {
                    setTextStyle(AlStyles.STYLE_STRIKE);
                } else
                if (rtf_param.toString().equalsIgnoreCase("1")) {
                    setTextStyle(AlStyles.STYLE_STRIKE);
                } else
                if (rtf_param.toString().equalsIgnoreCase("0")) {
                    clearTextStyle(AlStyles.STYLE_STRIKE);
                }
                break;
            case AlFormatTag.TAG_UL:
                if (rtf_param.length() == 0) {
                    setTextStyle(AlStyles.STYLE_UNDER);
                } else
                if (rtf_param.toString().equalsIgnoreCase("1")) {
                    setTextStyle(AlStyles.STYLE_UNDER);
                } else
                if (rtf_param.toString().equalsIgnoreCase("0")) {
                    clearTextStyle(AlStyles.STYLE_UNDER);
                }
                break;
            case AlFormatTag.TAG_SUPER:
                setTextStyle(AlStyles.STYLE_SUP);
                break;
            case AlFormatTag.TAG_SUB:
                setTextStyle(AlStyles.STYLE_SUB);
                break;
            case AlFormatTag.TAG_NOSUPERSUB:
                clearTextStyle(AlStyles.STYLE_SUP + AlStyles.STYLE_SUB);
                break;
            // end styles

            // justify
            case AlFormatTag.TAG_QL:
                clearPropStyle(AlParProperty.SL2_JUST_MASK);
                setPropStyle(AlParProperty.SL2_JUST_LEFT);
                break;
            case AlFormatTag.TAG_QR:
                clearPropStyle(AlParProperty.SL2_JUST_MASK);
                setPropStyle(AlParProperty.SL2_JUST_RIGHT);
                break;
            case AlFormatTag.TAG_QJ:
                clearPropStyle(AlParProperty.SL2_JUST_MASK);
                break;
            case AlFormatTag.TAG_QC:
                clearPropStyle(AlParProperty.SL2_JUST_MASK);
                setPropStyle(AlParProperty.SL2_JUST_CENTER);
                break;
            // end justify

            case AlFormatTag.TAG_PICT:
                addImages();
                break;
        }

        rtf_tag = 0x00;
        allState.state_parser = (allState.state_parser & 0xf0) | STATE_RTF0_TEXT;
    }

    private int stateParserAfterTAG_U = STATE_RTF2_SKIPONE;
    private int readStringMode = 0;

    private void acceptChar(char ch) {

        if ((allState.state_parser & 0xf0) == 0) {
            if (ch == 0x00) {
                switch (rtf_tag) {
                    case AlFormatTag.TAG_UC:
                        try {
                            ch = (char) Integer.parseInt(rtf_param.toString());
                            stateParserAfterTAG_U =  ch > 0 ? STATE_RTF2_SKIPONE : 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case AlFormatTag.TAG_TAB:
                        doTextChar(' ', true);
                        break;
                    case AlFormatTag.TAG_LQUOTE:
                        doTextChar((char) 0x2018, true);
                        break;
                    case AlFormatTag.TAG_ENDASH:
                        doTextChar((char) 0x2013, true);
                        break;
                    case AlFormatTag.TAG_EMDASH:
                        doTextChar((char) 0x2014, true);
                        break;
                    case AlFormatTag.TAG_RQUOTE:
                        doTextChar((char) 0x2019, true);
                        break;
                    case AlFormatTag.TAG_LDBLQUOTE:
                        doTextChar((char) 0x201c, true);
                        break;
                    case AlFormatTag.TAG_RDBLQUOTE:
                        doTextChar((char) 0x201d, true);
                        break;
                    case AlFormatTag.TAG_U:
                        try {
                            ch = (char) Integer.parseInt(rtf_param.toString());
                            doTextChar(ch, true);
                            allState.state_parser = stateParserAfterTAG_U;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case AlFormatTag.TAG_F:
                        Integer char_cp = fnt_charset_mode.get(rtf_param.toString());
                        if (char_cp != null && (char_cp != (styleStack.buffer[styleStack.position].cp >> 16))) {
                            styleStack.buffer[styleStack.position].cp &= 0xffff;
                            styleStack.buffer[styleStack.position].cp |= char_cp << 16;
                            returnLang0();
                            Log.e("set font " + rtf_param.toString() + " charset =", Integer.toString(char_cp));
                        }
                        break;
                    case AlFormatTag.TAG_PICT:
                        pict_level = styleStack.position;
                        processTag0();
                        allState.state_parser = STATE_RTF2_WAITPICT;
                        break;
                    case AlFormatTag.TAG_INFO:
                        rtf_skip = styleStack.position;
                        allState.state_parser = STATE_RTF2_SKIP;
                        break;
                    case AlFormatTag.TAG_FONTTBL:
                        rtf_skip = styleStack.position;
                        allState.state_parser = STATE_RTF2_SKIPFNT;
                        break;
                    case AlFormatTag.TAG_LISTTABLE:
                        rtf_skip = styleStack.position;
                        allState.state_parser = STATE_RTF2_SKIP;
                        break;
                    case AlFormatTag.TAG_COLORTBL:
                    case AlFormatTag.TAG_NONSHPPICT:
                    case AlFormatTag.TAG_SHPRSLT:
                    case AlFormatTag.TAG_HEADER:
                    case AlFormatTag.TAG_STYLESHEET:
                    case AlFormatTag.TAG_PNTEXT:
                    case AlFormatTag.TAG_FOOTER:
                    case AlFormatTag.TAG_FOOTNOTE:
                        rtf_skip = styleStack.position;
                        allState.state_parser = STATE_RTF2_SKIP;
                        break;
                    case AlFormatTag.TAG_RTFSTAR:
                        rtf_skip = styleStack.position;
                        allState.state_parser = STATE_RTF2_COMMENT;
                        break;
                    default:
                        processTag0();
                        break;
                }
            } else {
                switch (ch) {
                    case 0x0a:
                    case 0x0d:
                        newParagraph();
                        break;
                    default:
                        doTextChar(ch, true);
                }
            }
        } else {
            switch (allState.state_parser & 0xf0) {
                case STATE_RTF2_COMMENT:
                    if (ch == 0x00) {
                        switch (rtf_tag) {
                            case AlFormatTag.TAG_RTFDECLEVEL:
                                if (styleStack.position < rtf_skip) {
                                    rtf_skip = 0;
                                    allState.state_parser = 0;
                                }
                                break;
                            case AlFormatTag.TAG_SHPPICT:
                            case AlFormatTag.TAG_SHPINST:
                                allState.state_parser = STATE_RTF2_MAYBEPICT;
                                break;
                            case AlFormatTag.TAG_LISTTABLE:
                                allState.state_parser = STATE_RTF2_SKIP;
                                break;
                            case AlFormatTag.TAG_FLDINST:
                                readStringMode = RTF_READSTRING_FLDINST;
                                allState.state_parser = STATE_RTF2_READSTRING;
                                resetTAGCRC();
                                break;
                            case AlFormatTag.TAG_BKMKSTART:
                                readStringMode = RTF_READSTRING_BKMKSTART;
                                allState.state_parser = STATE_RTF2_READSTRING;
                                resetTAGCRC();
                                break;
                        }
                    }
                    break;
                case STATE_RTF2_READSTRING:
                    if (ch == 0x00) {
                        switch (rtf_tag) {
                            case AlFormatTag.TAG_RTFDECLEVEL:
                                if (styleStack.position < rtf_skip) {

                                    switch (readStringMode) {
                                    case RTF_READSTRING_FLDINST:
                                        String slink = getHyperLink(rtf_param.toString());

                                        if (slink.length() > 0) {
                                            addCharFromTag((char)AlStyles.CHAR_LINK_S, false);
                                            addTextFromTag(slink, false);
                                            addCharFromTag((char)AlStyles.CHAR_LINK_E, false);
                                            setTextStyle(AlStyles.STYLE_LINK);
                                        }
                                        break;
                                    case RTF_READSTRING_BKMKSTART:
                                        if (rtf_param != null) {
                                            lnk.add(AlOneLink.add(rtf_param.toString(), size, 0));
                                        }
                                        break;
                                    }

                                    rtf_skip = 0;
                                    allState.state_parser = 0;
                                }
                                break;
                        }
                    } else {
                        rtf_param.append(ch);
                    }
                    break;
                case STATE_RTF2_MAYBEPICT:
                    if (ch == 0x00) {
                        switch (rtf_tag) {
                            case AlFormatTag.TAG_RTFDECLEVEL:
                                if (styleStack.position < rtf_skip) {
                                    rtf_skip = 0;
                                    allState.state_parser = 0;
                                }
                                break;
                            case AlFormatTag.TAG_PICT:
                                pict_level = styleStack.position;
                                processTag0();
                                allState.state_parser = STATE_RTF2_WAITPICT;
                                break;
                        }
                    }
                    break;
                case STATE_RTF2_SKIPFNT:
                    if (ch == 0x00) {
                        switch (rtf_tag) {
                            case AlFormatTag.TAG_F:
                                if (fnt_level == -1)
                                    fnt_level = styleStack.position;
                                fnt_name = rtf_param.toString();
                                break;
                            case AlFormatTag.TAG_FCHARSET:
                                if (fnt_level == -1)
                                    fnt_level = styleStack.position;

                                try {
                                    fnt_charset = Integer.parseInt(rtf_param.toString());
                                    fnt_charset = getCodePageByCharset(fnt_charset);
                                } catch (Exception e) {
                                    fnt_charset = -1;
                                }

                                break;
                            case AlFormatTag.TAG_RTFDECLEVEL:
                                if (styleStack.position < rtf_skip) {
                                    rtf_skip = 0;
                                    allState.state_parser = 0;
                                } else
                                if (styleStack.position < fnt_level) {
                                    if (fnt_name != null && fnt_charset != -1) {
                                        fnt_charset_mode.put(fnt_name, fnt_charset);
                                        Log.e("fnttbl", fnt_name + '>' + fnt_charset);
                                    }
                                    fnt_name = null;
                                    fnt_charset = -1;
                                    fnt_level = -1;
                                }
                                break;
                        }
                    }
                    break;
                case STATE_RTF2_SKIP:
                    if (ch == 0x00) {
                        switch (rtf_tag) {
                            case AlFormatTag.TAG_RTFDECLEVEL:
                                if (styleStack.position < rtf_skip) {
                                    rtf_skip = 0;
                                    allState.state_parser = 0;
                                }
                                break;
                        }
                    }
                    break;
                case STATE_RTF2_SKIPONE:
                    allState.state_parser = 0;
                    break;
                case STATE_RTF2_WAITPICT:
                    if (ch == 0x00) {
                        switch (rtf_tag) {
                            case AlFormatTag.TAG_BIN:
                                allState.skipped_flag = InternalFunc.str2int(rtf_param, 10);
                                if (allState.skipped_flag > 0) {
                                    allState.state_parser = STATE_RTF0_SKIP_COUNTBYTES;
                                    allState.image_start = allState.start_position + 1;
                                    rtf_tag = 0x00;
                                    allState.image_type = AlOneImage.IMG_BINARYINFILE;
                                    return;
                                }
                                break;
                            case AlFormatTag.TAG_RTFDECLEVEL:
                                if (styleStack.position < pict_level) {
                                    pict_level = 0;
                                    allState.state_parser = rtf_skip < styleStack.position ? 0 : STATE_RTF2_SKIP;
                                }
                                if (rtf_skip < styleStack.position)
                                    rtf_skip = 0;
                                break;
                        }
                    } else
                    if (ch > 0x20 && pict_level == styleStack.position){
                        allState.state_parser = STATE_RTF2_PICT;
                        allState.image_start = allState.start_position;
                        allState.image_type = AlOneImage.IMG_HEX;
                    }
                    break;
                case STATE_RTF2_PICT:
                    if (ch == 0x00) {
                        switch (rtf_tag) {
                            case AlFormatTag.TAG_BIN:
                                allState.skipped_flag = InternalFunc.str2int(rtf_param, 10);
                                if (allState.skipped_flag > 0) {
                                    allState.state_parser = STATE_RTF0_SKIP_COUNTBYTES;
                                    allState.image_start = allState.start_position + 1;
                                    rtf_tag = 0x00;
                                    allState.image_type = AlOneImage.IMG_BINARYINFILE;
                                    return;
                                }
                                break;
                            case AlFormatTag.TAG_RTFDECLEVEL:
                                if (styleStack.position < pict_level) {

                                    if (/*allState.isOpened && */allState.image_start > 0 && allState.image_name != null) {
                                        allState.image_stop = allState.start_position_par;
                                        im.add(AlOneImage.add(allState.image_name, allState.image_start, allState.image_stop, allState.image_type));
                                        //addImage(AlImage.addImage(image_name, image_start, image_stop, AlImage.IMG_HEX));
                                    }

                                    allState.image_start = allState.image_stop = -1;
                                    allState.image_name = null;

                                    pict_level = 0;
                                    allState.state_parser = rtf_skip < styleStack.position ? 0 : STATE_RTF2_SKIP;
                                }

                                if (rtf_skip < styleStack.position)
                                    rtf_skip = 0;
                                break;
                        }
                    }
                    break;
            }
        }

        rtf_tag = 0x00;
        allState.state_parser = (allState.state_parser & 0xf0);
    }



    @Override
    protected void parser(final int start_pos, final int stop_posRequest) {
        // this code must be in any parser without change!!!
        int 	buf_cnt = 0, i, j;
        char 	ch, ch1;

        chinese_num_hex = 0;


        for (i = start_pos; i < stop_posRequest;) {
            buf_cnt = AlFiles.LEVEL1_FILE_BUF_SIZE;
            if (i + buf_cnt > stop_posRequest) {
                buf_cnt = aFiles.getByteBuffer(i, parser_inBuff, stop_posRequest - i + 2);
                if (buf_cnt > stop_posRequest - i)
                    buf_cnt = stop_posRequest - i;
            } else {
                buf_cnt = aFiles.getByteBuffer(i, parser_inBuff, buf_cnt + 2);
                buf_cnt -= 2;
            }

            label_get_next_char:
            for (j = 0; j < buf_cnt;) {
                allState.start_position = i + j;
                ch = (char)parser_inBuff[j++];
                ch &= 0xff;

                if (ch >= 0x80) {
                    switch (autoCodePage ? (styleStack.buffer[styleStack.position].cp & 0xffff) : use_cpR0) {
                        case 932:
                            switch (ch) {
                                case 0x80 :
                                case 0xfd :
                                case 0xfe :
                                case 0xff : ch = 0x0000; break;
                                default :
                                    if (ch >= 0xa1 && ch <= 0xdf) {
                                        ch = (char) (ch + 0xfec0);
                                        break;
                                    }
                                    ch1 = (char) (parser_inBuff[j++] & 0xff);
                                    ch = (ch1 >= 0x40 && ch1 <= 0xfc) ? CP932.getChar(ch, ch1) : 0x00;
                                    break;
                            }
                            break;
                        case 936:
                            switch (ch) {
                                case 0x80 : ch = 0x20AC; break;
                                case 0xff : ch = 0x0000; break;
                                default :
                                    ch1 = (char) (parser_inBuff[j++] & 0xff);
                                    ch = (ch1 >= 0x40 && ch1 <= 0xfe) ? CP936.getChar(ch, ch1) : 0x00;
                                    break;
                            }
                            break;
                        case 949:
                            switch (ch) {
                                case 0x80 :
                                case 0xff : ch = 0x0000; break;
                                default :
                                    ch1 = (char) (parser_inBuff[j++] & 0xff);
                                    ch = (ch1 >= 0x41 && ch1 <= 0xfe) ? CP949.getChar(ch, ch1) : 0x00;
                                    break;
                            }
                            break;
                        case 950:
                            switch (ch) {
                                case 0x80 :
                                case 0xff : ch = 0x0000; break;
                                default :
                                    ch1 = (char) (parser_inBuff[j++] & 0xff);
                                    ch = (ch1 >= 0x40 && ch1 <= 0xfe) ? CP950.getChar(ch, ch1) : 0x00;
                                    break;
                            }
                            break;
                        default:
                            ch = data_cp[ch - 0x80];
                    }
                }
                if ((ch & AlStyles.STYLE_MASK_4CODECONVERT) == AlStyles.STYLE_BASE_4CODECONVERT)
                    ch = 0x00;
                // end must be code
                /////////////////// Begin Real Parser

                label_repeat_letter:
                while (true)
                    switch (allState.state_parser & 0x0f) {
                        case STATE_RTF0_SKIP_COUNTBYTES:
                            allState.skipped_flag--;
                            if (allState.skipped_flag == 0) {
                                allState.state_parser = STATE_RTF2_PICT;
                            }
                            continue label_get_next_char;
                        case STATE_RTF0_TEXT:
                            allState.start_position_par = allState.start_position;

                            if (ch == '{') {
                                incLevel();
                            } else
                            if (ch == '}') {
                                decLevel();
                            } else
                            if (ch == '\\') {
                                allState.state_parser = (allState.state_parser & 0xf0) | STATE_RTF0_TAG_START;
                            } else
                            if (ch >= 0x20){
                                if ((ch & AlStyles.STYLE_BASE_MASK) != AlStyles.STYLE_BASE0)
                                    acceptChar(ch);//doTextChar1(ch, true);
                            }
                            continue label_get_next_char;
                        case STATE_RTF0_TAG_START:
                            if (AlUnicode.isRTFManage(ch)) {
                                resetTAGCRC();
                                rtf_tag = (rtf_tag * 31) + Character.toLowerCase(ch);
                                allState.state_parser = (allState.state_parser & 0xf0) | STATE_RTF0_TAG_PROCESS;
                            } else
                            if (ch <= 0x20) {
                                if (ch == 0x0a || ch == 0x0d) {
                                    acceptChar(ch);
                                } else
                                if (ch == 0x09) {
                                    acceptChar(' ');
                                } else
                                    allState.state_parser = (allState.state_parser & 0xf0) | STATE_RTF0_TEXT;
                            } else
                            if (ch == '\\') {
                                acceptChar('\\');
                            } else
                            if (ch == '_') {
                                acceptChar('-');
                            } else
                            if (ch == '~') {
                                acceptChar((char)0xa0);
                            } else
                            if (ch == '-') {
                                allState.state_parser = (allState.state_parser & 0xf0) | STATE_RTF0_TEXT;
                            } else
                            if (ch == '{') {
                                acceptChar('{');
                            } else
                            if (ch == '}') {
                                acceptChar('}');
                            } else
                            if (ch == '*') {
                                rtf_tag = AlFormatTag.TAG_RTFSTAR;
                                acceptChar((char)0x00);
                            } else
                            if (ch == '\'') {
                                resetTAGCRC();
                                allState.state_parser = (allState.state_parser & 0xf0) | STATE_RTF0_TWO_HEX1;
                            } else {
                                allState.state_parser = (allState.state_parser & 0xf0) | STATE_RTF0_TEXT;
                            }
                            continue label_get_next_char;
                        case STATE_RTF0_TAG_PROCESS:
                            if (AlUnicode.isRTFManage(ch)) {
                                rtf_tag = (rtf_tag * 31) + Character.toLowerCase(ch);
                            } else
                            if (ch == '\\' || ch == '{' || ch == '}') {
                                acceptChar((char)0x00);
                                continue label_repeat_letter;
                            } else
                            if (ch <= ' ' || ch == ';') {
                                acceptChar((char)0x00);
                            } else
                            if (ch == '-' || (ch >= '0' && ch <= '9')) {
                                rtf_param.append(ch);
                                allState.state_parser = (allState.state_parser & 0xf0) | STATE_RTF0_TAG_END;
                            } else {
                                acceptChar((char)0x00);
                                continue label_repeat_letter;
                            }
                            continue label_get_next_char;
                        case STATE_RTF0_TAG_END:
                            if (ch == '\\' || ch == '{' || ch == '}') {
                                acceptChar((char)0x00);
                                continue label_repeat_letter;
                            } else
                            if (ch >= '0' && ch <= '9') {
                                rtf_param.append(ch);
                            } else
                            if (ch == 0x20 && rtf_tag == AlFormatTag.TAG_U) {
                                acceptChar((char)0x00);
                                continue label_repeat_letter;
                            } else {
                                acceptChar((char)0x00);
                                if (ch > 0x20 && ch != ';')
                                    continue label_repeat_letter;
                            }
                            continue label_get_next_char;
                        case STATE_RTF0_TWO_HEX1:
                            rtf_param.append(ch);
                            allState.state_parser = (allState.state_parser & 0xf0) | STATE_RTF0_TWO_HEX2;
                            continue label_get_next_char;
                        case STATE_RTF0_TWO_HEX2:
                            rtf_param.append(ch);
                            addTwoHex();
                            allState.state_parser = (allState.state_parser & 0xf0) | STATE_RTF0_TEXT;
                            continue label_get_next_char;
                    }

                /////////////////// End Real Parser
                // this code must be in any parser without change!!!
            }
            i += j;
        }
        newParagraph();
        // end must be code

    }

    public int getLangToCP(int lang) {
        switch (lang) {
            case/*Afrikaans	af	af	*/				1078: 	return		1252;
            case/*Albanian	sq	sq	*/				1052: 	return		1250; // 1252?
            //case/*Amharic	am	am	*/				1118: 	return		-1; //???
            case/*Arabic General*/					1: 		return 		1256;
            case/*Arabic - Algeria	ar	ar-dz	*/	5121: 	return		1256;
            case/*Arabic - Bahrain	ar	ar-bh	*/	15361: 	return		1256;
            case/*Arabic - Egypt	ar	ar-eg	*/	3073: 	return		1256;
            case/*Arabic - Iraq	ar	ar-iq	*/		2049: 	return		1256;
            case/*Arabic - Jordan	ar	ar-jo	*/	11265: 	return		1256;
            case/*Arabic - Kuwait	ar	ar-kw	*/	13313: 	return		1256;
            case/*Arabic - 	ar	ar-lb	*/			12289: 	return		1256;
            case/*Arabic - Libya	ar	ar-ly	*/	4097: 	return		1256;
            case/*Arabic - 	ar	ar-ma	*/			6145: 	return		1256;
            case/*Arabic - Oman	ar	ar-om	*/		8193: 	return		1256;
            case/*Arabic - Qatar	ar	ar-qa	*/	16385: 	return		1256;
            case/*Arabic - Saud	ar	ar-sa	*/		1025: 	return		1256;
            case/*Arabic - Syria	ar	ar-sy	*/	10241: 	return		1256;
            case/*Arabic - Tunisia	ar	ar-tn	*/	7169: 	return		1256;
            case/*Arabic - 	ar	ar-ae*/				14337: 	return		1256;
            case/*Arabic - Yemen	ar	ar-ye*/		9217: 	return		1256;
            case/*Armenian	hy	hy*/				1067: 	return		1252; //???
            case/*Assamese	as	as*/				1101: 	return		1252; //???
            case/*Azeri - Cyrillic	az	az-az*/		2092: 	return		1251;
            case/*Azeri - Latin	az	az-az*/			1068: 	return		1254;
            case/*Basque	eu	eu*/				1069: 	return		1252;
            case/*Belarusian	be	be*/			1059: 	return		1251;
            //case/*Bengali - 	bn	bn*/			2117: 	return		-1; //???
            case/*Bengali - India	bn	bn*/		1093: 	return		1252; //???
            case 									4122:	return 		1252;
            //case/*Bosnian	bs	bs*/				5146: 	return		-1; //???
            case/*Bulgarian	bg	bg*/				1026: 	return		1251;
            case/*Burmese	my	my*/				1109: 	return		1252; //???
            case/*Catalan	ca	ca*/				1027: 	return		1252;
            case/*Chinese - China	zh	zh-cn*/		2052: 	return		950; //???
            case/*Chinese - 	zh	zh-hk*/			3076: 	return		950; //???
            case/*Chinese - SAR	zh	zh-mo*/			5124: 	return		950; //???
            case/*Chinese - 	zh	zh-sg*/			4100: 	return		950; //???
            case/*Chinese - 	zh	zh-tw*/			1028: 	return		950; //???
            case/*Chinese General*/					4: 		return 		950;
            case/*Croatian		hr*/				1050: 	return		1250;
            case/*Czech	cs	cs*/					1029: 	return		1250;
            case/*Danish	da	da*/				1030: 	return		1252;
            //case/*Divehi; Dhivehi	dv	dv*/		1125: 	return		-1; //???
            case/*Dutch - Belgium	nl	nl-be*/		2067: 	return		1252;
            case/*Dutch - 	nl	nl-nl*/				1043: 	return		1252;
            //case/*Edo*/								1126: 	return		-1; //???
            case/*English*/							9: 		return		1252;
            case/*English - 	en	en-au*/			3081: 	return		1252;
            case/*English - 	en	en-bz*/			10249: 	return		1252;
            case/*English - 	en	en-ca*/			4105: 	return		1252;
            case/*English - 	en	en-cb*/			9225: 	return		1252;
            case/*English - Britain	en	en-gb*/		2057: 	return		1252;
            case/*English - India	en	en-in*/		16393: 	return		1252; //???
            case/*English - Ireland	en	en-ie*/		6153: 	return		1252;
            case/*English - 	en	en-jm*/			8201: 	return		1252;
            case/*English - New 	en	en-nz*/		5129: 	return		1252;
            case/*English - 	en	en-ph*/			13321: 	return		1252;
            case/*English - Africa	en	en-za*/		7177: 	return		1252;
            case/*English - 	en	en-tt*/			11273: 	return		1252;
            case/*English - States	en	en-us*/		1033: 	return		1252;
            case/*English - 	en*/				12297: 	return		1252;
            case/*Estonian	et	et*/				1061: 	return		1257;
            case/*Faroese	fo	fo*/				1080: 	return		1252;
            case/*Farsi - Persian	fa	fa*/		1065: 	return		1256;
            //case/*Filipino*/						1124: 	return		-1; //???
            case/*Finnish	fi	fi*/				1035: 	return		1252;
            case/*French*/							8204: 	return		1252;
            case/*French - 	fr	fr-be*/				2060: 	return		1252;
            case/*French - Cameroon	fr*/			11276: 	return		1252; //???
            case/*French - 	fr	fr-ca*/				3084: 	return		1252;
            case/*French - Congo	fr*/			9228: 	return		1252; //???
            case/*French - Cote 	fr*/			12300: 	return		1252; //???
            case/*French - France	fr	fr-fr*/		1036: 	return		1252;
            case/*French - 	fr	fr-lu*/				5132: 	return		1252;
            case/*French - Mali	fr*/				13324: 	return		1252; //???
            case/*French - 	fr*/					6156: 	return		1252;
            case/*French - 	fr*/					14348: 	return		1252; //???
            case/*French - 	fr*/					10252: 	return		1252; //???
            case/*French - 	fr	fr-ch*/				4108: 	return		1252;
            case/*French - Indies	fr*/			7180: 	return		1252; //???
            case/*Frisian -*/ 						1122: 	return		1252; //???
            case/*FYRO 	mk	mk*/					1071: 	return		1251;
            case/*Gaelic - Ireland	gd	gd-ie*/		2108: 	return		1252; //???
            case/*Gaelic - 	gd	gd*/				1084: 	return		1252; //???
            case/*Galician	gl*/					1110: 	return		1252;
            case/*Georgian	ka*/					1079: 	return		1252; //???
            case/*German - 	de	de-at*/				3079: 	return		1252;
            case/*German - 	de	de-de*/				1031: 	return		1252;
            case/*German - 	de	de-li*/				5127: 	return		1252;
            case/*German - 	de	de-lu*/				4103: 	return		1252;
            case/*German - 	de	de-ch*/				2055: 	return		1252;
            case/*Greek	el	el*/					1032: 	return		1253;
            //case/*Guarani - 	gn	gn*/			1140: 	return		-1; //???
            case/*Gujarati	gu	gu*/				1095: 	return		1252; //???
            case/*Hebrew	he	he*/				1037: 	return		1255;
            case/*Hindi	hi	hi*/					1081: 	return		1252; //???
            case/*Hungarian	hu	hu*/				1038: 	return		1250;
            case/*Icelandic	is	is*/				1039: 	return		1252;
            //case/*Igbo - Nigeria*/					1136: 	return		-1; //???
            case/*Indonesian	id	id*/			1057: 	return		1252;
            case/*Italian - Italy	it	it-it*/		1040: 	return		1252;
            case/*Italian - 	it	it-ch*/			2064: 	return		1252;
            case/*Japanese	ja	ja*/				1041: 	return		932; //???
            case/*Kannada	kn	kn*/				1099: 	return		1252; //???
            case/*Kashmiri India*/					2144:	return 		1252;
            case/*Kashmiri	ks	ks*/				1120: 	return		1252; //???
            case/*Kazakh	kk	kk*/				1087: 	return		1251;
            case/*Khmer	km	km*/					1107: 	return		1252; //???
            case/*Konkani*/							1111: 	return		1252; //???
            case/*Korean	ko	ko*/				1042: 	return		1252; //???
            case/*Korean Johab*/					2066: 	return 		1252;
            case/*Kyrgyz - Cyrillic*/				1088: 	return		1251;
            case/*Lao	lo	lo*/					1108: 	return		1252; //???
            //case/*Latin	la	la*/					1142: 	return		-1; //???
            case/*Latvian	lv	lv*/				1062: 	return		1257;
            case/*Lithuanian	lt	lt*/			1063: 	return		1257;
            case/*Lithuanian Classic*/				2087: 	return 		1257;
            case/*Malay - Brunei	ms	ms-bn*/		2110: 	return		1252;
            case/*Malay - 	ms	ms-my*/				1086: 	return		1252;
            case/*Malayalam	ml	ml*/				1100: 	return		1252; //???
            case/*Maltese	mt	mt*/				1082: 	return		1252; //???
            case/*Manipuri*/						1112: 	return		1252; //???
            //case/*Maori	mi	mi*/					1153: 	return		-1; //???
            case/*Marathi	mr	mr*/				1102: 	return		1252; //???
            //case/*Mongolian	mn	mn*/				2128: 	return		-1; //???
            case/*Mongolian	mn	mn*/				1104: 	return		1251;
            case/*Nepali	ne	ne*/				1121: 	return		1252; //???
            case/*Nepali India*/					2145: 	return 		1252;
            case/*Norwegian - 	nb	no-no*/			1044: 	return		1252;
            case/*Norwegian - 	nn	no-no*/			2068: 	return		1252;
            case/*Oriya	or	or*/					1096: 	return		1252; //???
            case/*Polish	pl	pl*/				1045: 	return		1250;
            case/*Portuguese - 	pt	pt-br*/			1046: 	return		1252;
            case/*Portuguese - 	pt	pt-pt*/			2070: 	return		1252;
            case/*Punjabi	pa	pa*/				1094: 	return		1252; //???
            case/*Raeto-Romance	rm	rm*/			1047: 	return		1252; //???
            case/*Romanian - 	ro	ro-mo*/			2072: 	return		1250; //???
            case/*Romanian - 	ro	ro*/			1048: 	return		1250;
            case/*Russian	ru	ru*/				1049: 	return		1251;
            case/*Russian - 	ru	ru-mo*/			2073: 	return		1251; //???
            case/*Sami Lappish*/					1083: 	return		1252; //???
            case/*Sanskrit	sa	sa*/				1103: 	return		1252; //???
            case/*Serbian - Cyrillic	sr	sr-sp*/	3098: 	return		1251;
            case/*Serbian - Latin	sr	sr-sp*/		2074: 	return		1250;
            case/*Sesotho (Sutu)*/					1072: 	return		1252; //???
            case/*Setsuana	tn	tn*/				1074: 	return		1252; //???
            case/*Sindhi	sd	sd*/				1113: 	return		1252; //???
            //case/*Sinhala; 	si	si*/				1115: 	return		-1; //???
            case/*Slovak	sk	sk*/				1051: 	return		1250;
            case/*Slovenian	sl	sl*/				1060: 	return		1250;
            //case/*Somali	so	so*/				1143: 	return		-1; //???
            case/*Sorbian	sb	sb*/				1070: 	return		1252; //???
            case/*Spanish*/							3082: 	return		1252;
            case/*Spanish - 	es	es-ar*/			11274: 	return		1252;
            case/*Spanish - 	es	es-bo*/			16394: 	return		1252;
            case/*Spanish - Chile	es	es-cl*/		13322: 	return		1252;
            case/*Spanish - 	es	es-co*/			9226: 	return		1252;
            case/*Spanish - Costa 	es	es-cr*/		5130: 	return		1252;
            case/*Spanish - 	es	es-do*/			7178: 	return		1252;
            case/*Spanish - 	es	es-ec*/			12298: 	return		1252;
            case/*Spanish - El 	es	es-sv*/			17418: 	return		1252;
            case/*Spanish - 	es	es-gt*/			4106: 	return		1252;
            case/*Spanish - 	es	es-hn*/			18442: 	return		1252;
            case/*Spanish - 	es	es-mx*/			2058: 	return		1252;
            case/*Spanish - 	es	es-ni*/			19466: 	return		1252;
            case/*Spanish - 	es	es-pa*/			6154: 	return		1252;
            case/*Spanish - 	es	es-py*/			15370: 	return		1252;
            case/*Spanish - Peru	es	es-pe*/		10250: 	return		1252;
            case/*Spanish - Rico	es	es-pr*/		20490: 	return		1252;
            case/*Spanish - ()	es	es-es*/			1034: 	return		1252;
            case/*Spanish - 	es	es-uy*/			14346: 	return		1252;
            case/*Spanish - 	es	es-ve*/			8202: 	return		1252;
            case/*Swahili	sw	sw*/				1089: 	return		1252;
            case/*Swedish - 	sv	sv-fi*/			2077: 	return		1252;
            case/*Swedish - 	sv	sv-se*/			1053: 	return		1252;
            //case/*Syriac*/							1114: 	return		-1; //???
            case/*Tajik	tg	tg*/					1064: 	return		1252; //???
            case/*Tamil	ta	ta*/					1097: 	return		1252; //???
            case/*Tatar	tt	tt*/					1092: 	return		1251;
            case/*Telugu	te	te*/				1098: 	return		1252; //???
            case/*Thai	th	th*/					1054: 	return		1252; //???
            case/*Tibetan	bo	bo*/				1105: 	return		1252; //???
            case/*Tsonga	ts	ts*/				1073: 	return		1252; //???
            case/*Turkish	tr	tr*/				1055: 	return		1254;
            case/*Turkmen	tk	tk*/				1090: 	return		1251; //???
            case/*Ukrainian	uk	uk*/				1058: 	return		1251;
            case/*Urdu	ur	ur*/					1056: 	return		1256;
            case/*Urdu India*/						2080: 	return 		1252;
            case/*Uzbek - Cyrillic	uz	uz-uz*/		2115: 	return		1251;
            case/*Uzbek - Latin	uz	uz-uz*/			1091: 	return		1254;
            case/*Venda*/							1075: 	return		1252; //???
            case/*Vietnamese	vi	vi*/			1066: 	return		1258;
            case/*Welsh	cy	cy*/					1106: 	return		1252; //???
            case/*Xhosa	xh	xh*/					1076: 	return		1252; //???
            case/*Yiddish	yi	yi*/				1085: 	return		1252; //???
            case/*Zulu	zu	zu*/					1077: 	return		1252; //???


        }

        return use_cpR0;
    }



}
