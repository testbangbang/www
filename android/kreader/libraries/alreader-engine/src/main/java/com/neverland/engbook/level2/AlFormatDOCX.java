package com.neverland.engbook.level2;

import com.neverland.engbook.allstyles.AlCSSHtml;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlFilesDocx;
import com.neverland.engbook.level1.AlOneZIPRecord;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.InternalFunc;

import java.util.HashMap;

public class AlFormatDOCX extends AlAXML {


    private static final int DOCX_TEST_BUF_LENGTH = 1024;
    private static final String DOCX_TEST_STR_1 = "<?mso-application progid=\"word.document\"?>";
    private static final String DOCX_TEST_STR_2 = "<?xml";
    private static final String DOCX_TEST_STR_3 = "<pkg:package";

    private static final int DOCX_XML_STANDART	= 0;
    private static final int DOCX_XML_WITH_PART	= 1;
    private static final int DOCX_XML_WO_PART	= 2;

    public static boolean isDOCX(AlFiles a) {
        if (a.getIdentStr().contentEquals("docx"))
            return true;
        return false;
    }

    public static int isDOCX_XML(AlFiles a) {

        char[] buf_uc = new char[DOCX_TEST_BUF_LENGTH];
        String s;

        if (getTestBuffer(a, TAL_CODE_PAGES.CP1251, buf_uc, DOCX_TEST_BUF_LENGTH, true)) {
            s = String.copyValueOf(buf_uc);

            if (s.contains(DOCX_TEST_STR_1) && s.contains(DOCX_TEST_STR_2)) {

                if (s.contains(DOCX_TEST_STR_3))
                    return DOCX_XML_WITH_PART;
                return DOCX_XML_WO_PART;
            }
        }

        return 0;
    }

    private int			docx_type;


