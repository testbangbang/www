package com.neverland.engbook.level2;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlOneZIPRecord;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;

public class AlFormatHTML extends AlAXML {
    private static final int HTML_TEST_BUF_LENGTH = 1024;

    private static final String HTML_TEST_STR_1 = "<html";
    private static final String HTML_TEST_STR_2 = "<head";
    private static final String HTML_TEST_STR_3 = "<body";
    private static final String HTML_TEST_STR_4 = "<meta";
    private static final String HTML_TEST_STR_5 = " html";

    public static boolean isHTML(AlFiles a) {

        char[] buf_uc = new char[HTML_TEST_BUF_LENGTH];
        String s;

        if (getTestBuffer(a, TAL_CODE_PAGES.CP1251, buf_uc, HTML_TEST_BUF_LENGTH)) {
            s = String.copyValueOf(buf_uc);
            if  ((s.contains(HTML_TEST_STR_1) || s.contains(HTML_TEST_STR_5)) &&
                    (s.contains(HTML_TEST_STR_2) || s.contains(HTML_TEST_STR_3) || s.contains(HTML_TEST_STR_4))) {
                return true;
            }
        }

        if (getTestBuffer(a, TAL_CODE_PAGES.CP1200, buf_uc, HTML_TEST_BUF_LENGTH)) {
            s = String.copyValueOf(buf_uc);
            if  ((s.contains(HTML_TEST_STR_1) || s.contains(HTML_TEST_STR_5)) &&
                    (s.contains(HTML_TEST_STR_2) || s.contains(HTML_TEST_STR_3) || s.contains(HTML_TEST_STR_4))) {
                return true;
            }
        }

        if (getTestBuffer(a, TAL_CODE_PAGES.CP1201, buf_uc, HTML_TEST_BUF_LENGTH)) {
            s = String.copyValueOf(buf_uc);
            if  ((s.contains(HTML_TEST_STR_1) || s.contains(HTML_TEST_STR_5)) &&
                    (s.contains(HTML_TEST_STR_2) || s.contains(HTML_TEST_STR_3) || s.contains(HTML_TEST_STR_4))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref, AlStylesOptions stl) {
        allState.isOpened = true;

        xml_mode = false;
        ident = "HTML";

        aFiles = myParent;

        if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG) != 0)
            aFiles.needUnpackData();

        preference = pref;
        styles = stl;

        size = 0;

        autoCodePage = bookOptions.codePage == TAL_CODE_PAGES.AUTO;
        if (autoCodePage) {
            setCP(getBOMCodePage(true, true, true, true));
        } else {
            setCP(bookOptions.codePage);
        }
        if (use_cpR0 == TAL_CODE_PAGES.AUTO)
            setCP(bookOptions.codePageDefault);

        allState.state_parser = STATE_XML_SKIP;
        allState.state_skipped_flag = true;

        allState.state_parser = 0;
        parser(0, aFiles.getSize());
        newParagraph();

        allState.isOpened = false;
    }

    protected int 		section_count = 0;
    protected boolean 	is_title = false;
    protected boolean 	is_book_title = false;
    protected int 		content_start = 0;
    protected int		skip_count = 0;

    @Override
    boolean isNeedAttribute(int atr) {
        switch (atr) {
            case AlFormatTag.TAG_NAME:
            case AlFormatTag.TAG_ID:
            case AlFormatTag.TAG_HREF:
            case AlFormatTag.TAG_TYPE:
            case AlFormatTag.TAG_ALIGN:
            case AlFormatTag.TAG_SRC:
            case AlFormatTag.TAG_NOTE:
            case AlFormatTag.TAG_CHARSET:
            case AlFormatTag.TAG_CONTENT:
            case AlFormatTag.TAG_VALUE:
            case AlFormatTag.TAG_NUMBER:
            case AlFormatTag.TAG_CONTENT_TYPE:
            case AlFormatTag.TAG_TITLE:
                return true;
        }
        return super.isNeedAttribute(atr);
    }

    private void addTestContent(String s, int level) {
        if (s == null)
            return;
        s = s.trim();
        if (s.length() == 0)
            return;

        if ((paragraph & AlStyles.PAR_NOTE) == 0)
            addContent(AlOneContent.add(s, content_start, level));
    }

