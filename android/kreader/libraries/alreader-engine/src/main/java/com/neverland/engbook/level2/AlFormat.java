package com.neverland.engbook.level2;

import android.util.Log;

import com.neverland.engbook.bookobj.AlBookEng.PairTextStyle;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.AlOneSearchResult;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlRandomAccessFile;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.AlMultiFiles;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlOneLink;
import com.neverland.engbook.util.AlOneMultiFile;
import com.neverland.engbook.util.AlOneTable;
import com.neverland.engbook.util.AlOneTableCell;
import com.neverland.engbook.util.AlOneTableRow;
import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlProfileOptions;
import com.neverland.engbook.util.AlStyleStack;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.InternalConst;
import com.neverland.engbook.util.InternalFunc;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public abstract class AlFormat {

    public static final char LEVEL2_TABLETOTEXT = ':';
    public static final String LEVEL2_TABLETOTEXT_STR = ":";
    public static final char LEVEL2_COVERTOTEXT = '*';
    public static final String LEVEL2_COVERTOTEXT_STR = "*";
    public static final char LEVEL2_SPACE = ' ';
    public static final String LEVEL2_LIST0TOTEXT = "\u2022\u00a0";
    public static final String LEVEL2_LIST1TOTEXT = "\u25e6\u00a0";
    public static final String LEVEL2_LIST2TOTEXT = "\u25aa\u00a0";
    public static final String LEVEL2_PRGUSED = ", AlReader.NEW";
    public static final String LEVEL2_PRGUSEDTEST = "AlReader.NEW";
    public static final int LEVEL2_FRM_ADDON_SKIPPEDTEXT = 0x10000000;
    public static final int LEVEL2_FRM_ADDON_CODETEXT = 0x20000000;
    public static final int LEVEL2_FRM_ADDON_SPECIALTEXT = 0x40000000;
    public static final int LEVEL2_MASK_FOR_LEVEL = 0xffff;

    public long lastPageCount;
    public long lastCalcTime;
    public boolean softHyphenPresent;
    protected int softHyphenCount;

    //public boolean	isSupportStyles = false;


    public boolean  isTextFormat = true;



    public boolean haveProblem = false;

    AlPreferenceOptions preference = new AlPreferenceOptions();
    AlStylesOptions styles = new AlStylesOptions();
    int size;

    protected int use_cpR0;
    protected char[] data_cp = null;

    public final ArrayList<String> bookAuthors = new ArrayList<>(0);
    public final ArrayList<String> bookGenres = new ArrayList<>(0);
    public final ArrayList<String> bookSeries = new ArrayList<>(0);
    public String fullPath = null;
    public String bookCRC = null;
    public String bookTitle = null;
    public AlFiles aFiles = null;
    public final ArrayList<AlOneSearchResult> resfind = new ArrayList<>(0);

    public final ArrayList<AlOneParagraph> par0 = new ArrayList<>(0);
    final ArrayList<AlOneImage> im = new ArrayList<>(0);
    private final ArrayList<AlOneTable> ta = new ArrayList<>(0);
    final ArrayList<AlOneLink> lnk = new ArrayList<>(0);
    public final ArrayList<AlOneContent> ttl = new ArrayList<>(0);

    public ArrayList<AlOneImage> getAllImages() {
        return im;
    }

    boolean autoCodePage;

    public final AlStyleStack		styleStack = new AlStyleStack();
    public final AlMultiFiles		multiFiles = new AlMultiFiles();

    public String coverName = null;

    public AlOneTable			currentTable = new AlOneTable();
    protected AlOneTableCell	currentCell = new AlOneTableCell();
    protected AlOneTableRow		currentRow = new AlOneTableRow();

    final AlStateLevel2 allState = new AlStateLevel2();


    final AlParText	parText = new AlParText();
    final AlStoredPar stored_par = new AlStoredPar();
    final AlSpecialBuff specialBuff = new AlSpecialBuff();

    private final AlSlotData slotText = new AlSlotData();
    private final AlSlotData slotNote = new AlSlotData();

    int tune0;
    boolean isFirstParagraph;

    int program_used_position;

    String ident;

    final byte[] parser_inBuff = new byte[AlFiles.LEVEL1_FILE_BUF_SIZE + 2];

    protected final static int MAX_STACK_STYLES = 1024;

    public AlFormat() {
        coverName = null;
        bookAuthors.clear();
        bookGenres.clear();
        bookSeries.clear();
        bookTitle = null;
        bookCRC = null;

        program_used_position = -2;
        tune0 = 0;

        isFirstParagraph = true;

        data_cp = null;

        clearAllArray();

        haveProblem = false;
        autoCodePage = true;
    }

    @Override
    public void finalize() throws Throwable {
        clearAllArray();
        stored_par.data = null;
        super.finalize();
    }

    private void clearAllArray() {
        par0.clear();
        im.clear();
        ta.clear();
        lnk.clear();
        ttl.clear();
        resfind.clear();
    }

    void addContent(AlOneContent ap) {
        if (ap.iType > 9)
            ap.iType = 9;

        if (ttl.size() > 0) {
            AlOneContent a = ttl.get(ttl.size() - 1);
            if (a.name.contentEquals(ap.name) && a.iType == ap.iType && ap.positionS - a.positionS < 1024)
                return;
        }

        ttl.add(ap);
    }

    void newEmptyTextParagraph() {
        styleStack.buffer[styleStack.position].prop |=  AlParProperty.SL2_EMPTY_BEFORE;
        if (allState.state_special_flag)
            specialBuff.add(' ');
    }

    /*void newEmptyStyleParagraph() {
        paragraph |= AlStyles.PAR_PREVIOUS_EMPTY_0;
        allState.text_present = false;
        if (allState.state_special_flag0)
            state_specialBuff0.append(' ');
    }*/

    boolean addTable(AlOneTable ap) {
        ta.add(ap);
        return true;
    }

    void addtestLink(String s) {
        AlOneLink a = AlOneLink.add(s, size, allState.isNoteSection ? 1 : 0);
        lnk.add(a);
    }

    void addtestLink(String s, int tp) {
        lnk.add(AlOneLink.add(s, size, tp));
    }

    void addtestLink(String s, int pos, int tp) {
        lnk.add(AlOneLink.add(s, pos, tp));
    }


    void decULNumber() {
        long tmp = (styleStack.buffer[styleStack.position].prop & AlParProperty.SL2_UL_MASK) >> AlParProperty.SL2_UL_SHIFT;
        if (tmp > 0x00)
            tmp--;
        tmp <<= AlParProperty.SL2_UL_SHIFT;
        styleStack.buffer[styleStack.position].prop &= ~AlParProperty.SL2_UL_BASE;
        styleStack.buffer[styleStack.position].prop |= tmp;
    }

    void incULNumber() {
        long tmp = (styleStack.buffer[styleStack.position].prop & AlParProperty.SL2_UL_MASK) >> AlParProperty.SL2_UL_SHIFT;
        if (tmp < 0x0f)
            tmp++;
        tmp <<= AlParProperty.SL2_UL_SHIFT;
        styleStack.buffer[styleStack.position].prop &= ~AlParProperty.SL2_UL_BASE;
        styleStack.buffer[styleStack.position].prop |= tmp;
    }

    void clearULNumber() {
        styleStack.buffer[styleStack.position].prop &= ~AlParProperty.SL2_UL_BASE;
    }


    void closeOpenNotes() {
        if (lnk.size() > 0) {
            AlOneLink al = lnk.get(lnk.size() - 1);
            if (al.iType == 1 && al.positionE == -1)
                lnk.get(lnk.size() - 1).positionE = size;
        }
    }


    char getConvertChar(int cp, AlIntHolder pos) {
        byte[] tmp_buff = new byte[4];
        aFiles.getByteBuffer(pos.value, tmp_buff, 4);
        AlIntHolder j = new AlIntHolder(0);
        char ch = AlUnicode.byte2Wide(cp, tmp_buff, j);
        pos.value += j.value;
        return ch;
    }

    int getBOMCodePage(boolean bomUTF16, boolean bomUTF8, boolean realUTF8, boolean realAll) {
        char ch;
        AlIntHolder pos = new AlIntHolder(0);

        if (bomUTF16) {
            pos.value = 0;
            ch = getConvertChar(TAL_CODE_PAGES.CP1200, pos);
            if (ch == 0xfeff)
                return TAL_CODE_PAGES.CP1200;
            if (ch == 0xfffe)
                return TAL_CODE_PAGES.CP1201;
        }

        if (bomUTF8) {
            pos.value = 0;
            ch = getConvertChar(TAL_CODE_PAGES.CP65001, pos);
            if (ch == 0xfeff)
                return TAL_CODE_PAGES.CP65001;
        }

        if (realUTF8) {
            byte[] data = new byte[4096];
            pos.value = 16384;
            if (pos.value > aFiles.getSize())
                pos.value = aFiles.getSize() - 4096;
            if (pos.value < 0)
                pos.value = 0;
            int cnt = aFiles.getByteBuffer(pos.value, data, 4096);
            int state = 0, seq_ok = 0, seq_all = 0, i;
            boolean noErr = true;
            for (i = 0; i < cnt; i++) {
                ch = (char) (data[i] & 0xff);
                if ((ch < 0x80) || ((ch & 0xc0) == 0xc0))
                    break;
            }
            for (; i < cnt && noErr; i++) {
                ch = (char) (data[i] & 0xff);
                switch (state) {
                    case 0:
                        if ((ch & 0x80) == 0)
                            continue;
                        seq_all++;
                        if ((ch & 0xfe) == 0xfc)
                            state = 62;
                        else if ((ch & 0xfc) == 0xf8)
                            state = 52;
                        else if ((ch & 0xf8) == 0xf0)
                            state = 42;
                        else if ((ch & 0xf0) == 0xe0)
                            state = 32;
                        else if ((ch & 0xe0) == 0xc0)
                            state = 22;
                        else
                            noErr = false;
                        break;
                    case 32:
                    case 42:
                    case 43:
                    case 52:
                    case 53:
                    case 54:
                    case 62:
                    case 63:
                    case 64:
                    case 65:
                        seq_all++;
                        if ((ch & 0xc0) == 0x80) state++;
                        else noErr = false;
                        break;
                    case 22:
                    case 33:
                    case 44:
                    case 55:
                    case 66:
                        seq_all++;
                        if ((ch & 0xc0) == 0x80) {
                            seq_ok++;
                            state = 0;
                        } else noErr = false;
                        break;
                }
            }

            if (noErr) {
                if (seq_all > 0) {
                    return TAL_CODE_PAGES.CP65001;
                }
            }
        }

        if (realAll) {
            byte[] data = new byte[8192];
            pos.value = 0;
            int rsize = aFiles.getSize();

            if (rsize > 12288)
                pos.value = 4096;

            if (rsize >= 8192)
                rsize = 8192;

            if (rsize != 0) {
                rsize = aFiles.getByteBuffer(pos.value, data, rsize);

                try {
                    UniversalDetector detector = new UniversalDetector(null);
                    detector.handleData(data, 0, rsize);
                    detector.dataEnd();

                    String encoding = detector.getDetectedCharset();
                    detector.reset();

                    if (encoding != null) {
                        int cp = AlUnicode.getTestCodePage(encoding, ident);
                        if (cp != -1)
                            Log.d("DETECT CODE PAGE", encoding);
                        return cp;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return TAL_CODE_PAGES.AUTO;
    }

    protected void setCP(int newcp) {
        use_cpR0 = AlUnicode.int2cp(newcp);
        data_cp = AlUnicode.getDataCP(use_cpR0);
    }

    int getCP() {
        if (autoCodePage)
            return TAL_CODE_PAGES.AUTO;
        return use_cpR0;
    }

    private void addRealParagraph(AlOneParagraph a) {
        par0.add(a);
    }

    void setTextStyle(long tag) {
        allState.clearSkipped();
        styleStack.buffer[styleStack.position].paragraph |= tag;
        if (parText.length != 0)
            doTextChar(getTextStyle(), false);
        allState.restoreSkipped();
    }

    void clearTextStyle(long tag) {
        allState.clearSkipped();
        styleStack.buffer[styleStack.position].paragraph &= ~tag;
        if (parText.length != 0)
            doTextChar(getTextStyle(), false);
        allState.restoreSkipped();
    }

    protected char getTextStyle() {
        return (char) (AlStyles.STYLE_BASE0 + (styleStack.buffer[styleStack.position].paragraph & AlStyles.STYLE_MASK));
    }

    protected char getTextSize() {
        return (char)(AlStyles.STYLE_BASE1 | styleStack.getActualSize());
    }

    protected char selectTextColor(long colorIndex) {
        return (char)(AlStyles.STYLE_BASE1 + AlStyles.STYLE_BASE_SETTEXTCOLOR + ((colorIndex & AlStyles.SL_COLOR_MASK) >> AlStyles.SL_COLOR_SHIFT));
    }

    void setParagraphStyle(long tag) {
        styleStack.buffer[styleStack.position].paragraph |= tag;
    }

    void clearParagraphStyle(long tag) {
        styleStack.buffer[styleStack.position].paragraph &= (~tag);
    }

    void setStateStyle(long tag) {
        allState.description |= tag;
    }

    void clearStateStyle(long tag) {
        allState.description &= (~tag);
    }

    void setPropStyle(long tag) {
        styleStack.buffer[styleStack.position].prop |= tag;
    }

    void clearPropStyle(long tag) {
        styleStack.buffer[styleStack.position].prop &= (~tag);
    }

    void newParagraph() {
        if (parText.length != 0) {
            if (parText.haveLetter) {
                AlOneParagraph a = new AlOneParagraph();

                a.positionS = parText.positionS;
                a.positionE = parText.positionE;
                a.start = parText.sizeStart;
                a.paragraph = parText.paragraph;
                a.prop = parText.prop;
                a.level = parText.level;
                a.table_start = parText.tableStart;
                a.table_counter = parText.tableCounter;

                a.length = parText.copy(a);

                addRealParagraph(a);

                styleStack.clearFlagInAllParentsProp(AlParProperty.SL2_BREAK_BEFORE | AlParProperty.SL2_EMPTY_BEFORE | AlParProperty.SL2_MARGT_MASK);
                styleStack.clearFlagInAllParentsParagraph(AlStyles.SL_FIRSTP);
            } else {
                size -= parText.length;
                if (!parText.haveLetter && ((styleStack.buffer[styleStack.position].prop & AlParProperty.SL2_JUSTIFY_POEM) == 0L))
                newEmptyTextParagraph();
            }
        }

        parText.clear();
        if (allState.state_special_flag)
            specialBuff.add(' ');
    }

    void addTextFromTag(String s, boolean addSpecial) {
        int i;
        final int l = s.length();
        allState.insertFromTag = true;
        for (i = 0; i < l; i++)
            doTextChar(s.charAt(i), addSpecial);
        allState.insertFromTag = false;
    }

    void addTextFromTag(StringBuilder s, boolean addSpecial) {
        int i;
        final int l = s.length();
        allState.insertFromTag = true;
        for (i = 0; i < l; i++)
            doTextChar(s.charAt(i), addSpecial);
        allState.insertFromTag = false;
    }

    void addCharFromTag(char s, boolean addSpecial) {
        allState.insertFromTag = true;
        doTextChar(s, addSpecial);
        allState.insertFromTag = false;
    }

	/*void incStylePoint(long style, int tag) {
		styleStack_point++;
		if (styleStack_point >= styleStack.size()) {
			styleStack.add(AlOneStyleStack.addStyleStack(style, tag));
		} else {
			(styleStack.get(styleStack_point)).real_style = style;
		}
	}

	long decStylePoint(int tag) {
		long res = styleStack.get(styleStack_point).real_style;
		if (styleStack_point > 0)
			styleStack_point--;
		return 0;
	}*/

    public int getSize() {
        return size;
    }

    public void removeCover() {
        if (size > 0 && par0.size() >= 2 && par0.get(0).length == 3 && par0.get(0).ptext[1] == LEVEL2_COVERTOTEXT) {
            par0.get(1).start = 0;

            char tmp[] = new char[par0.get(1).length + 3];
            System.arraycopy(par0.get(1).ptext, 0, tmp, 0, par0.get(1).length);

            par0.get(1).ptext = tmp;
            par0.get(1).ptext[par0.get(1).length++] = 0x00;
            par0.get(1).ptext[par0.get(1).length++] = 0x00;
            par0.get(1).ptext[par0.get(1).length++] = 0x00;

            par0.remove(0);
        }
    }

    public void prepareAll() {
        int i, j, k;

        bookCRC = aFiles.getCRCForBook();

        softHyphenPresent = preference.useSoftHyphen && softHyphenCount > 100;

        if (ttl.size() > 0) {

            int m = 100;
            for (i = 0; i < ttl.size(); i++)
                if (ttl.get(i).iType < m)
                    m = ttl.get(i).iType;
            if (m != 0)
                for (i = 0; i < ttl.size(); i++)
                    ttl.get(i).iType -= m;

            AlOneContent content;
            StringBuilder sb = new StringBuilder();
            sb.setLength(0);
            for (i = 0; i < ttl.size(); i++) {
                content = ttl.get(i);

                sb.setLength(0);
                sb.append(content.name);

                boolean invisible = false;
                for (j = 0; j < sb.length(); j++) {
                    if (sb.charAt(j) == AlStyles.CHAR_IMAGE_S || sb.charAt(j) == AlStyles.CHAR_LINK_S || sb.charAt(j) == AlStyles.CHAR_ROWS_S)
                        invisible = true;

                    while (invisible && j < sb.length()) {
                        if (sb.charAt(j) == AlStyles.CHAR_IMAGE_E || sb.charAt(j) == AlStyles.CHAR_LINK_E || sb.charAt(j) == AlStyles.CHAR_ROWS_E)
                            invisible = false;
                        sb.deleteCharAt(j);
                    }
                }

                for (j = 0; j < sb.length(); j++) {
                    if ((sb.charAt(j) & AlStyles.STYLE_MASK_4CODECONVERT) == AlStyles.STYLE_BASE_4CODECONVERT
                        || sb.charAt(j) < 0x20) {
                        sb.setCharAt(j, (char) 0x00);
                    } else if (sb.charAt(j) == 0xa0) {
                        sb.setCharAt(j, (char) 0x20);
                    }
                }

                j = sb.length() - 1;
                while (j >= 0) {
                    if (sb.charAt(j) == 0x00 || sb.charAt(j) == 0xad) {
                        sb.deleteCharAt(j);
                    } else j--;
                }

                for (j = 0; j < sb.length(); j++) {
                    while (sb.charAt(j) == 0x20 && j + 1 < sb.length() && sb.charAt(j + 1) == 0x20)
                        sb.deleteCharAt(j);
                }

                while (sb.length() > 0)
                    if (sb.charAt(0) == 0x20) {
                        sb.deleteCharAt(0);
                    } else {
                        break;
                    }
                while (sb.length() > 0)
                    if (sb.charAt(sb.length() - 1) == 0x20) {
                        sb.deleteCharAt(sb.length() - 1);
                    } else {
                        break;
                    }

                content.name = sb.toString();
            }
        }

        prepareCustom();

        if (lnk.size() > 0) {
            boolean need_end;
            AlOneLink link0, link1;
            for (i = 0; i < lnk.size(); i++) {
                link0 = lnk.get(i);
                if (link0.iType == 1 && link0.positionE == -1) {
                    k = 0;
                    need_end = false;
                    link1 = null;
                    for (j = i + 1; j < lnk.size(); j++) {
                        link1 = lnk.get(j);
                        if (link1.positionS == link0.positionS) {
                            k++;
                            if (link1.positionE != -1) {
                                need_end = true;
                                break;
                            }
                            continue;
                        }
                        if (link1.iType == 1)
                            break;
                    }

                    if (link1 == null) {
                        lnk.get(i).positionE = size;
                    } else {
                        for (j = i; j <= i + k; j++) {
                            if (link1.iType == 1) {
                                lnk.get(i).positionE = need_end ? link1.positionE : link1.positionS;
                            } else {
                                lnk.get(i).positionE = size;
                            }
                        }
                    }
                }
            }
        }

        int ie = par0.size();
        long iv0 = 0, iv1;

        /*for (i = 0; i < ie; i++) {
            iv1 = par.get(i).iType & AlStyles.PAR_PARAGRAPH_MASK;
            if (iv1 == iv0 && iv1 != AlStyles.PAR_CITE)
                par.get(i).iType &= AlStyles.PAR_PREVIOUS_EMPTY_MASK;
            iv0 = iv1;
        }*/
    }

	/*public int getTextBuffer_Notes(int pos, char[] text, long[] style, AlProfileOptions profiles) {
		pos &= AlFiles.LEVEL1_FILE_BUF_MASK;
		int end = pos + getParagraphSlot(pos, text, style, profiles);		
		return end - pos;
	}*/


    public int getNoteBuffer(int pos, PairTextStyle textAndStyle, int shtamp, AlProfileOptions profiles) {
        return getAllBuffer(pos, textAndStyle, slotNote, shtamp, profiles);
    }

    public int getTextBuffer(int pos, PairTextStyle textAndStyle, int shtamp, AlProfileOptions profiles) {
        return getAllBuffer(pos, textAndStyle, slotText, shtamp, profiles);
    }

    private int getAllBuffer(int pos, PairTextStyle textAndStyle, AlSlotData slot, int shtamp, AlProfileOptions profiles) {
        pos &= AlFiles.LEVEL1_FILE_BUF_MASK;

        if (shtamp != slot.shtamp) {
            slot.end[0] = slot.end[1] = -1;
            slot.shtamp = shtamp;
        }

        if (slot.start[slot.active] == pos && slot.end[slot.active] > slot.start[slot.active]) {
            textAndStyle.txt = slot.txt[slot.active];
            textAndStyle.stl = slot.stl[slot.active];
            return slot.end[slot.active] - pos;
        }

        slot.active = 1 - slot.active;

        if (slot.start[slot.active] == pos && slot.end[slot.active] > slot.start[slot.active]) {
            textAndStyle.txt = slot.txt[slot.active];
            textAndStyle.stl = slot.stl[slot.active];
            return slot.end[slot.active] - pos;
        }

        slot.initBuffer();

        //Log.e("fill buffer " + Integer.toString(pos), Integer.toString(slot.active) + '_' + slot.toString());

        slot.start[slot.active] = pos;
        slot.end[slot.active] = pos + getParagraphSlot(pos, slot.txt[slot.active], slot.stl[slot.active], profiles);

        textAndStyle.txt = slot.txt[slot.active];
        textAndStyle.stl = slot.stl[slot.active];
        return slot.end[slot.active] - pos;
    }

    int findParagraphPositionBySourcePos(int start, int end, int pos) {
        int tmp = (end + start) >> 1;
        AlOneParagraph ap = par0.get(tmp);

        if (ap.positionS >= pos) {

            if (tmp == 0)
                return ap.start;

            if (par0.get(tmp - 1).positionS < pos) {
                if (par0.get(tmp - 1).positionE > pos)
                    return par0.get(tmp - 1).start;
                return ap.start;
            }

            if (tmp == end)
                return ap.start;

            return findParagraphPositionBySourcePos(start, tmp, pos);
        }

        if (tmp == start)
            return ap.start;

        return findParagraphPositionBySourcePos(tmp, end, pos);
    }

    private int findParagraphByPos01(int start, int end, int pos) {
        int tmp = (end + start) >> 1;
        AlOneParagraph ap = par0.get(tmp);
        if (ap.start > pos) {
            return findParagraphByPos01(start, tmp, pos);
        } else if (ap.start + ap.length <= pos) {
            return findParagraphByPos01(tmp, end, pos);
        }
        return tmp;
    }

    int findParagraphByPos(int pos) {
        if (pos >= size)
            pos = size - 1;
        if (pos < 0)
            pos = 0;
        return findParagraphByPos01(0, par0.size(), pos);
    }

    int isEmptyParagraph(char data[], int len) {
        boolean isInvisible = false;
        int cnt_obj = 0;
        char ch;
        for (int i = 0; i < len; i++) {
            ch = stored_par.data[i];
            if (ch < 0x20) {
                switch (ch) {
                    case AlStyles.CHAR_SOFTPAR:

                        break;
                    case AlStyles.CHAR_TITLEIMG_START:
                        if (isInlineImage()) {
                            isInvisible = true;
                        } else {

                        }
                        break;
                    case AlStyles.CHAR_TITLEIMG_STOP:
                        if (isInlineImage()) {
                            isInvisible = false;
                        } else {

                        }
                        break;
                    case AlStyles.CHAR_ROWS_S:
                    case AlStyles.CHAR_LINK_S:
                    case AlStyles.CHAR_IMAGE_S:
                        isInvisible = true;
                        break;
                    case AlStyles.CHAR_ROWS_E:
                    case AlStyles.CHAR_LINK_E:
                    case AlStyles.CHAR_IMAGE_E:
                        cnt_obj++;
                        isInvisible = false;
                        break;
                }
            } else
            if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {

            } else
            if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {

            } else
            if (isInvisible) {

            } else
		/*if (ch == AlStyles::CHAR_INVISIBLE_SPACE) {

		} else*/
                if (!AlUnicode.isSpaceSeparator(ch)) {
                return 1;
            }
        }

        return -cnt_obj;
    }

    private void getPreparedParagraph0(int paragraph_num, AlOneParagraph alp) {
        int len;

        if (alp == null)
            alp = par0.get(paragraph_num);
        len = alp.length;
        getParagraph(alp);

        if (alp.is_prepared)
            return;
        alp.is_prepared = true;

        int i, j;
        char ch;
        int start_style_point = -3;
        int cnt_obj = 0;
        boolean isInvisible = false;

        //не пустой ли параграф
        if ((cnt_obj = isEmptyParagraph(stored_par.data, len)) <= 0) {

            if (paragraph_num == 0) {
                if (par0.size() > 1)
                    par0.get(1).prop &= ~(AlParProperty.SL2_BREAK_BEFORE);
            } else {
                isInvisible = false;
                for (i = 0; i < len; i++) {
                    ch = stored_par.data[i];
                    if (ch < 0x20) {
                        switch (ch) {
                            case AlStyles.CHAR_SOFTPAR:

                                break;
                            case AlStyles.CHAR_TITLEIMG_START:
                                if (isInlineImage()) {
                                    isInvisible = true;
                                } else {

                                }
                                break;
                            case AlStyles.CHAR_TITLEIMG_STOP:
                                if (isInlineImage()) {
                                    isInvisible = false;
                                } else {

                                }
                                break;
                            case AlStyles.CHAR_ROWS_S:
                            case AlStyles.CHAR_LINK_S:
                            case AlStyles.CHAR_IMAGE_S:
                                isInvisible = true;
                                break;
                            case AlStyles.CHAR_ROWS_E:
                            case AlStyles.CHAR_LINK_E:
                            case AlStyles.CHAR_IMAGE_E:
                                isInvisible = false;
                                break;
                        }
                    } else
                    if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {

                    } else
                    if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {

                    } else
                    if (isInvisible) {

                    } else {
                        if (cnt_obj != 0) {
						/*if (ch == AlStyles::CHAR_INVISIBLE_SPACE) {
							stored_par.data[i] = 0x00;
						} else*/
                            if (ch == 0x0a) {
                                break;
                            } else
                            if (AlUnicode.isSpaceSeparator(ch)) {
                                stored_par.data[i] = 0x00;
                            } else {
                                break;
                            }
                        } else {
                            stored_par.data[i] = 0x00;
                        }
                    }
                }
            }
        }


        //замена символов
        for (i = 0; i < len; i++) {
            ch = stored_par.data[i];
            switch (ch) {
                //case 0xad:   if (!preference.useSoftHyphen)
                //	stored_par.data[i] = 0x00; break;
                case 0x2011:
                    stored_par.data[i] = 0x2d;
                    break;
                case 0x3000:
                    stored_par.data[i] = 0x20;
                    break;
            }
        }

        /*// преобразование code /code в pre /pre
        if ((alp.iType & (AlStyles.PAR_PRE | AlStyles.STYLE_CODE)) == AlStyles.STYLE_CODE) {
            start_style_point = 1;
            j = AlStyles.STYLE_CODE;
            for (i = 0; i < len; i++) {
                ch = stored_par.data[i];
                if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
                    if ((ch & AlStyles.STYLE_CODE) == 0) {
                        j = ch & AlStyles.STYLE_MASK;
                        continue;
                    }
                } else
                if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
                    continue;
                }

            if ((j & AlStyles.STYLE_CODE) == 0 && ch != 0x20 && ch != 0xa0) {
                    start_style_point = 0;
                    break;
                }
            }
            if (start_style_point == 1) {
                alp.iType |= AlStyles.PAR_PRE;
            }
        }*/
        /*// удаление неразрывных пробелов
        if (preference.delete0xA0 && (alp.iType & AlStyles.PAR_PRE) == 0) {
            j = (int) (alp.iType & AlStyles.STYLE_CODE);
            for (i = 0; i < len; i++) {
                ch = stored_par.data[i];
                if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
                    if ((ch & AlStyles.STYLE_CODE) == 0) {
                        j = ch & AlStyles.STYLE_CODE;
                        continue;
                    }
                }
                if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
                    continue;
                }
                if (ch == 0xa0 && j == 0 && i < len - 1 && !AlUnicode.isDashPunctuation(stored_par.data[i + 1])) {
                    stored_par.data[i] = 0x20;
                }
            }
        }*/


        // преобразование ссылок в SUP, если возможно
        start_style_point = -3;
        if (preference.notesAsSUP) {
            for (i = 0; i < len; i++) {
                ch = stored_par.data[i];

                if (ch == AlStyles.CHAR_LINK_S && i > 0 && !AlUnicode.isNotCharForSUP(stored_par.data[i - 1])/*stored_par.data[i - 1] != 0x20*/) {
                    start_style_point = -2;
                }

                if (start_style_point == -3)
                    continue;

                if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
                    continue;
                } else
                if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {

                    if ((ch & (AlStyles.STYLE_SUP | AlStyles.STYLE_SUB)) != 0) {
                        start_style_point = -3;
                        continue;
                    }

                    if ((ch & AlStyles.SL_MASKFORLINK) != AlStyles.STYLE_LINK) {
                        if (start_style_point > 0) {
                            stored_par.data[start_style_point] |= AlStyles.STYLE_SUP;
                            stored_par.data[i] &= ~AlStyles.STYLE_SUP;
                        }
                    } else
                    if (start_style_point == -2 && (ch & AlStyles.SL_MASKFORLINK) == AlStyles.STYLE_LINK) {
                        start_style_point = i;
                    }
                } else
                if (start_style_point > 0) {
                    switch (ch) {
                        case 0x00: case '[': case ']': case '0': case '1': case '2': case '{': case '}': case '(': case ')':
                        case '3' : case '4': case '5': case '6': case '7': case '8': case '9': case '*':
                            break;
                        default:
                            start_style_point = -3;
                            break;
                    }
                }
            }
        }


        /*if ((alp.iType & AlStyles.PAR_PRE) != 0 && len > 1) {
            for (i = 0; i < len - 1; i++) {
                ch = stored_par.data[i];
                if (ch != 0x20) {
                    i++;
                    continue;
                }

                for (j = i + 1; j < len; j++) {
                    if (stored_par.data[j] < 0x20
                            || ((stored_par.data[j] & AlStyles.STYLE_MASK_4CODECONVERT) == AlStyles.STYLE_BASE_4CODECONVERT))
                        continue;
                    if (stored_par.data[j] == 0x20) {
                        stored_par.data[i] = 0xa0;
                        break;
                    }
                }
            }

            alp.iType &= ~(AlStyles.PAR_NATIVEJUST | AlStyles.SL_JUST_MASK);
            alp.iType |= AlStyles.PAR_NATIVEJUST | AlStyles.SL_JUST_LEFT;

            return;
        } else {
            boolean needReturn = false;
            j = (int) (alp.iType & AlStyles.STYLE_CODE);
            boolean needJustLeft = j != 0;
            for (i = 0; i < len; i++) {
                ch = stored_par.data[i];
                if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0
                        || (ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
                    j = (int) (ch & AlStyles.STYLE_CODE);
                    needJustLeft |= j != 0;
                    continue;
                }

                if (ch == 0x20 && j != 0 && i > 0 && stored_par.data[i - 1] == 0x20) {
                    stored_par.data[i] = 0xa0;
                    needReturn = true;
                }
            }
            if (needJustLeft) {
                alp.iType &= ~(AlStyles.PAR_NATIVEJUST | AlStyles.SL_JUST_MASK);
                alp.iType |= AlStyles.PAR_NATIVEJUST | AlStyles.SL_JUST_LEFT;
            }
            if (needReturn) {
                return;
            }
        }*/

        if (preference.need_dialog == 2)
            return;
        if (((alp.paragraph & (AlStyles.SL_PRESERVE_SPACE)) != 0) || ((alp.prop & (AlParProperty.SL2_JUSTIFY_POEM)) != 0))
            return;

        boolean disable_linear = false;
        // выравнивание диалогов
        for (i = 0; i < len; i++) {
            ch = stored_par.data[i];
            if (Character.getType(ch) == Character.SPACE_SEPARATOR) {
                if (disable_linear)
                    return;
                stored_par.data[i] = AlStyles.CHAR_NONE;
            } else
            if (ch < 0x20
                    || ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0)) {
                //disable_linear = (ch & (AlStyles::PAR_PRE | AlStyles::STYLE_CODE)) != 0;
            } else
            if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
                //disable_linear = (ch & (AlStyles::PAR_PRE | AlStyles::STYLE_CODE)) != 0;
            } else
            if (ch == 0x2022) {
                i++;
                break;
            } else
            if (Character.getType(ch) == Character.DASH_PUNCTUATION) {
                if (preference.need_dialog == 0)
                    stored_par.data[i] = 8212;
                i++;
                break;
            } else {
                return;
            }
        }
        for (; i < len; i++) {
            ch = stored_par.data[i];
            if (Character.getType(ch) == Character.SPACE_SEPARATOR) {
                stored_par.data[i] = 0xa0;
                i++;
                break;
            } else if (ch < 0x20 || ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0)) {

            } else
            if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {

            } else {
                return;
            }
        }
        for (; i < len; i++) {
            ch = stored_par.data[i];
            if (Character.getType(ch) == Character.SPACE_SEPARATOR) {
                stored_par.data[i] = AlStyles.CHAR_NONE;
            } else if (ch < 0x20 || ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0)) {

            } else
            if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {

            } else {
                return;
            }
        }
    }

    private void getParagraph(AlOneParagraph ap) {
        stored_par.data = ap.ptext;
        stored_par.size = stored_par.length = ap.length;
    }

    private int getParagraphSlot(int pos, char[] slot_t, long[] slot_s, AlProfileOptions profiles) {
        boolean isInvisible = false;
        char ch;

        int profileType = profiles.showFirstLetter;//ProfileManager.isMarkFirstLetter() & 0x03;
        boolean extfl_pstart = true;//(PrefManager.getInt(R.string.keyoptuser_custom) & (1 << 0)) != 0;
        boolean extfl_pend = true;//extfl_pstart && (PrefManager.getInt(R.string.keyoptuser_custom) & (1 << 1)) != 0;
        boolean extfl_dialog = true;//(PrefManager.getInt(R.string.keyoptuser_custom) & (1 << 2)) != 0;
        long extfl_mask = 0x03;//(PrefManager.getInt(R.string.keyoptuser_custom) & (1 << 4)) != 0 ? 0x03 : 0x00;

        int i, j, k;
        AlOneParagraph ap;
        int par_num = findParagraphByPos(pos);
        ap = par0.get(par_num);
        getPreparedParagraph0(par_num, ap);

        long style_par = getParagraphRealStyle(ap.paragraph);
        char style_image = 0;
        style_par |= AlStyles.SL_PAR | ((long)par_num << AlStyles.SL3_NUMBER_SHIFT);
        long style_par_image_title = 0;

        int fletter_cnt = 0;

        j = pos - ap.start;
        for (i = 0; i < j; i++) {
            ch = stored_par.data[i];

            if (ch < 0x20) {
                if (fletter_cnt > 0) {
                    fletter_cnt--;
                }

                switch (ch) {
                    case AlStyles.CHAR_SOFTPAR:
                        style_par |= AlStyles.SL_PAR;
                        break;
                    case AlStyles.CHAR_TITLEIMG_START:
                        if (isInlineImage()) {
                            isInvisible = true;
                        } else {
                            style_par_image_title = style_par;
                            //style_par = PrefManager.getStyle(PrefManager.STYLE_FOOTNOTES);
                            //style_par = styles.style[InternalConst.STYLES_STYLE_AUTHOR];
                            //style_par &= ~(AlStyles.SL_JUST_MASK | AlStyles.SL_MARGL_MASK | AlStyles.SL_MARGR_MASK);
                            //style_par |= AlStyles.SL_JUST_CENTER | AlStyles.SL_MARGL1 | AlStyles.SL_MARGR1;
                            style_par |= AlStyles.SL_PAR;
                            //ap.iType |= AlStyles.PAR_SKIPFIRSTLet;
                        }
                        break;
                    case AlStyles.CHAR_TITLEIMG_STOP:
                        if (isInlineImage()) {
                            isInvisible = false;
                        } else {
                            style_par = style_par_image_title;
                            style_par_image_title = 0;
                            style_par |= AlStyles.SL_PAR;
                            //ap.iType &= ~AlStyles.PAR_SKIPFIRSTLet;
                        }
                        break;
                    case AlStyles.CHAR_ROWS_S:
                    case AlStyles.CHAR_LINK_S:
                    case AlStyles.CHAR_IMAGE_S:
                        isInvisible = true;
                        break;
                    case AlStyles.CHAR_ROWS_E:
                    case AlStyles.CHAR_LINK_E:
                    case AlStyles.CHAR_IMAGE_E:
                        isInvisible = false;
                        break;
                }
            } else
            if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
                style_par &= AlStyles.STYLE_ICHARMASK;
                style_par |= ch & AlStyles.STYLE_MASK;
            } else
            if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
                if ((ch & AlStyles.STYLE_BASE_SETTEXTCOLOR) == 0) {
                    style_par &= AlStyles.SL_SIZE_IMASK;
                    if ((style_par & AlStyles.SL_TABLE) == 0) {
                        style_par |= (ch & (AlStyles.SL_SIZE_MASK >> AlStyles.SL_SIZE_SHIFT)) << AlStyles.SL_SIZE_SHIFT;
                    } else {
                        style_par |= ((ch & (AlStyles.SL_SIZE_MASK >> AlStyles.SL_SIZE_SHIFT)) - 20) << AlStyles.SL_SIZE_SHIFT;
                    }
                } else {
                    style_par &= AlStyles.SL_COLOR_IMASK;
                    style_par |= (ch & (AlStyles.SL_COLOR_MASK >> AlStyles.SL_COLOR_SHIFT)) << AlStyles.SL_COLOR_SHIFT;
                }
            } else
                if (((style_par & AlStyles.SL_PAR) != 0) && (!isInvisible) &&
                        ((ch & AlStyles.STYLE_BASE_MASK) != AlStyles.STYLE_BASE0) &&
                        ((ch & AlStyles.STYLE_BASE_MASK) != AlStyles.STYLE_BASE1) &&
                        ((ch > 0x20) || (ch == AlStyles.CHAR_IMAGE_E) || (ch == AlStyles.CHAR_ROWS_E))
                        ) {

                    if ((ap.paragraph & (AlStyles.MASK_FOR_FLETTER - AlStyles.SL_FIRSTP - extfl_mask)) == 0 &&
                            (style_par & (AlStyles.STYLE_MASK - extfl_mask)) == 0) {

                        switch (profileType) {
                            case 0x02:
                                if ((ap.paragraph & (AlStyles.MASK_FOR_FLETTER - extfl_mask)) != AlStyles.SL_FIRSTP)
                                    break;
                            case 0x03:
                            case 0x01:
                                if (Character.isUpperCase(ch) &&
                                        (i == stored_par.cpos - 1 || !Character.isUpperCase(stored_par.data[i + 1]))) {
                                    if (extfl_pend)
                                        fletter_cnt = isValidFLet(InternalConst.FLET_MODE_LETTER, i + 1, extfl_pend, extfl_mask != 0);
                                } else if (extfl_pstart && AlUnicode.isDigit(ch)) {
                                    fletter_cnt = isValidFLet(InternalConst.FLET_MODE_LETTER, i + 1, extfl_pend, extfl_mask != 0);
                                } else if (extfl_pstart && AlUnicode.isCSSFirstLetter(ch)) {
                                    fletter_cnt = isValidFLet(InternalConst.FLET_MODE_START, i + 1, extfl_pend, extfl_mask != 0);
                                } else if (extfl_dialog && AlUnicode.isDashPunctuation(ch)) {
                                    fletter_cnt = isValidFLet(InternalConst.FLET_MODE_DIALOG, i + 1, extfl_pend, extfl_mask != 0);
                                }
                                break;
                        }
                    }

                    style_par &= ~((ch == AlStyles.CHAR_ROWS_E) ? 0 : AlStyles.SL_PAR);
                } else if (fletter_cnt > 0) {
                    fletter_cnt--;
                }
        }
        j = 0;
        boolean is_link;
        while (true) {
            for (; i < stored_par.length; i++) {
                ch = stored_par.data[i];
                style_image = 0;
                if (ch < 0x20) {
                    slot_t[j] = 0x00;
                    switch (ch) {
                        case AlStyles.CHAR_SOFTPAR:
                            style_par |= AlStyles.SL_PAR;
                            break;
                        case AlStyles.CHAR_TITLEIMG_START:
                            if (isInlineImage()) {
                                isInvisible = true;
                            } else {
                                style_par_image_title = style_par;
                                //style_par = PrefManager.getStyle(PrefManager.STYLE_FOOTNOTES);
                                //style_par = styles.style[InternalConst.STYLES_STYLE_AUTHOR];
                                //style_par &= ~(AlStyles.SL_JUST_MASK | AlStyles.SL_MARGL_MASK | AlStyles.SL_MARGR_MASK);
                                //style_par |= AlStyles.SL_JUST_CENTER | AlStyles.SL_MARGL1 | AlStyles.SL_MARGR1;
                                style_par |= AlStyles.SL_PAR;
                                //ap.iType |= AlStyles.PAR_SKIPFIRSTLet;
                            }
                            break;
                        case AlStyles.CHAR_TITLEIMG_STOP:
                            if (isInlineImage()) {
                                isInvisible = false;
                            } else {
                                style_par = style_par_image_title;
                                style_par_image_title = 0;
                                style_par |= AlStyles.SL_PAR;
                                //ap.iType &= ~AlStyles.PAR_SKIPFIRSTLet;
                            }
                            break;
                        case AlStyles.CHAR_ROWS_S:
                        case AlStyles.CHAR_LINK_S:
                        case AlStyles.CHAR_IMAGE_S:
                            isInvisible = true;
                            break;
                        case AlStyles.CHAR_LINK_E:
                            isInvisible = false;
                            break;
                        case AlStyles.CHAR_ROWS_E:
                        case AlStyles.CHAR_IMAGE_E:
                            isInvisible = false;
                            slot_t[j] = ch;
                            style_image = (char) AlStyles.SL_IMAGE;
                            break;
                        case AlStyles.CHAR_COVER:
                            slot_t[j] = ch;
                            break;
                    }
                } else
                if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
                    slot_t[j] = 0x00;
                    if ((ch & AlStyles.STYLE_BASE_SETTEXTCOLOR) == 0) {
                        style_par &= AlStyles.SL_SIZE_IMASK;
                        if ((style_par & AlStyles.SL_TABLE) == 0) {
                            style_par |= (ch & (AlStyles.SL_SIZE_MASK >> AlStyles.SL_SIZE_SHIFT)) << AlStyles.SL_SIZE_SHIFT;
                        } else {
                            style_par |= ((ch & (AlStyles.SL_SIZE_MASK >> AlStyles.SL_SIZE_SHIFT)) - 20) << AlStyles.SL_SIZE_SHIFT;
                        }
                    } else {
                        style_par &= AlStyles.SL_COLOR_IMASK;
                        style_par |= (ch & (AlStyles.SL_COLOR_MASK >> AlStyles.SL_COLOR_SHIFT)) << AlStyles.SL_COLOR_SHIFT;
                    }
                } else
                if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
                    // mark last link char if present
                    if ((style_par & AlStyles.STYLE_LINK) != 0 && (ch & AlStyles.STYLE_LINK) == 0) {
                        for (k = j - 1; k >= 0; k--) {
                            if (slot_t[k] > 0x20 || slot_t[k] == AlStyles.CHAR_IMAGE_E) {
                                slot_s[k] |= AlStyles.SL_MARKNOTE;
                                break;
                            }
                        }
                    }
                    //
                    style_par &= AlStyles.STYLE_ICHARMASK;
                    style_par |= ch & AlStyles.STYLE_MASK;
                    slot_t[j] = 0x00;
                } else {
                    slot_t[j] = isInvisible ? 0x00 : ch;
                }

                slot_s[j] = style_par + style_image;

                is_link = (style_par & AlStyles.STYLE_LINK) != 0;
                if (is_link) {
                    slot_s[j] &= AlStyles.SL_COLOR_IMASK;
                    slot_s[j] |= AlStyles.SL_COLOR_LINK;
                }

                if (fletter_cnt > 0) {
                    slot_s[j] |= AlStyles.SL_MARKFIRTSTLETTER0;
                    slot_s[j] &= AlStyles.SL_MASKSTYLESOVER;
                    if ((ch == 0xa0 || ch == 0x20) && profiles.classicFirstLetter &&
                            (preference.need_dialog) != 2) {
                        slot_t[j] = 0x00;
                    } else if (ch == 0x20 && profiles.classicFirstLetter)
                        slot_t[j] = 0xa0;
                    fletter_cnt--;
                }

                if (((style_par & AlStyles.SL_PAR) != 0) &&
                        (!isInvisible) && (slot_t[j] != 0x00) &&
                        ((ch > 0x20) || (ch == AlStyles.CHAR_IMAGE_E) || (ch == AlStyles.CHAR_ROWS_E))) {

                    if ((ap.paragraph & (AlStyles.MASK_FOR_FLETTER - AlStyles.SL_FIRSTP - extfl_mask)) == 0 &&
                            (style_par & (AlStyles.STYLE_MASK - extfl_mask)) == 0) {

                        switch (profileType) {
                            case 0x02:
                                if ((ap.paragraph & (AlStyles.MASK_FOR_FLETTER - extfl_mask)) != AlStyles.SL_FIRSTP)
                                    break;
                            case 0x03:
                            case 0x01:
                                if (AlUnicode.isUpperCase(ch) &&
                                        (i == stored_par.cpos - 1 || !AlUnicode.isUpperCase(stored_par.data[i + 1]))) {
                                    slot_s[j] |= AlStyles.SL_MARKFIRTSTLETTER0;
                                    slot_s[j] &= AlStyles.SL_MASKSTYLESOVER;
                                    if (extfl_pend)
                                        fletter_cnt = isValidFLet(InternalConst.FLET_MODE_LETTER, i + 1, extfl_pend, extfl_mask != 0);
                                } else if (extfl_pstart && AlUnicode.isDigit(ch)) {
                                    slot_s[j] |= AlStyles.SL_MARKFIRTSTLETTER0;
                                    fletter_cnt = isValidFLet(InternalConst.FLET_MODE_LETTER, i + 1, extfl_pend, extfl_mask != 0);
                                } else if (extfl_pstart && AlUnicode.isCSSFirstLetter(ch)) {
                                    if ((fletter_cnt = isValidFLet(InternalConst.FLET_MODE_START, i + 1, extfl_pend, extfl_mask != 0)) > 0)
                                        slot_s[j] |= AlStyles.SL_MARKFIRTSTLETTER0;
                                } else if (extfl_dialog && AlUnicode.isDashPunctuation(ch)) {
                                    if ((fletter_cnt = isValidFLet(InternalConst.FLET_MODE_DIALOG, i + 1, extfl_pend, extfl_mask != 0)) > 0)
                                        slot_s[j] |= AlStyles.SL_MARKFIRTSTLETTER0;
                                }
                                break;
                        }
                    }

                    style_par &= (~((ch == AlStyles.CHAR_ROWS_E) ? 0 : AlStyles.SL_PAR));
                }

                /*if (((slot_s[j] & AlStyles.SL_MARKFIRTSTLETTER) == 0) &&
                        (style & (AlStyles.STYLE_BOLD | AlStyles.STYLE_ITALIC | AlStyles.STYLE_CODE*//* | AlStyles.SL_CSTYLE*//*)) != 0) {
                    //remap_font = slot_s[j];
                    remap_color = slot_s[j];

                    *//*if ((style & AlStyles.SL_CSTYLE) != 0) {
                        switch (((int) style_s) & AlStyles.REMAP_MASKF) {
                            case AlStyles.REMAP_TEXTF:
                                if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0)
                                    remap_font = style_s | AlStyles.SL_REMAPFONT;
                                break;
                            case AlStyles.REMAP_FONTF:
                                if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT)
                                    remap_font = style_s;
                                break;
                            case AlStyles.REMAP_ALLF:
                                remap_font = style_s;
                                break;
                        }

                        if (!is_link)
                            switch (((int) style_s) & AlStyles.REMAP_MASKC) {
                                case AlStyles.REMAP_TEXTC:
                                    if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0)
                                        remap_color = style_s;
                                    break;
                                case AlStyles.REMAP_FONTC:
                                    if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT)
                                        remap_color = style_s;
                                    break;
                                case AlStyles.REMAP_ALLC:
                                    remap_color = style_s;
                                    break;
                            }
                    }*//*


                    if ((style & 0x03) == AlStyles.STYLE_BOLD) {
                        *//*switch (((int) style_b) & AlStyles.REMAP_MASKF) {
                            case AlStyles.REMAP_TEXTF:
                                if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0)
                                    remap_font = style_b;
                                break;
                            case AlStyles.REMAP_FONTF:
                                if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT)
                                    remap_font = style_b;
                                break;
                            case AlStyles.REMAP_ALLF:
                                remap_font = style_b;
                                break;
                        }*//*

                        if (!is_link)
                            switch (((int) style_b) & AlStyles.REMAP_MASKC) {
                                case AlStyles.REMAP_TEXTC:
                                    if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0)
                                        remap_color = style_b;
                                    break;
                                case AlStyles.REMAP_FONTC:
                                    if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT)
                                        remap_color = style_b;
                                    break;
                                case AlStyles.REMAP_ALLC:
                                    remap_color = style_b;
                                    break;
                            }
                    }


                    if ((style & 0x03) == AlStyles.STYLE_ITALIC) {
                        *//*switch (((int) style_i) & AlStyles.REMAP_MASKF) {
                            case AlStyles.REMAP_TEXTF:
                                if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0)
                                    remap_font = style_i;
                                break;
                            case AlStyles.REMAP_FONTF:
                                if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT)
                                    remap_font = style_i;
                                break;
                            case AlStyles.REMAP_ALLF:
                                remap_font = style_i;
                                break;
                        }*//*

                        if (!is_link)
                            switch (((int) style_i) & AlStyles.REMAP_MASKC) {
                                case AlStyles.REMAP_TEXTC:
                                    if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0)
                                        remap_color = style_i;
                                    break;
                                case AlStyles.REMAP_FONTC:
                                    if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT)
                                        remap_color = style_i;
                                    break;
                                case AlStyles.REMAP_ALLC:
                                    remap_color = style_i;
                                    break;
                            }
                    }

                    if ((style & 0x03) == AlStyles.STYLE_ITALIC + AlStyles.STYLE_BOLD) {
                        *//*switch (((int) style_bi) & AlStyles.REMAP_MASKF) {
                            case AlStyles.REMAP_TEXTF:
                                if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0)
                                    remap_font = style_bi;
                                break;
                            case AlStyles.REMAP_FONTF:
                                if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT)
                                    remap_font = style_bi;
                                break;
                            case AlStyles.REMAP_ALLF:
                                remap_font = style_bi;
                                break;
                        }*//*

                        if (!is_link)
                            switch (((int) style_bi) & AlStyles.REMAP_MASKC) {
                                case AlStyles.REMAP_TEXTC:
                                    if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0)
                                        remap_color = style_bi;
                                    break;
                                case AlStyles.REMAP_FONTC:
                                    if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT)
                                        remap_color = style_bi;
                                    break;
                                case AlStyles.REMAP_ALLC:
                                    remap_color = style_bi;
                                    break;
                            }
                    }

                    if ((style & AlStyles.STYLE_CODE) != 0) {
                        *//*switch (((int) style_c) & AlStyles.REMAP_MASKF) {
                            case AlStyles.REMAP_TEXTF:
                                if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0)
                                    remap_font = style_c;
                                break;
                            case AlStyles.REMAP_FONTF:
                                if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT)
                                    remap_font = style_c;
                                break;
                            case AlStyles.REMAP_ALLF:
                                remap_font = style_c;
                                break;
                        }*//*

                        if (!is_link)
                            switch (((int) style_c) & AlStyles.REMAP_MASKC) {
                                case AlStyles.REMAP_TEXTC:
                                    if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0)
                                        remap_color = style_c;
                                    break;
                                case AlStyles.REMAP_FONTC:
                                    if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT)
                                        remap_color = style_c;
                                    break;
                                case AlStyles.REMAP_ALLC:
                                    remap_color = style_c;
                                    break;
                            }
                    }

                    slot_s[j] &= *//*AlStyles.SL_FONT_IMASK & *//*AlStyles.SL_COLOR_IMASK;// & AlStyles.SL_SIZE_IMASK;
                    slot_s[j] |= //(remap_font & (AlStyles.SL_FONT_MASK | AlStyles.SL_REMAPFONT*//* AlStyles.SL_SIZE_MASK*//*)) |
                            (remap_color & AlStyles.SL_COLOR_MASK);
                }*/

                j++;
                if (j == AlFiles.LEVEL1_FILE_BUF_SIZE)
                    return j;
            }

            fletter_cnt = 0;

            par_num++;
            if (par_num == par0.size()) {
                return j;
            }
            ap = par0.get(par_num);
            getPreparedParagraph0(par_num, ap);

            style_par = getParagraphRealStyle(ap.paragraph);
            style_par |= AlStyles.SL_PAR | ((long)par_num << AlStyles.SL3_NUMBER_SHIFT);


            i = 0;
        }
    }

    private int isValidFLet(int flagLetter, int start, boolean punctation_end, boolean bi_mask) {
        int res = 0, res2 = 0;
        switch (flagLetter) {
            case InternalConst.FLET_MODE_LETTER:
                for (; start < stored_par.length; start++) {
                    if (bi_mask &&
                            ((stored_par.data[start] & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0
                             || (stored_par.data[start] & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) &&
                            (stored_par.data[start] & (AlStyles.STYLE_MASK - 0x03)) == 0) {
                        res++;
                        if (start == stored_par.length - 1)
                            return 0;
                        continue;
                    } else if (stored_par.data[start] == 0x00) {
                        res++;
                        if (start == stored_par.length - 1)
                            return 0;
                        continue;
                    } else if (AlUnicode.isCSSFirstLetter(stored_par.data[start])) {
                        return res + 1;
                    } else
                        return 0;
                }
                break;
            case InternalConst.FLET_MODE_DIALOG:
            case InternalConst.FLET_MODE_START:
                for (; start < stored_par.length; start++) {
                    if (bi_mask &&
                            ((stored_par.data[start] & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0
                                    || (stored_par.data[start] & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) &&
                            (stored_par.data[start] & (AlStyles.STYLE_MASK - 0x03)) == 0) {
                        res++;
                        if (start == stored_par.length - 1)
                            return 0;
                        continue;
                    } else if (stored_par.data[start] == 0x00) {
                        res++;
                        if (start == stored_par.length - 1)
                            return 0;
                        continue;
                    } else if (stored_par.data[start] == 0xa0 || stored_par.data[start] == 0x20) {
                        res++;
                        if (start == stored_par.length - 1)
                            return 0;
                        continue;
                    } else if (Character.isUpperCase(stored_par.data[start])) {
                        res++;
                        if (!punctation_end)
                            return res;
                        start++;
                        break;
                    } else
                        return 0;
                }

                for (; start < stored_par.length; start++) {
                    if (bi_mask &&
                            ((stored_par.data[start] & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0
                                    || (stored_par.data[start] & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) &&
                            (stored_par.data[start] & (AlStyles.STYLE_MASK - 0x03)) == 0) {
                        res2++;
                        if (start == stored_par.length - 1)
                            return res;
                        continue;
                    } else if (stored_par.data[start] == 0x00) {
                        res2++;
                        if (start == stored_par.length - 1)
                            return res;
                        continue;
                    } else if (AlUnicode.isCSSFirstLetter(stored_par.data[start])) {
                        return res + res2 + 1;
                    } else
                        break;
                }

                return res;
        }
        return 0;
    }

    private long getParagraphRealStyle(long s) {
        long res = s;

        if (((s & AlStyles.STYLE_CODE) != 0))
            res |= AlStyles.SL_FONT_CODE;

        if (((s & AlStyles.SL_TABLE) != 0)) {
            res = s & AlStyles.SL_FONT_MASK;
            res |= AlStyles.SL_TABLE;
            res |= (((s & AlStyles.SL_SIZE_MASK) >> AlStyles.SL_SIZE_SHIFT) - 20) << AlStyles.SL_SIZE_SHIFT;
        }

        return res;
    }

    private boolean isInlineImage() {
        int i;
        boolean isInvisible = false;
        char ch;
        for (i = 0; i < stored_par.length; i++) {
            ch = stored_par.data[i];
            if (ch < 0x20) {
                switch (ch) {
                    case AlStyles.CHAR_TITLEIMG_START:
                        isInvisible = true;
                        break;
                    case AlStyles.CHAR_TITLEIMG_STOP:
                        isInvisible = false;
                        break;
                    case AlStyles.CHAR_ROWS_S:
                    case AlStyles.CHAR_LINK_S:
                    case AlStyles.CHAR_IMAGE_S:
                        isInvisible = true;
                        break;
                    case AlStyles.CHAR_ROWS_E:
                    case AlStyles.CHAR_LINK_E:
                    case AlStyles.CHAR_IMAGE_E:
                        isInvisible = false;
                        break;
                }
            } else
            if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {

            } else
            if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {

            } else {
                if (isInvisible)
                    continue;
                if (ch > 0x20)
                    return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "\r\n" + ident + " " + size + " symbols " + par0.size() +
                " paragraph " + " cp:" + Integer.toString(use_cpR0) + "\r\n";
    }

    public TAL_NOTIFY_RESULT findText(String find) {
        TAL_NOTIFY_RESULT res = TAL_NOTIFY_RESULT.ERROR;

        resfind.clear();
        if (find == null)
            return TAL_NOTIFY_RESULT.OK;

        if (size < 1)
            return TAL_NOTIFY_RESULT.OK;

        StringBuilder sFind = new StringBuilder(find);
        if (sFind.length() >= InternalConst.FIND_LEN)
            return res;

        int i, correct = 0;
        for (i = 0; i < sFind.length(); i++) {
            char ch = sFind.charAt(i);

            if (ch == '?') {
                sFind.setCharAt(i, (char) AlStyles.CHAR_ANYCHAR);
            } else if (AlUnicode.isLetter(ch)) {
                sFind.setCharAt(i, Character.toLowerCase(ch));
                correct++;
            } else if (AlUnicode.isDigit(ch)) {
                correct++;
            } else if (AlUnicode.isPunctuation(ch)) {
                sFind.setCharAt(i, '.');
                correct++;
            } else {
                sFind.setCharAt(i, ' ');
            }
        }

        if (correct < 1)
            return res;


        boolean isInvisible = false;
        int fLen = sFind.length() - 1;
        char ch, lastChar = sFind.charAt(fLen);

        char[] stackChar = new char[InternalConst.FIND_LEN];
        int[] stackPos = new int[InternalConst.FIND_LEN];
        int fPos = 0, pos = 0;

        int j;
        AlOneParagraph ap;
        int par_num = findParagraphByPos(pos);
        ap = par0.get(par_num);
        getParagraph(ap);
        j = pos - ap.start;
        for (i = 0; i < j; i++) {
            if (stored_par.data[i] < 0x20) {
                switch (stored_par.data[i]) {
                    case AlStyles.CHAR_ROWS_S:
                    case AlStyles.CHAR_LINK_S:
                    case AlStyles.CHAR_IMAGE_S:
                        isInvisible = true;
                        break;
                    case AlStyles.CHAR_ROWS_E:
                    case AlStyles.CHAR_LINK_E:
                    case AlStyles.CHAR_IMAGE_E:
                        isInvisible = false;
                        break;
                }
            }
        }

        while (true) {
            if (i == 0) {
                if (Character.getType(stackChar[(fPos - 1) & InternalConst.FIND_MASK]) != Character.SPACE_SEPARATOR) {
                    stackChar[fPos & InternalConst.FIND_MASK] = ' ';
                    stackPos[fPos & InternalConst.FIND_MASK] = ap.start + i;
                    fPos++;
                }
            }
            for (; i < stored_par.length; i++) {
                ch = stored_par.data[i];

                if (ch < 0x20) {
                    switch (ch) {
                        case AlStyles.CHAR_TITLEIMG_START:
                        case AlStyles.CHAR_ROWS_S:
                        case AlStyles.CHAR_LINK_S:
                        case AlStyles.CHAR_IMAGE_S:
                            isInvisible = true;
                            continue;
                        case AlStyles.CHAR_TITLEIMG_STOP:
                        case AlStyles.CHAR_ROWS_E:
                        case AlStyles.CHAR_LINK_E:
                        case AlStyles.CHAR_IMAGE_E:
                            isInvisible = false;
                            continue;
                        case AlStyles.CHAR_COVER:
                            ch = ' ';
                            break;
                        default:
                            continue;
                    }
                } else if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
                    continue;
                } else
                if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
                   	continue;
                } else {
                    if (isInvisible)
                        continue;
                }

                if (AlUnicode.isLetterOrDigit(ch)) {
                    ch = Character.toLowerCase(ch);
                } else if (AlUnicode.isPunctuation(ch)) {
                    ch = '.';
                } else {
                    if (Character.getType(ch) != Character.SPACE_SEPARATOR) {
                        continue;
                    } else if (Character.getType(stackChar[(fPos - 1) & InternalConst.FIND_MASK]) != Character.SPACE_SEPARATOR) {
                        ch = ' ';
                    } else {
                        continue;
                    }
                }

                //
                stackChar[fPos & InternalConst.FIND_MASK] = ch;
                stackPos[fPos & InternalConst.FIND_MASK] = ap.start + i;

                if (ch == lastChar) {
                    for (j = 0; j <= fLen; j++) {
                        if (stackChar[(fPos - j) & InternalConst.FIND_MASK] != sFind.charAt(fLen - j) && sFind.charAt(fLen - j) != AlStyles.CHAR_ANYCHAR) {
                            break;
                        } else if (j == fLen) {
                            AlOneSearchResult a = new AlOneSearchResult();
                            a.pos_start = stackPos[(fPos - j) & InternalConst.FIND_MASK];
                            a.pos_end = stackPos[fPos & InternalConst.FIND_MASK];

                            if (a.pos_start >= ap.start) {
                                getFindContext(a, ap.start, par_num, par_num);
                            } else {
                                getFindContext(a, 0, findParagraphByPos(a.pos_start), par_num);
                            }

                            resfind.add(a);
                            res = TAL_NOTIFY_RESULT.OK;
                        }
                    }
                }
                fPos++;
            }

            par_num++;
            if (par_num == par0.size())
                break;
            ap = par0.get(par_num);
            getParagraph(ap);
            i = 0;
        }

        return res;
    }

     private static final int LEVEL2_FIND_CONTEXT_OFFSET = 35;
     private void getFindContext(AlOneSearchResult find, int start, int par_start, int par_stop) {

        int par_num = par_start;

        AlOneParagraph ap;
        boolean isInvisible = false;
        int apstart;
        char ch;

        int state = 0, ws = -1, we = -1;
        StringBuilder text = new StringBuilder();

        while (par_num <= par_stop) {

            if (par_start == par_stop) {
                apstart = start;
                par_num++;
            } else {
                ap = par0.get(par_num++);
                getParagraph(ap);
                apstart = ap.start;
            }

            for (int i = 0; i < stored_par.length; i++) {
                ch = stored_par.data[i];

                if (ch < 0x20) {
                    switch (ch) {
                        case AlStyles.CHAR_TITLEIMG_START:
                        case AlStyles.CHAR_ROWS_S:
                        case AlStyles.CHAR_LINK_S:
                        case AlStyles.CHAR_IMAGE_S:
                            isInvisible = true;
                            continue;
                        case AlStyles.CHAR_TITLEIMG_STOP:
                        case AlStyles.CHAR_ROWS_E:
                        case AlStyles.CHAR_LINK_E:
                        case AlStyles.CHAR_IMAGE_E:
                            isInvisible = false;
                            continue;
                        case AlStyles.CHAR_COVER:
                            ch = ' ';
                            break;
                        default:
                            continue;
                    }
                } else
                if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
                    continue;
                } else
                if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
                        continue;
                } else {
                    if (isInvisible)
                        continue;
                }

                if (state == 0 && apstart + i > find.pos_start - LEVEL2_FIND_CONTEXT_OFFSET) {
                    if (ch == 0x20 || i == 0 || apstart + i > find.pos_start - (LEVEL2_FIND_CONTEXT_OFFSET >> 2))
                        state = 1;
                }

                if (state == 1) {

                    if (ws == -1 && apstart + i >= find.pos_start) {
                        text.append((char)AlStyles.CHAR_MARKER_FIND_S);
                        ws = apstart + i;
                    }

                    text.append(ch);

                    if (we == -1 && apstart + i >= find.pos_end) {
                        text.append((char)AlStyles.CHAR_MARKER_FIND_E);
                        we = apstart + i;
                    }

                    if (apstart + i > find.pos_end + LEVEL2_FIND_CONTEXT_OFFSET) {
                        if (ch == 0x20 || i == 0 || apstart + i > find.pos_end + LEVEL2_FIND_CONTEXT_OFFSET + (LEVEL2_FIND_CONTEXT_OFFSET >> 1)) {
                            state = 2;
                        }
                    }

                } else
                if (state == 2) {
                    if (par_num > par_stop)
                        break;
                }
            }
        }

        find.context = text.toString();
    }

    static boolean getTestBuffer(AlFiles a, int cp, char[] dst, int len, boolean toLower) {

        byte[] buf = new byte[len << 2];
        int cnt = a.getByteBuffer(0, buf, len << 2);
        Integer opos = 0;
        AlIntHolder ipos = new AlIntHolder(0);

        while ((ipos.value < cnt) && (opos < len))
            dst[opos++] = AlUnicode.byte2Wide(cp, buf, ipos);
        buf = null;

        if (toLower)
            for (cnt = 0; cnt < opos; cnt++)
                dst[cnt] = AlUnicode.toLower(dst[cnt]);

        if (opos > 0) {
            dst[opos - 1] = 0x00;
            return true;
        }

        return false;
    }

    public int getNumParagraphByPoint(int pos) {
        if (pos < 0)
            return 0;
        if (pos >= size)
            return par0.size() - 1;
        return findParagraphByPos(pos);
    }

    public int getStartPragarphByNum(int num) {
        return par0.get(num).start;
    }

    public int getLengthPragarphByNum(int num) {
        return par0.get(num).length;
    }

    public long getStylePragarphByNum(int num) {
        return par0.get(num).paragraph;
    }

    public String getLinkNameByPos(int pos, InternalConst.TAL_LINK_TYPE typeLink) {
        StringBuilder res = new StringBuilder();

        if (pos < 0 | pos >= size)
            return null;

        final char startChar;
        final char endChar;

        switch (typeLink) {
            case LINK:
                startChar = AlStyles.CHAR_LINK_S;
                endChar = AlStyles.CHAR_LINK_E;
                break;
            case IMAGE:
                startChar = AlStyles.CHAR_IMAGE_S;
                endChar = AlStyles.CHAR_IMAGE_E;
                break;
            case ROW:
                startChar = AlStyles.CHAR_ROWS_S;
                endChar = AlStyles.CHAR_ROWS_E;
                break;
            default:
                return null;
        }

        //final char startChar = (char) (getLink ? AlStyles.CHAR_LINK_S : AlStyles.CHAR_IMAGE_S);
        //final char endChar = (char) (getLink ? AlStyles.CHAR_LINK_E : AlStyles.CHAR_IMAGE_E);

        AlOneParagraph ap;
        int par_num = findParagraphByPos(pos);
        int j;
        boolean fl = false;
        while (par_num >= 0) {
            ap = par0.get(par_num);
            getParagraph(ap);
            j = pos - ap.start;
            if (j >= ap.length)
                j = ap.length - 1;

            for (; j >= 0; j--) {
                if (fl) {
                    if (stored_par.data[j] == startChar) {
                        if (res.length() < 1)
                            return null;
                        if (res.charAt(0) == '#')
                            res.delete(0, 1);
                        if (res.length() < 1)
                            return null;
                        return res.toString();
                    } else {
                        if (res.length() == 0) {
                            res.append(stored_par.data[j]);
                        } else {
                            res.insert(0, stored_par.data[j]);
                        }
                    }
                } else {
                    if (stored_par.data[j] == endChar)
                        fl = true;
                }
            }

            if (fl)
                return null;

            par_num--;
        }

        return null;
    }

    public boolean getNoNeedSave() {
        return multiFiles.modePart;
    }

    public long	getPositionAddon(int pos) {
        if (multiFiles.modePart)
            return 0L;

        for (int i = multiFiles.collect.size() - 1; i >= 0; i--) {
            if (pos >= multiFiles.collect.get(i).level2_start) {

                i -= AlMultiFiles.LEVEL_FOR_MULTI >> 1;
                if (i < 0)
                    return 0L;

                return (((long)(multiFiles.collect.get(i).level2_start & 0x7fffffff)) << 32L) |
                        (multiFiles.collect.get(i).level1_start & 0x7fffffff);
            }
        }

        return 0L;
    }

    public AlOneImage getImageByName(String name) {
        if (name == null)
            return null;

        if (LEVEL2_COVERTOTEXT_STR.equalsIgnoreCase(name)) {
            if (coverName == null)
                return null;
            name = coverName;
        }

        if (im != null) {
            for (int i = 0; i < im.size(); i++) {
                if (name.equalsIgnoreCase(im.get(i).name))
                    return im.get(i);
            }
        }
		
		/*if (tableToText.equalsIgnoreCase(name)) {
			AlImage al = AlImage.addImage(tableToText, 0, 0, AlImage.IMG_TABLE + AlImage.IMG_UNKNOWN);
			if (addImage(al))
				return im0.get(im0.size() - 1);
			return null;
		}*/

        AlOneImage a = new AlOneImage();
        a.name = name;
        /*if (LEVEL2_TABLETOTEXT_STR.equalsIgnoreCase(name)) {
            a.positionE = 0;
            a.iType = AlOneImage.IMG_TABLE;
        } else {*/
        int num = aFiles.getExternalFileNum(name);
        if (num != AlFiles.LEVEL1_FILE_NOT_FOUND) {
            a.positionE = aFiles.getExternalFileSize(num);
            a.iType = AlOneImage.IMG_MEMO;
        }
        //}
        im.add(a);

        return im.get(im.size() - 1);
    }

    public AlOneTable getTableByName(String name) {
        if (ta == null)
            return null;
        int i, table_start;

        if (!name.startsWith("table:"))
            return null;

        table_start = InternalFunc.str2int(name.substring(6), 10);

        for (i = 0; i < ta.size(); i++) {
            if (ta.get(i).start == table_start)
                return ta.get(i);
        }

        return null;
    }

    public AlOneTable getTableByNum(int table_start) {
        if (ta == null)
            return null;

        for (int i = 0; i < ta.size(); i++) {
            if (ta.get(i).start == table_start)
                return ta.get(i);
        }

        return null;
    }


    public AlOneLink getLinkByName(String name, boolean internalOnly) {
        if (lnk == null && internalOnly)
            return null;

        int i;
        if (lnk != null) {
            for (i = 0; i < lnk.size(); i++) {
                if (name.equalsIgnoreCase(lnk.get(i).name))
                    return lnk.get(i);
            }

            i = name.indexOf('#');
            if (i > 0) {
                String name2 = name.substring(0, i);
                for (i = 0; i < lnk.size(); i++) {
                    if (name2.equalsIgnoreCase(lnk.get(i).name))
                        return lnk.get(i);
                }
            }
        }
		
		/*if (internalOnly)
			return null;
		
		{
			String nameLink = name;
			String metka = null;
			
			int indexMetki = name.lastIndexOf('#');
			int indexSlash = name.lastIndexOf('/');
			int indexDouble = name.lastIndexOf(':');
			if (indexMetki > 0 && indexMetki > indexSlash && indexMetki > indexDouble) {			
				String currName = name.substring(0, indexMetki);
				int tmp = currName.lastIndexOf('.');
				if (tmp > 0) {
					currName = currName.substring(tmp, indexMetki);
					if (AlReader3GridOpenFile.isValidFileExt(currName.toLowerCase())) {
						metka = name.substring(indexMetki + 1, name.length());
						nameLink = name.substring(0, indexMetki);
					}
				}
			}
			
			nameLink = aFiles.externalFileExists(nameLink);
			if (nameLink != null) {
				AlLink a = new AlLink();
				a.name = "file://" + nameLink;
				a.iType = AlLink.LINK_TEXT;
				a.positionE = a.positionS = -1;
				if (metka != null)
					a.name = a.name + "#" + metka;
				return a;
			}
		}*/

        return null;
    }

    public TAL_NOTIFY_RESULT createDebugFile(String pathForDebug) {
        TAL_NOTIFY_RESULT res;
        res = aFiles.createDebugFile(pathForDebug);
        if (res == TAL_NOTIFY_RESULT.ERROR)
            return res;

        AlRandomAccessFile df = new AlRandomAccessFile();

        String ustr;
        byte[] bb = null;

        String tmp = pathForDebug + "_taldeb.f";

        if (df.open(tmp, 1) == TAL_RESULT.OK) {

            ustr = (char) 0xfeff + aFiles.getFullRealName() + "\n\r";
            try {
                bb = ustr.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            df.write(bb);

            ustr = aFiles.toString() + this.toString();
            try {
                bb = ustr.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            df.write(bb);

            ustr = "\n\rCalculation page time: " + lastCalcTime;
            try {
                bb = ustr.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            df.write(bb);
            ustr = "\n\rCalculation page count: " + lastPageCount;
            try {
                bb = ustr.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            df.write(bb);

            if (bookTitle != null) {
                ustr = "\n\rTitle: \"" + bookTitle + "\"";
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                df.write(bb);
            }

            if (bookAuthors.size() > 0) {
                ustr = "\n\rAuthors: ";
                for (int i = 0; i < bookAuthors.size(); i++) {
                    ustr += "\"" + bookAuthors.get(i) + "\" ";
                }
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                df.write(bb);
            }

            if (bookGenres.size() > 0) {
                ustr = "\n\rGenres: ";
                for (int i = 0; i < bookGenres.size(); i++) {
                    ustr += "\"" + bookGenres.get(i) + "\" ";
                }
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                df.write(bb);
            }

            if (bookSeries.size() > 0) {
                ustr = "\n\rSeries: ";
                for (int i = 0; i < bookSeries.size(); i++) {
                    ustr += "\"" + bookSeries.get(i) + "\" ";
                }
                ustr += "\n\r";
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                df.write(bb);
            }

            if (lnk.size() > 0) {
                ustr = "\n\rLinks:";
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                df.write(bb);
                for (int i = 0; i < lnk.size(); i++) {
                    ustr = "\n\r";
                    ustr += lnk.get(i).toString();
                    try {
                        bb = ustr.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    df.write(bb);
                }
            }

            if (im.size() > 0) {
                ustr = "\n\r\n\rImages:";
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                df.write(bb);
                for (int i = 0; i < im.size(); i++) {
                    ustr = "\n\r";
                    ustr += im.get(i).toString();
                    try {
                        bb = ustr.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    df.write(bb);
                }
            }

            if (ta.size() > 0) {
                ustr = "\n\r\n\rTables:";
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                df.write(bb);
                for (int i = 0; i < ta.size(); i++) {
                    ustr = "\n\r";
                    ustr += ta.get(i).toString();
                    try {
                        bb = ustr.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    df.write(bb);
                }
            }

            if (ttl.size() > 0) {
                ustr = "\n\r\n\rContent:";
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                df.write(bb);
                for (int i = 0; i < ttl.size(); i++) {
                    ustr = "\n\r";
                    ustr += ttl.get(i).toString();
                    try {
                        bb = ustr.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    df.write(bb);
                }
            }

            debugStyles(df);

            //////////////////////////////
            for (int i = 0; i < par0.size(); i++) {

                ustr = "\n\r\n\r" + par0.get(i).toString() + "\n\r";
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                df.write(bb);

                getParagraph(par0.get(i));
                ustr = String.copyValueOf(stored_par.data, 0, par0.get(i).length);
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                df.write(bb);
            }

            if (!multiFiles.modePart) {
                ustr = "\n\r\n\r";
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                df.write(bb);

                for (int i = 0; i < multiFiles.collect.size(); i++) {
                    ustr = "\n\r" + AlOneMultiFile.outString(multiFiles.collect.get(i));
                    try {
                        bb = ustr.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    df.write(bb);
                }
            }


            df.close();
        } else {
            res = TAL_NOTIFY_RESULT.ERROR;
        }

        df = null;

        return res;
    }

    abstract protected void doTextChar(char ch, boolean addSpecial);

    public int getPageStart(int pos) {
        return 0;
    }

    public int getCountPages() {
        return 0;
    }

    public void debugStyles(AlRandomAccessFile df) {

    };

    protected void prepareCustom() {
        haveNotesOnPageReal = false;
        if (lnk.size() > 0)
            for (int i = 0; i < lnk.size(); i++) {
                if (lnk.get(i).iType == 1) {
                    haveNotesOnPageReal = true;
                    break;
                }
            }
    }

    abstract protected void parser(final int start_pos, final int stop_posRequest);

    abstract public void initState(AlBookOptions bookOptions, AlFiles myParent,
                                   AlPreferenceOptions pref, AlStylesOptions stl);

    public String getTableSource(int address) {
        if (ta == null)
            return null;

        int i, j;
        AlOneTable table = null;
        for (i = 0; i < ta.size(); i++) {
            if (ta.get(i).start == address) {
                table = ta.get(i);
                break;
            }
        }

        if (table == null)
            return null;

        StringBuilder sb = new StringBuilder();

        int buf_cnt;
        char ch;
        AlIntHolder jVal = new AlIntHolder(0);
        for (i = table.start; i < table.stop; ) {
            buf_cnt = AlFiles.LEVEL1_FILE_BUF_SIZE;
            if (i + buf_cnt > table.stop) {
                buf_cnt = aFiles.getByteBuffer(i, parser_inBuff, table.stop - i + 2);
                if (buf_cnt > table.stop - i)
                    buf_cnt = table.stop - i;
            } else {
                buf_cnt = aFiles.getByteBuffer(i, parser_inBuff, buf_cnt + 2);
                buf_cnt -= 2;
            }

            for (j = 0; j < buf_cnt; ) {
                allState.start_position = i + j;

                jVal.value = j;
                ch = AlUnicode.byte2Wide(use_cpR0, parser_inBuff, jVal);
                j = jVal.value;

                sb.append(ch);
            }
            i += j;
        }

        if (sb.length() > 0) {
            sb.append('>');
            return sb.toString();
        }
        return null;
    }

    public String getDictWordByPos(int posStart, int posEnd) {
        if (posStart < 0 | posEnd >= size)
            return null;
        if (posStart > posEnd)
            return null;

        StringBuilder res = new StringBuilder();

        AlOneParagraph ap;
        char ch;
        boolean isInvisible = false, isAccept = false;
        int par_num = findParagraphByPos(posStart);

        ap = par0.get(par_num);
        getParagraph(ap);

        for (int j = 0; j < ap.length; j++) {
            ch = stored_par.data[j];

            isAccept = ap.start + j >= posStart;

            if (ch == 0xad) {

            } else if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {

            } else if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {

            } else if (ch < 0x20) {
                switch (ch) {
                    case AlStyles.CHAR_SOFTPAR:
                        res.setLength(0);
                        break;
                    case AlStyles.CHAR_TITLEIMG_START:
                        if (isInlineImage())
                            isInvisible = true;
                        break;
                    case AlStyles.CHAR_TITLEIMG_STOP:
                        if (isInlineImage())
                            isInvisible = false;
                        break;
                    case AlStyles.CHAR_ROWS_S:
                    case AlStyles.CHAR_LINK_S:
                    case AlStyles.CHAR_IMAGE_S:
                        isInvisible = true;
                        break;
                    case AlStyles.CHAR_ROWS_E:
                    case AlStyles.CHAR_LINK_E:
                    case AlStyles.CHAR_IMAGE_E:
                        isInvisible = false;
                        break;
                }
            } else if (isInvisible) {

            } else if (isAccept) {
                if (ch == 0x20) {
                    break;
                } else if (AlUnicode.isLetter(ch)) {
                    if (AlUnicode.isChineze(ch))
                        if (ap.start + j > posEnd)
                            break;
                    res.append(ch);
                } else if (AlUnicode.isApostrophe(ch) || ch == '-' || ch == '\'' || ch == 0x301) {
                    if (ch != 0x301) {
                        if (res.length() > 0)
                            res.append(ch);
                    }
                } else {
                    if (res.length() > 0)
                        break;
                }
            } else {
                if (AlUnicode.isLetter(ch)) {
                    if (AlUnicode.isChineze(ch)) {
                        res.setLength(0);
                    } else {
                        res.append(ch);
                    }
                } else if (AlUnicode.isApostrophe(ch) || ch == '-' || ch == '\'' || ch == 0x301) {
                    if (ch != 0x301) {
                        if (res.length() > 0)
                            res.append(ch);
                    }
                } else {
                    res.setLength(0);
                }
            }
        }

        while (res.length() > 0 && !AlUnicode.isLetter(res.charAt(res.length() - 1)))
            res.deleteCharAt(res.length() - 1);

        return res.length() > 0 ? res.toString() : null;
    }

    public String getTextByPos(int posStart, int posEnd, boolean forDictionary) {
        if (posStart < 0 | posEnd > size)
            return null;
        if (posStart > posEnd)
            return null;

        StringBuilder res = new StringBuilder();

        AlOneParagraph ap;
        char ch;
        boolean isInvisible = false, isAccept = false;
        int par_num = findParagraphByPos(posStart);

        while (true) {

            ap = par0.get(par_num);
            getParagraph(ap);

            for (int j = 0; j < ap.length; j++) {
                ch = stored_par.data[j];

                isAccept = ap.start + j >= posStart;

                if (ch == 0xad) {

                } else if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {

                } else if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {

                } else if (ch < 0x20) {
                    switch (ch) {
                        case AlStyles.CHAR_SOFTPAR:
                            res.append(' ');
                            break;
                        case AlStyles.CHAR_TITLEIMG_START:
                            if (isInlineImage())
                                isInvisible = true;
                            break;
                        case AlStyles.CHAR_TITLEIMG_STOP:
                            if (isInlineImage())
                                isInvisible = false;
                            break;
                        case AlStyles.CHAR_ROWS_S:
                        case AlStyles.CHAR_LINK_S:
                            isInvisible = true;
                            break;
                        case AlStyles.CHAR_IMAGE_S:
                            if (isAccept)
                                res.append('*');
                            isInvisible = true;
                            break;
                        case AlStyles.CHAR_ROWS_E:
                        case AlStyles.CHAR_LINK_E:
                        case AlStyles.CHAR_IMAGE_E:
                            isInvisible = false;
                            break;
                    }
                } else if (isInvisible) {

                } else if (isAccept) {
                    if (ch == 0x20) {
                        if (ap.start + j >= posEnd)
                            break;
                        if (res.length() > 0 && res.charAt(res.length() - 1) > 0x20)
                            res.append(ch);
                    } else if (ch == 0x301) {

                    } else {
                        res.append(ch);
                    }
                } else {
                    if (ch == 0x20 || AlUnicode.isChineze(ch)) {
                        res.setLength(0);
                    } else if (ch == 0x301) {

                    } else {
                        res.append(ch);
                    }
                }
            }

            if (ap.start + ap.length >= posEnd)
                break;

            if (isAccept)
                res.append("\r\n");

            par_num++;
        }

        if (res.length() > 0 && forDictionary) {
            StringBuilder s = new StringBuilder();
            s.setLength(0);
            for (int i = 0; i < res.length(); i++) {
                if (res.charAt(i) == 0x301)
                    continue;

                if (AlUnicode.isLetter(res.charAt(i))) {

                    s.append(res.charAt(i));
                } else {
                    switch (res.charAt(i)) {
                        case 0x2d:
                        case 0x2010:
                        case 0x2011:
                        case 0x2012:
                            //case 0x2013:
                            //case 0x2014:
                            //case 0x2015:

                        case 0x27:
                        case 0x60:
                        case 0x2019:
                        case 0x2bc:
                            s.append(res.charAt(i));
                            break;
                        default:
                            if (s.length() < 1 || s.charAt(s.length() - 1) != 0x20)
                                s.append(' ');
                            break;
                    }
                }
            }

            res.setLength(0);
            if (s.length() > 0)
                res.append(s.toString().trim());
        }

        return res.length() > 0 ? res.toString() : null;
    }

    protected boolean haveNotesOnPageReal = false;

    public boolean haveNotesOnPage() {
        return haveNotesOnPageReal;
    }

    protected String getHyperLink(String str) {
        String res = "";

        String s = str.trim();
        if (s.length() > 0) {
            while (s.contains("  ")) {
                s = s.replace("  ", " ");
            }

            if (s.toUpperCase().startsWith("PAGEREF ") ||
                    s.toUpperCase().startsWith("REF ") ||
                    s.toUpperCase().startsWith("HYPERLINK ")) {

                StringBuilder s2 = new StringBuilder();
                s2.setLength(0);

                int k = 2, j = s.length();

                for (; k < j; k++) if (s.charAt(k) == 0x20) break;
                for (; k < j; k++) if (s.charAt(k) != 0x20) break;
                while (s.charAt(k) == '\\') {
                    for (; k < j; k++) if (s.charAt(k) == 0x20) break;
                    for (; k < j; k++) if (s.charAt(k) != 0x20) break;
                }

                if (s.charAt(k) == '\"') k++;
                for (; k < j; k++) {
                    if (s.charAt(k) == 0x20 || s.charAt(k) == '\"' || s.charAt(k) == '\\')
                        break;
                    s2.append(s.charAt(k));
                }

                if (s2.length() > 0) {
                    res = s2.toString();
                }
            }
        }

        return res;
    }
}