    public AlFormatDOCX() {
        allState.image_start = -1;
        allState.image_stop = -1;
        allState.image_name = null;
    }

    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref, AlStylesOptions stl) {
        xml_mode = true;
        ident = "DOCX";

        aFiles = myParent;

        docx_type = isDOCX_XML(aFiles);

        if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG) != 0)
            aFiles.needUnpackData();

        preference = pref;
        styles = stl;

        size = 0;
        active_file = UNKNOWN_FILE_SOURCE_NUM;
        active_type = AlOneZIPRecord.SPECIAL_UNKNOWN;

        autoCodePage = false;
        setCP(TAL_CODE_PAGES.CP65001);

        allState.state_parser = STATE_XML_SKIP;
        allState.incSkipped();

        cssStyles.init(this, TAL_CODE_PAGES.CP65001, AlCSSHtml.CSSHTML_SET_DOCX);
        //if ((bookOptions.formatOptions & AlFiles.BOOKOPTIONS_DISABLE_CSS) != 0)
            cssStyles.disableExternal = true;

        styleStack.linearMode = true;

        parser(0, -1);
    }

    private final HashMap<String, String> allId = new HashMap<>();

    private void addRelationShip() {
        if (active_type == AlOneZIPRecord.SPECIAL_FIRST) {
            StringBuilder sId = tag.getATTRValue(AlFormatTag.TAG_ID);
            StringBuilder sTarget = tag.getATTRValue(AlFormatTag.TAG_TARGET);
            String target;

            if (sId != null && sTarget != null && sId.length() > 0 && sTarget.length() > 0) {

                if (sTarget.toString().indexOf(':') != -1
                    /*sTarget.startsWith("http:") ||
                    sTarget.startsWith("https:") ||
                    sTarget.startsWith("ftp:") ||
                    sTarget.startsWith("file:") ||
                    sTarget.startsWith("mailto:")*/) {
                    target = sTarget.toString();
                } else {
                    target = AlFiles.getAbsoluteName("/word/", sTarget.toString());
                }

                allId.put(sId.toString(), target);
            }
        }
    }



    static final int DOCX_ALLSTYLES = (int) (AlStyles.STYLE_BOLD | AlStyles.STYLE_ITALIC |
                AlStyles.STYLE_UNDER | AlStyles.STYLE_STRIKE |
                AlStyles.STYLE_SUP | AlStyles.STYLE_SUB);


    protected void clearStyles() {
        clearTextStyle(DOCX_ALLSTYLES);
    }

    protected void clearList() {
        clearPropStyle(AlParProperty.SL2_UL_BASE);
    }

    @Override
    protected void newParagraph() {
        boolean save_break = false;
        if (parText.length > 0) {
            save_break = true;
        } else {
            setPropStyle(AlParProperty.SL2_EMPTY_BEFORE);
        }

        super.newParagraph();

        if (save_break)
            clearPropStyle(AlParProperty.SL2_BREAK_BEFORE);

        clearList();
        clearStyles();
    }


    @Override
    protected void newEmptyTextParagraph() {
        super.newEmptyTextParagraph();
        clearList();
        clearStyles();
    }

    private static final String STL_EN0 = "1";
    private static final String STL_EN1 = "true";
    private static final String STL_EN2 = "on";
    private static final String STL_NONE = "none";

    private static final String JC_LEFT = "left";
    private static final String JC_RIGHT = "right";
    private static final String JC_CENTER = "center";

    protected final void verifyStyleStart(int stl) {
        StringBuilder v = tag.getATTRValue(AlFormatTag.TAG_VAL);
        boolean res = false;
        if (v == null) {
            res = true;
        } else {

            String u = null;
            switch (v.length()) {
                case 1:
                    if (STL_EN0.contentEquals(v))
                        res = true;
                    break;
                case 2:
                    u = v.toString().toLowerCase();
                    if (STL_EN2.contentEquals(u))
                        res = true;
                    break;
                case 4:
                    u = v.toString().toLowerCase();
                    if (STL_EN1.contentEquals(u))
                        res = true;
                    break;
            }
        }

        if (res) {
            setTextStyle(stl);
        } else {
            clearTextStyle(stl);
        }
    }

    protected final void verifyStyleUnder(int stl) {
        StringBuilder v = tag.getATTRValue(AlFormatTag.TAG_VAL);
        if (v == null)
            return;

        boolean res = true;
        if (v.length() == 4 && STL_NONE.contentEquals(v.toString().toLowerCase()))
            res = false;

        if (res) {
            setTextStyle(stl);
        } else {
            clearTextStyle(stl);
        }
    }

    @Override
    public boolean isNeedAttribute(int atr) {
        switch (atr) {
            case AlFormatTag.TAG_NUMFILES:
            case AlFormatTag.TAG_TARGET:
            case AlFormatTag.TAG_ANCHOR:
            case AlFormatTag.TAG_EMBED:
            case AlFormatTag.TAG_VAL:
                return true;
        }
        return super.isNeedAttribute(atr);
    }


    protected  boolean addNotes() {
        StringBuilder s = tag.getATTRValue(AlFormatTag.TAG_ANCHOR);

        if (s != null) {
            addTextFromTag((char) AlStyles.CHAR_LINK_S + s.toString() + (char) AlStyles.CHAR_LINK_E, false);
            return true;
        }

        s = tag.getATTRValue(AlFormatTag.TAG_ID);
        if (s != null) {
            String s1 = allId.get(s.toString());

            if (s1 != null) {
                addTextFromTag((char) AlStyles.CHAR_LINK_S + s1 + (char) AlStyles.CHAR_LINK_E, false);
                return true;
            }
        }

        return false;
    }

    private void addtestImage() {

        if (allState.image_start > 0) {
            allState.image_stop = tag.start_pos;
            im.add(AlOneImage.add(allState.image_name, allState.image_start, allState.image_stop, AlOneImage.IMG_BASE64));
        }

        allState.image_start = -1;
    }

    private void testImage() {
        allState.image_start = -1;

        StringBuilder s1 = tag.getATTRValue(AlFormatTag.TAG_ID);
        if (s1 != null) {
            allState.image_name = s1.toString();
        }

        allState.image_start = allState.start_position;
    }

    protected  boolean addFootNotes() {
        StringBuilder s = tag.getATTRValue(AlFormatTag.TAG_ID);

        if (s != null) {
            addTextFromTag((char)AlStyles.CHAR_LINK_S + "footnotes" + s + (char)AlStyles.CHAR_LINK_E, false);

            setTextStyle(AlStyles.STYLE_LINK);
            addTextFromTag("{", false);
            addTextFromTag(s, false);
            addTextFromTag("}", false);
            clearTextStyle(AlStyles.STYLE_LINK);

            return true;
        }

        return false;
    }

    protected  boolean addEndNotes() {
        StringBuilder s = tag.getATTRValue(AlFormatTag.TAG_ID);

        if (s != null) {
            addTextFromTag((char)AlStyles.CHAR_LINK_S + "endnotes" + s + (char)AlStyles.CHAR_LINK_E, false);

            setTextStyle(AlStyles.STYLE_LINK);
            addTextFromTag("(" + (char)0x2022 + ")", false);
            clearTextStyle(AlStyles.STYLE_LINK);

            return true;
        }

        return false;
    }



    protected void addTestFootLink(int tp) {
        StringBuilder s = tag.getATTRValue(AlFormatTag.TAG_ID);

        if (s != null && s.length() > 0) {
            if (tp == 0) {
                //addLink(AlLink.addLink("footnotes" + s, size, 1));
                addtestLink("footnotes" + s, size, 1);
            } else
            if (tp == 1) {
                //addLink(AlLink.addLink("endnotes" + s, size, 1));
                addtestLink("endnotes" + s, size, 1);
            }
        }
    }

    protected void addTestLink(int tp) {
        if (active_type != AlOneZIPRecord.SPECIAL_FIRST) {
            StringBuilder s = tag.getATTRValue(AlFormatTag.TAG_NAME);

            if (s != null && s.length() > 0)
                addtestLink(s.toString(), size, tp);
        }
    }


    public boolean addImages() {
        if (docx_type == DOCX_XML_WO_PART) {
            StringBuilder sId = tag.getATTRValue(AlFormatTag.TAG_SRC);

            if (sId != null) {

                addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                addTextFromTag(sId, false);
                addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
                return true;
            }
        } else
        if (active_file != UNKNOWN_FILE_SOURCE_NUM) {
            StringBuilder sId = tag.getATTRValue(AlFormatTag.TAG_ID);

            if (sId == null)
                sId = tag.getATTRValue(AlFormatTag.TAG_EMBED);

            if (sId != null) {

                String sTarget = allId.get(sId.toString());

                if (sTarget != null) {
                    addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                    addTextFromTag(sTarget, false);
                    addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
                }
            }
        }
        return false;
    }

    @Override
    protected boolean externPrepareTAG() {
        StringBuilder param;

        switch (tag.tag) {
            case AlFormatTag.TAG_T:
                if (tag.closed) {
                    allState.incSkipped();
                } else if (!tag.ended) {
                    allState.decSkipped();
                } else {

                }
                return true;
            case AlFormatTag.TAG_HYPERLINK:
                if (tag.closed) {
                    if ((styleStack.buffer[styleStack.position].paragraph & AlStyles.STYLE_LINK) != 0)
                        clearTextStyle(AlStyles.STYLE_LINK);
                } else if (!tag.ended) {
                    if (addNotes())
                        setTextStyle(AlStyles.STYLE_LINK);
                } else {

                }
                return true;
            case AlFormatTag.TAG_BODY:
                if (tag.closed) {
                    clearStateStyle(AlStateLevel2.PAR_NOTE);
                    newParagraph();
                    setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
                } else if (!tag.ended) {
                    newParagraph();
                } else {

                }
                return true;

            case AlFormatTag.TAG_BINARYDATA:
            case AlFormatTag.TAG_BINDATA:
                if (tag.closed) {
                    addtestImage();
                } else
                if (!tag.ended) {
                    allState.state_parser = STATE_XML_SKIP;
                    testImage();
                } else {

                }
                return true;

            case AlFormatTag.TAG_PART:
                if (tag.closed) {
                    closeOpenNotes();
                    active_file = UNKNOWN_FILE_SOURCE_NUM;
                    active_type = AlOneZIPRecord.SPECIAL_UNKNOWN;
                    clearStateStyle(AlStateLevel2.PAR_NOTE);
                    newParagraph();
                    setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
                } else if (!tag.ended) {
                    param = tag.getATTRValue(AlFormatTag.TAG_NAME);
                    if (param != null) {

                        StringBuilder param1 = new StringBuilder();
                        if (param.charAt(0) != EngBookMyType.AL_ROOT_RIGHTPATH && param.charAt(0) != EngBookMyType.AL_ROOT_WRONGPATH)
                            param1.append(EngBookMyType.AL_ROOT_RIGHTPATH);
                        for (int i = 0; i < param.length(); i++)
                            if (param.charAt(i) == EngBookMyType.AL_ROOT_WRONGPATH) param1.append(EngBookMyType.AL_ROOT_RIGHTPATH); else param1.append(param.charAt(i));

                        String param0 = param1.toString();
                        active_file = 1;
                        if (param0.contentEquals(AlFilesDocx.FILE_RELS)) {
                            active_type = AlOneZIPRecord.SPECIAL_FIRST;
                        } else
                        if (param0.contentEquals(AlFilesDocx.FILE_STYLES0)) {
                            active_type = AlOneZIPRecord.SPECIAL_FIRST;
                        } else
                        if (param0.contentEquals(AlFilesDocx.FILE_STYLES1)) {
                            active_type = AlOneZIPRecord.SPECIAL_FIRST;
                        } else
                        if (param0.contentEquals(AlFilesDocx.LEVEL1_ZIP_FIRSTNAME_DOCX)) {
                            active_type = AlOneZIPRecord.SPECIAL_FIRST;
                        } else
                        if (param0.contentEquals(AlFilesDocx.FILE_FOOTNOTES)) {
                            active_type = AlOneZIPRecord.SPECIAL_FIRST;
                            setStateStyle(AlStateLevel2.PAR_NOTE);
                        } else
                        if (param0.contentEquals(AlFilesDocx.FILE_ENDNOTES)) {
                            active_type = AlOneZIPRecord.SPECIAL_FIRST;
                            setStateStyle(AlStateLevel2.PAR_NOTE);
                        } else {
                            allState.image_name = param.toString();
                            active_type = AlOneZIPRecord.SPECIAL_UNKNOWN;
                            active_file = UNKNOWN_FILE_SOURCE_NUM;
                        }

                    } else {
                        active_file = UNKNOWN_FILE_SOURCE_NUM;
                        active_type = AlOneZIPRecord.SPECIAL_UNKNOWN;
                    }
                } else {

                }
                return true;

            case AlFormatTag.TAG_EXTFILE:
                if (tag.closed) {
                    closeOpenNotes();
                    active_file = UNKNOWN_FILE_SOURCE_NUM;
                    active_type = AlOneZIPRecord.SPECIAL_UNKNOWN;
                    clearStateStyle(AlStateLevel2.PAR_NOTE);
                    newParagraph();
                    setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
                } else if (!tag.ended) {
                    param = tag.getATTRValue(AlFormatTag.TAG_IDREF);
                    if (param != null) {
                        switch (InternalFunc.str2int(param, 10)) {
                            case AlOneZIPRecord.SPECIAL_FIRST:
                                active_type = AlOneZIPRecord.SPECIAL_FIRST;
                                break;
                            case AlOneZIPRecord.SPECIAL_STYLE:
                                active_type = AlOneZIPRecord.SPECIAL_FIRST;
                                break;
                            case AlOneZIPRecord.SPECIAL_FOOTNOTE:
                                active_type = AlOneZIPRecord.SPECIAL_FIRST;
                                setStateStyle(AlStateLevel2.PAR_NOTE);
                                break;
                            case AlOneZIPRecord.SPECIAL_ENDNOTE:
                                active_type = AlOneZIPRecord.SPECIAL_FIRST;
                                setStateStyle(AlStateLevel2.PAR_NOTE);
                                break;
                            default:
                                active_type = AlOneZIPRecord.SPECIAL_NONE;
                                break;
                        }

                        param = tag.getATTRValue(AlFormatTag.TAG_NUMFILES);
                        if (param != null) {
                            active_file = InternalFunc.str2int(param, 10);
                        } else {
                            active_file = UNKNOWN_FILE_SOURCE_NUM;
                            active_type = AlOneZIPRecord.SPECIAL_NONE;
                        }
                    } else {
                        active_file = UNKNOWN_FILE_SOURCE_NUM;
                        active_type = AlOneZIPRecord.SPECIAL_NONE;
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_R:
                if (tag.closed) {

                } else if (!tag.ended) {
                    clearStyles();
                } else {

                }
                return true;
            case AlFormatTag.TAG_LI:
                if (tag.closed) {

                } else if (!tag.ended) {
                    newParagraph();
                    addTextFromTag("1", true);
                } else {

                }
                return true;
            case AlFormatTag.TAG_P:
                if (tag.closed) {

                } else if (!tag.ended) {
                    newParagraph();
                } else {
                    newParagraph();
                    newEmptyTextParagraph();
                }
                return true;
            case AlFormatTag.TAG_B:
                if (tag.closed) {

                } else /*if (!tag.ended) {
                    verifyStyleStart(AlStyles.PAR_STYLE_BOLD);
                } else */{
                    verifyStyleStart((int) AlStyles.STYLE_BOLD);
                }
                return true;
            case AlFormatTag.TAG_I:
                if (tag.closed) {

                } else /*if (!tag.ended) {
                    verifyStyleStart(AlStyles.PAR_STYLE_ITALIC);
                } else */{
                    verifyStyleStart((int) AlStyles.STYLE_ITALIC);
                }
                return true;
            case AlFormatTag.TAG_U:
                if (tag.closed) {

                } else /*if (!tag.ended) {
                    verifyStyleUnder(AlStyles.PAR_STYLE_UNDER);
                } else */{
                    verifyStyleUnder((int) AlStyles.STYLE_UNDER);
                }
                return true;
            case AlFormatTag.TAG_STRIKE:
                if (tag.closed) {

                } else /*if (!tag.ended) {
                    verifyStyleStart(AlStyles.PAR_STYLE_STRIKE);
                } else */{
                    verifyStyleStart((int) AlStyles.STYLE_STRIKE);
                }
                return true;
            case AlFormatTag.TAG_BR:
                if (tag.closed) {

                } else if (!tag.ended) {
                    newParagraph();
                    newEmptyTextParagraph();
                } else {

                }
                return true;
            case AlFormatTag.TAG_FOOTNOTE:
                if (tag.closed) {
                    if (preference.onlyPopupFootnote)
                        clearTextStyle(AlStyles.STYLE_HIDDEN);
                } else if (!tag.ended) {
                    param = tag.getATTRValue(AlFormatTag.TAG_TYPE);
                    if (param == null) {
                        newParagraph();
                        addTestFootLink(0);

                        if (preference.onlyPopupFootnote)
                            setTextStyle(AlStyles.STYLE_HIDDEN);

                        param = tag.getATTRValue(AlFormatTag.TAG_ID);
                        boolean setSUP = ((styleStack.buffer[styleStack.position].paragraph & AlStyles.STYLE_SUP) == 0);
                        if (setSUP)
                            setTextStyle(AlStyles.STYLE_SUP);
                        if (param != null) {
                            addTextFromTag(param, false);
                        } else {
                            addTextFromTag("*", false);
                        }
                        if (setSUP)
                            clearTextStyle(AlStyles.STYLE_SUP);
                    }

                } else {

                }
                return true;
            case AlFormatTag.TAG_ENDNOTE:
                if (tag.closed) {

                } else if (!tag.ended) {
                    param = tag.getATTRValue(AlFormatTag.TAG_TYPE);
                    if (param == null) {
                        newParagraph();
                        addTestFootLink(1);
                        addTextFromTag("" + (char)0x2022, false);
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_RELATIONSHIP:
                if (tag.closed) {

                } else /*if (!tag.ended) {
                    addRelationShip();
                } else */{
                    addRelationShip();
                }
                return true;
            case AlFormatTag.TAG_BLIP:
            case AlFormatTag.TAG_IMAGEDATA:
                if (tag.closed) {

                } else /*if (!tag.ended) {
                    addImages();
                } else */{
                    addImages();
                }
                return true;
            case AlFormatTag.TAG_BOOKMARKSTART:
                if (tag.closed) {

                } else /*if (!tag.ended) {
                    if (allState.isOpened) {
                        addTestLink(0);
                    }
                } else */{
                    //if (allState.isOpened) {
                        addTestLink(0);
                    //}
                }
                return true;
            case AlFormatTag.TAG_TAB:
                if (tag.closed) {

                } else /*if (!tag.ended) {
                    doTextChar(' ', false);
                } else */{
                    doTextChar(' ', false);
                }
                return true;
            case AlFormatTag.TAG_FOOTNOTEREFERENCE:
                if (tag.closed) {

                } else if (!tag.ended) {

                } else {
                    addFootNotes();
                }
                return true;
            case AlFormatTag.TAG_ENDNOTEREFERENCE:
                if (tag.closed) {

                } else if (!tag.ended) {

                } else {
                    addEndNotes();
                }
                return true;
            case AlFormatTag.TAG_VERTALIGN:
                if (tag.closed) {

                } else if (!tag.ended) {

                } else {
                    param = tag.getATTRValue(AlFormatTag.TAG_VAL);
                    if (param != null) {
                        if ("superscript".contentEquals(param)) {
                            setTextStyle(AlStyles.STYLE_SUP);
                        } else
                        if ("subscript".contentEquals(param)) {
                            setTextStyle(AlStyles.STYLE_SUB);
                        }
                    }
                }
                return true;
            case AlFormatTag.TAG_ILVL:
                if (tag.closed) {

                } else if (!tag.ended) {

                } else {
                    if (parText.length == 0) {

                            param = tag.getATTRValue(AlFormatTag.TAG_VAL);
                            if (param != null) {
                                try {
                                    long i = Long.parseLong(param.toString());
                                    i++;
                                    if (i > 0 && i <= 15) {
                                        i <<= AlParProperty.SL2_UL_SHIFT;
                                        clearList();
                                        setPropStyle(i);
                                    }
                                } catch (Exception e) {

                                }
                            }

                    }
                }
                return true;
            case AlFormatTag.TAG_NOBREAKHYPHEN:
                if (tag.closed) {

                } else if (!tag.ended) {

                } else {
                    doTextChar('-', false);
                }
                return true;
            case AlFormatTag.TAG_TBL:
            case AlFormatTag.TAG_TC:
            case AlFormatTag.TAG_TR:
            case AlFormatTag.TAG_GRIDSPAN:
                return prepareTable();
        }
       return false;
    }

}