    protected  void setSpecialText(boolean flag) {
        if (flag) {
            allState.state_special_flag0 = true;
            state_specialBuff0.setLength(0);
        } else {
            if (is_book_title) {
                if (allState.isOpened) {
                    bookTitle = state_specialBuff0.toString().trim();
                    addTestContent(bookTitle, section_count);
                }
                is_book_title = false;
            } else
            if (is_title) {
                if (allState.isOpened)
                    addTestContent(state_specialBuff0.toString().trim(), section_count);
                is_title = false;
            }
            allState.state_special_flag0 = false;
        }
    }

    public  boolean addImages() {
        StringBuilder s = tag.getATTRValue(AlFormatTag.TAG_SRC);

        if (s == null)
            s = tag.getATTRValue(AlFormatTag.TAG_HREF);

        if (s != null) {
            addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
            addTextFromTag(s, false);
            addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
            return false;
        }

        return false;
    }

    protected  boolean addNotes() {
        StringBuilder s = tag.getATTRValue(AlFormatTag.TAG_HREF);

        if (s != null) {
            addCharFromTag((char) AlStyles.CHAR_LINK_S, false);
            addTextFromTag(s, false);
            addCharFromTag((char) AlStyles.CHAR_LINK_E, false);
            return true;
        }

        return false;
    }

    //public final static String listAdd0 = "" + (char)0x2022 + (char)0xa0;

    @Override
    public boolean externPrepareTAG() {
        StringBuilder param;

        if (allState.isOpened/* && tag.tag != AlFormatTag.TAG_BINARY*/) {
            param = tag.getATTRValue(AlFormatTag.TAG_ID);
            if (param != null)
                addtestLink(param.toString());
        }

        switch (tag.tag) {
            case AlFormatTag.TAG_SCRIPT:
            case AlFormatTag.TAG_STYLE:
                if (tag.closed) {
                    if (skip_count > 0)
                        skip_count--;
                    if (skip_count == 0) {
                        allState.state_skipped_flag = false;
                    } else {
                        allState.state_parser = STATE_XML_SKIP;
                    }
                } else if (!tag.ended) {
                    skip_count++;
                    allState.state_skipped_flag = true;
                    allState.state_parser = STATE_XML_SKIP;
                } else {

                }
                return true;
            case AlFormatTag.TAG_TITLE:
                if (tag.closed) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0) {
                        setSpecialText(false);
                    }
                } else if (!tag.ended) {
                    //testRealCodePage();
                    if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0) {
                        is_book_title = true;
                        setSpecialText(true);
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_A:
                if (tag.closed) {
                    if ((paragraph & AlStyles.PAR_STYLE_LINK) != 0)
                        clearTextStyle(AlStyles.PAR_STYLE_LINK);
                } else if (!tag.ended) {
                    if (allState.isOpened) {
                        param = tag.getATTRValue(AlFormatTag.TAG_NAME);
                        if (param != null) {
                            addtestLink(param.toString());
                        }
                    }
                    if (addNotes())
                        setTextStyle(AlStyles.PAR_STYLE_LINK);
                } else {

                }
                return true;
            case AlFormatTag.TAG_HEAD:
                if (tag.closed) {
                    //testRealCodePage();
                    clearParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                    newParagraph();
                } else if (!tag.ended) {
                    if (allState.isOpened) {
                        newParagraph();
                        setParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_BODY:
                if (tag.closed) {
                    //state_skipped_flag = true;
                    clearParagraphStyle(AlStyles.PAR_NOTE);
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                } else if (!tag.ended) {
                    //testRealCodePage();
                    allState.state_skipped_flag = false;
                    skip_count = 0;
                    newParagraph();
                } else {

                }
                return true;
            case AlFormatTag.TAG_Q:
            case AlFormatTag.TAG_BLOCKQUOTE:
            case AlFormatTag.TAG_CITE:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_CITE);
                    newParagraph();
                    newEmptyTextParagraph();
                } else if (!tag.ended) {
                    newParagraph();
                    newEmptyTextParagraph();
                    setParagraphStyle(AlStyles.PAR_CITE);
                } else {

                }
                return true;
            case AlFormatTag.TAG_H1:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_TITLE);//
                    newParagraph();
                    newEmptyStyleParagraph();
                    //setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                    if (allState.isOpened)
                        setSpecialText(false);
                } else if (!tag.ended) {
                    newParagraph();
                    newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                    setParagraphStyle(AlStyles.PAR_TITLE);// | AlStyles.PAR_BREAKPAGE);
                    if (allState.isOpened) {
                        section_count = 0;
                        is_title = true;
                        content_start = size;
                        setSpecialText(true);
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_H2:
            case AlFormatTag.TAG_H3:
            case AlFormatTag.TAG_H4:
            case AlFormatTag.TAG_H5:
            case AlFormatTag.TAG_H6:
            case AlFormatTag.TAG_H7:
            case AlFormatTag.TAG_H8:
            case AlFormatTag.TAG_H9:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_SUBTITLE);
                    newParagraph();
                    newEmptyStyleParagraph();
                    if (allState.isOpened)
                        setSpecialText(false);
                } else if (!tag.ended) {
                    newParagraph();
                    newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_SUBTITLE);// | AlStyles.PAR_BREAKPAGE);
                    if (allState.isOpened) {
                        section_count = 1;
                        is_title = true;
                        content_start = size;
                        setSpecialText(true);
                    }
                } else {
                    newParagraph();
                    newEmptyStyleParagraph();
                }
                return true;
            case AlFormatTag.TAG_DIV:
            case AlFormatTag.TAG_DT:
            case AlFormatTag.TAG_DD:
            case AlFormatTag.TAG_P:


                if (tag.closed) {
                    newParagraph();
                } else if (!tag.ended) {
                    newParagraph();
                } else {
                    newParagraph();
                    newEmptyTextParagraph();
                }
                return true;
            case AlFormatTag.TAG_TT:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_CODE);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_CODE);
                } else {

                }
                return true;
            case AlFormatTag.TAG_SUP:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_SUP);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_SUP);
                } else {

                }
                return true;
            case AlFormatTag.TAG_SUB:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_SUB);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_SUB);
                } else {

                }
                return true;
            case AlFormatTag.TAG_B:
            case AlFormatTag.TAG_STRONG:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_BOLD);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_BOLD);
                } else {

                }
                return true;
            case AlFormatTag.TAG_I:
            case AlFormatTag.TAG_EM:
            case AlFormatTag.TAG_DFM:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_ITALIC);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_ITALIC);
                } else {

                }
                return true;
            case AlFormatTag.TAG_U:
            case AlFormatTag.TAG_S:
            case AlFormatTag.TAG_INS:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_UNDER);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_UNDER);
                } else {

                }
                return true;
            case AlFormatTag.TAG_STRIKE:
            case AlFormatTag.TAG_DEL:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_STRIKE);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_STRIKE);
                } else {

                }
                return true;
            case AlFormatTag.TAG_PRE:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_PRE);
                    newParagraph();
                    allState.state_code_flag = false;
                } else if (!tag.ended) {
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_PRE);
                    allState.state_code_flag = true;
                } else {

                }
                return true;
            case AlFormatTag.TAG_UL:
            case AlFormatTag.TAG_OL:
            //case AlFormatTag.TAG_LI:
                if (tag.closed) {
                    decULNumber();
                } else
                if (!tag.ended) {
                    incULNumber();
                } else {

                }
                return true;
            case AlFormatTag.TAG_LI:
                newParagraph();
                return true;
            case AlFormatTag.TAG_HR:
            case AlFormatTag.TAG_BR:
                if (tag.closed) {

                } else /*if (!tag.ended) {
                    newParagraph();
                } else */{
                    newParagraph();
                }
                return true;
            case AlFormatTag.TAG_IMG:
                if (tag.closed) {

                } else if (!tag.ended) {
                    addImages();
                } else {
                    addImages();
                }
                return true;
            case AlFormatTag.TAG_TABLE:
            case AlFormatTag.TAG_TR:
            case AlFormatTag.TAG_TD:
                if (tag.closed) {

                } else /*if (!tag.ended) {
                    newParagraph();
                } else */{
                    newParagraph();
                }
                return true;
        }

        /*if (tag.closed) {
            switch (tag.tag) {
                case AlFormatTag.TAG_SCRIPT:
                case AlFormatTag.TAG_STYLE:
                    if (skip_count > 0)
                        skip_count--;
                    if (skip_count == 0) {
                        allState.state_skipped_flag = false;
                    } else {
                        allState.state_parser = STATE_XML_SKIP;
                    }
                    return true;

                case AlFormatTag.TAG_TITLE:
                    if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0) {
                        setSpecialText(false);
                    }
                    return true;

                case AlFormatTag.TAG_A:
                    if ((paragraph & AlStyles.PAR_STYLE_LINK) != 0)
                        clearTextStyle(AlStyles.PAR_STYLE_LINK);
                    return true;

                case AlFormatTag.TAG_HEAD:
                    //testRealCodePage();
                    clearParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                    newParagraph();
                    return true;
                case AlFormatTag.TAG_BODY:

                    //state_skipped_flag = true;
                    clearParagraphStyle(AlStyles.PAR_NOTE);
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                    return true;

                case AlFormatTag.TAG_Q:
                case AlFormatTag.TAG_BLOCKQUOTE:
                case AlFormatTag.TAG_CITE:
                    clearParagraphStyle(AlStyles.PAR_CITE);
                    newParagraph();
                    newEmptyTextParagraph();

                case AlFormatTag.TAG_H1:
                    clearParagraphStyle(AlStyles.PAR_TITLE);//
                    newParagraph();
                    newEmptyStyleParagraph();
                    //setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                    if (allState.isOpened)
                        setSpecialText(false);
                    return true;

                case AlFormatTag.TAG_H2:
                case AlFormatTag.TAG_H3:
                case AlFormatTag.TAG_H4:
                case AlFormatTag.TAG_H5:
                case AlFormatTag.TAG_H6:
                case AlFormatTag.TAG_H7:
                case AlFormatTag.TAG_H8:
                case AlFormatTag.TAG_H9:
                    clearParagraphStyle(AlStyles.PAR_SUBTITLE);
                    newParagraph();
                    newEmptyStyleParagraph();
                    if (allState.isOpened)
                        setSpecialText(false);
                    return true;

                case AlFormatTag.TAG_DIV:
                case AlFormatTag.TAG_DT:
                case AlFormatTag.TAG_DD:
                case AlFormatTag.TAG_P:
                    newParagraph();
                    return true;

                case AlFormatTag.TAG_TT:
                    clearTextStyle(AlStyles.PAR_STYLE_CODE);
                    return true;
                case AlFormatTag.TAG_SUP:
                    clearTextStyle(AlStyles.PAR_STYLE_SUP);
                    return true;
                case AlFormatTag.TAG_SUB:
                    clearTextStyle(AlStyles.PAR_STYLE_SUB);
                    return true;
                case AlFormatTag.TAG_B:
                case AlFormatTag.TAG_STRONG:
                    clearTextStyle(AlStyles.PAR_STYLE_BOLD);
                    return true;
                case AlFormatTag.TAG_I:
                case AlFormatTag.TAG_EM:
                case AlFormatTag.TAG_DFM:
                    clearTextStyle(AlStyles.PAR_STYLE_ITALIC);
                    return true;
                case AlFormatTag.TAG_U:
                case AlFormatTag.TAG_S:
                case AlFormatTag.TAG_INS:
                    clearTextStyle(AlStyles.PAR_STYLE_UNDER);
                    return true;
                case AlFormatTag.TAG_STRIKE:
                case AlFormatTag.TAG_DEL:
                    clearTextStyle(AlStyles.PAR_STYLE_STRIKE);
                    return true;
                case AlFormatTag.TAG_PRE:
                    clearParagraphStyle(AlStyles.PAR_PRE);
                    newParagraph();
                    allState.state_code_flag = false;
                    return true;
            }
        } else {
            if (!tag.ended) {


                switch (tag.tag) {
                    case AlFormatTag.TAG_SCRIPT:
                    case AlFormatTag.TAG_STYLE:
                        skip_count++;
                        allState.state_skipped_flag = true;
                        allState.state_parser = STATE_XML_SKIP;
                        return true;

                    case AlFormatTag.TAG_LI:
                        newParagraph();
                        addTextFromTag(listAdd0, true);
                        return true;

                    case AlFormatTag.TAG_A:
                        if (allState.isOpened) {
                            param = tag.getATTRValue(AlFormatTag.TAG_NAME);
                            if (param != null) {
                                addtestLink(param.toString());
                            }
                        }
                        if (addNotes())
                            setTextStyle(AlStyles.PAR_STYLE_LINK);
                        return true;

                    case AlFormatTag.TAG_TITLE:
                        //testRealCodePage();
                        if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0) {
                            is_book_title = true;
                            setSpecialText(true);
                        }
                    case AlFormatTag.TAG_HEAD:
                        if (allState.isOpened) {
                            newParagraph();
                            setParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                        }
                        return true;
                    case AlFormatTag.TAG_BODY:
                        //testRealCodePage();
                        allState.state_skipped_flag = false;
                        skip_count = 0;
                        newParagraph();
                        return true;

                    case AlFormatTag.TAG_Q:
                    case AlFormatTag.TAG_BLOCKQUOTE:
                    case AlFormatTag.TAG_CITE:
                        newParagraph();
                        newEmptyTextParagraph();
                        setParagraphStyle(AlStyles.PAR_CITE);
                        return true;
                    case AlFormatTag.TAG_H1:
                        newParagraph();
                        newEmptyStyleParagraph();
                        setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                        setParagraphStyle(AlStyles.PAR_TITLE);// | AlStyles.PAR_BREAKPAGE);
                        if (allState.isOpened) {
                            section_count = 0;
                            is_title = true;
                            content_start = size;
                            setSpecialText(true);
                        }
                        return true;
                    case AlFormatTag.TAG_H2:
                    case AlFormatTag.TAG_H3:
                    case AlFormatTag.TAG_H4:
                    case AlFormatTag.TAG_H5:
                    case AlFormatTag.TAG_H6:
                    case AlFormatTag.TAG_H7:
                    case AlFormatTag.TAG_H8:
                    case AlFormatTag.TAG_H9:
                        newParagraph();
                        newEmptyStyleParagraph();
                        setParagraphStyle(AlStyles.PAR_SUBTITLE);// | AlStyles.PAR_BREAKPAGE);
                        if (allState.isOpened) {
                            section_count = 1;
                            is_title = true;
                            content_start = size;
                            setSpecialText(true);
                        }
                        return true;

                    case AlFormatTag.TAG_HR:
                    case AlFormatTag.TAG_BR:
                        newParagraph();
                        //newEmptyTextParagraph();
                        return true;

                    case AlFormatTag.TAG_DT:
                    case AlFormatTag.TAG_DIV:
                    case AlFormatTag.TAG_DD:
                    case AlFormatTag.TAG_P:
                        newParagraph();
                        return true;

                    case AlFormatTag.TAG_IMG:
                        addImages();
                        return true;

                    case AlFormatTag.TAG_TT:
                        setTextStyle(AlStyles.PAR_STYLE_CODE);
                        return true;
                    case AlFormatTag.TAG_SUP:
                        setTextStyle(AlStyles.PAR_STYLE_SUP);
                        return true;
                    case AlFormatTag.TAG_SUB:
                        setTextStyle(AlStyles.PAR_STYLE_SUB);
                        return true;
                    case AlFormatTag.TAG_B:
                    case AlFormatTag.TAG_STRONG:
                        setTextStyle(AlStyles.PAR_STYLE_BOLD);
                        return true;
                    case AlFormatTag.TAG_I:
                    case AlFormatTag.TAG_EM:
                    case AlFormatTag.TAG_DFM:
                        setTextStyle(AlStyles.PAR_STYLE_ITALIC);
                        return true;
                    case AlFormatTag.TAG_U:
                    case AlFormatTag.TAG_S:
                    case AlFormatTag.TAG_INS:
                        setTextStyle(AlStyles.PAR_STYLE_UNDER);
                        return true;
                    case AlFormatTag.TAG_STRIKE:
                    case AlFormatTag.TAG_DEL:
                        setTextStyle(AlStyles.PAR_STYLE_STRIKE);
                        return true;
                    case AlFormatTag.TAG_PRE:
                        newParagraph();
                        setParagraphStyle(AlStyles.PAR_PRE);
                        allState.state_code_flag = true;
                        return true;
                }
            } else {
                switch (tag.tag) {

                    case AlFormatTag.TAG_H1:
                    case AlFormatTag.TAG_H2:
                    case AlFormatTag.TAG_H3:
                    case AlFormatTag.TAG_H4:
                    case AlFormatTag.TAG_H5:
                    case AlFormatTag.TAG_H6:
                    case AlFormatTag.TAG_H7:
                    case AlFormatTag.TAG_H8:
                    case AlFormatTag.TAG_H9:
                        newParagraph();
                        newEmptyStyleParagraph();
                        return true;

                    case AlFormatTag.TAG_IMG:
                        addImages();
                        return true;

                    case AlFormatTag.TAG_HR:
                    case AlFormatTag.TAG_BR:
                        newParagraph();
                        //newEmptyTextParagraph();
                        return true;

                    case AlFormatTag.TAG_DT:
                    case AlFormatTag.TAG_DD:
                    case AlFormatTag.TAG_DIV:
                    case AlFormatTag.TAG_P:
                        newParagraph();
                        newEmptyTextParagraph();
                        return true;
                }
            }
        }
*/        return false;
    }


}
