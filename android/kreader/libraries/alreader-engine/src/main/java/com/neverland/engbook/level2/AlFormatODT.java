package com.neverland.engbook.level2;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlOneZIPRecord;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.InternalFunc;

public class AlFormatODT extends AlAXML {

    //protected int active_file = UNKNOWN_FILE_SOURCE_NUM;
    //protected int active_type = AlOneZIPRecord.SPECIAL_NONE;

    public static boolean isODT(AlFiles a) {
        //noinspection RedundantIfStatement
        if (a.getIdentStr().contentEquals("odt"))
            return true;
        return false;
    }

    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref, AlStylesOptions stl) {
        allState.isOpened = true;

        xml_mode = true;
        ident = "ODT";

        aFiles = myParent;

        if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG) != 0)
            aFiles.needUnpackData();

        preference = pref;
        styles = stl;

        size = 0;

        autoCodePage = false;
        setCP(TAL_CODE_PAGES.CP65001);

        allState.state_parser = STATE_XML_SKIP;
        allState.state_skipped_flag = true;

        parser(0, -1);
        newParagraph();

        allState.isOpened = false;
    }

    @Override
    public final boolean isNeedAttribute(int atr) {
        switch (atr) {
            case AlFormatTag.TAG_NUMFILES:
                return true;
        }
        return super.isNeedAttribute(atr);
    }

    public  boolean addImages() {
        StringBuilder s = tag.getATTRValue(AlFormatTag.TAG_HREF);

        if (s != null) {
            addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
            addTextFromTag(s, false);
            addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
            return false;
        }

        return false;
    }

    private boolean addNotes() {
        StringBuilder s;

        s = tag.getATTRValue(AlFormatTag.TAG_HREF);

        if (s != null) {
            addCharFromTag((char) AlStyles.CHAR_LINK_S, false);
            addTextFromTag(s, false);
            addCharFromTag((char) AlStyles.CHAR_LINK_E, false);
            return true;
        }

        return false;
    }

    @Override
    public boolean externPrepareTAG() {
        StringBuilder param;

        if (allState.isOpened/* && tag.tag != AlFormatTag.TAG_BINARY*/) {
            param = tag.getATTRValue(AlFormatTag.TAG_ID);
            if (param != null)
                addtestLink(param.toString());
        }

        switch (tag.tag) {

            case AlFormatTag.TAG_HEAD:
                if (active_type == AlOneZIPRecord.SPECIAL_NONE) {
                    if (tag.closed) {
                        if (allState.skip_count > 0)
                            allState.skip_count--;
                        if (allState.skip_count == 0) {
                            allState.state_skipped_flag = false;
                        } else {
                            allState.state_parser = STATE_XML_SKIP;
                        }
                    } else
                    if (!tag.ended) {
                        if (allState.isOpened) {
                            newParagraph();
                            //setParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                        }
                    } else {

                    }
                }
                return true;
            case AlFormatTag.TAG_SCRIPT:
            case AlFormatTag.TAG_STYLE:
            case AlFormatTag.TAG_TABLE_OF_CONTENT_SOURCE:
            case AlFormatTag.TAG_DESC: //???
            case AlFormatTag.TAG_SEQUENCE: //???
                if (active_type == AlOneZIPRecord.SPECIAL_NONE) {
                    if (tag.closed) {
                        if (allState.skip_count > 0)
                            allState.skip_count--;
                        if (allState.skip_count == 0) {
                            allState.state_skipped_flag = false;
                        } else {
                            allState.state_parser = STATE_XML_SKIP;
                        }
                    } else if (!tag.ended) {
                        allState.skip_count++;
                        allState.state_skipped_flag = true;
                        allState.state_parser = STATE_XML_SKIP;
                    } else {

                    }
                }
                return true;

            case AlFormatTag.TAG_A:
                if (tag.closed) {
                    if ((paragraph & AlStyles.STYLE_LINK) != 0)
                        clearTextStyle(AlStyles.STYLE_LINK);
                } else if (!tag.ended) {
                    if (addNotes())
                        setTextStyle(AlStyles.STYLE_LINK);
                } else {

                }
                return true;
            case AlFormatTag.TAG_BODY:
                if (active_type == AlOneZIPRecord.SPECIAL_NONE) {
                    if (tag.closed) {
                        allState.state_skipped_flag = true;
                        clearParagraphStyle(AlStyles.PAR_NOTE);
                        newParagraph();
                        setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                    } else if (!tag.ended) {
                        allState.state_skipped_flag = false;
                        allState.skip_count = 0;
                        if (allState.isOpened)
                            newParagraph();
                    } else {

                    }
                }
                return true;
            /*case AlFormatTag.TAG_BLOCKQUOTE:
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
                return true;*/
            /*case AlFormatTag.TAG_H1:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_TITLE);//
                    newParagraph();
                    newEmptyStyleParagraph();
                    if (allState.isOpened)
                        setSpecialText(false);
                } else if (!tag.ended) {
                    newParagraph();
                    newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                    setParagraphStyle(AlStyles.PAR_TITLE);
                    if (allState.isOpened) {
                        section_count = 0;
                        is_title = true;
                        content_start = size;
                        setSpecialText(true);
                    }
                } else {
                    newParagraph();
                    newEmptyStyleParagraph();
                }
                return true;
            case AlFormatTag.TAG_H:
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
                    setParagraphStyle(AlStyles.PAR_SUBTITLE);
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
                return true;*/
            /*case AlFormatTag.TAG_DIV:
            case AlFormatTag.TAG_DT:
            case AlFormatTag.TAG_DD:*/
            case AlFormatTag.TAG_P:
                //case AlFormatTag.TAG_FRAME:
                if (tag.closed) {
                    newParagraph();
                } else if (!tag.ended) {
                    newParagraph();
                } else {
                    newParagraph();
                    newEmptyTextParagraph();
                }
                return true;
            /*case AlFormatTag.TAG_TT:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_CODE);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_CODE);
                } else {

                }
                return true;*/
            case AlFormatTag.TAG_FRAME:
                doTextChar(' ', false);
                return true;
            case AlFormatTag.TAG_LIST_ITEM:
            case AlFormatTag.TAG_LI:
                /*if (tag.closed) {

                } else if (!tag.ended) {
                    if (allState.isOpened)
                        newParagraph();
                    addTextFromTag(listAdd0, true);
                } else {

                }*/
                if (tag.closed) {
                    decULNumber();
                } else
                if (!tag.ended) {
                    incULNumber();
                } else {

                }
                return true;
            case AlFormatTag.TAG_BOOKMARK:
            case AlFormatTag.TAG_BOOKMARK_START:
                if (tag.closed) {

                } else /*if (!tag.ended) {
                    if (allState.isOpened) {
                        param = tag.getATTRValue(AlFormatTag.TAG_NAME);
                        if (param != null) {
                            addtestLink(param.toString());
                        }
                    }
                } else */{
                    if (allState.isOpened) {
                        param = tag.getATTRValue(AlFormatTag.TAG_NAME);
                        if (param != null) {
                            addtestLink(param.toString());
                        }
                    }
                }
                return true;
            /*case AlFormatTag.TAG_TITLE:
                if (tag.closed) {

                } else if (!tag.ended) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0) {
                        is_book_title = true;
                        setSpecialText(true);
                    }
                } else {

                }
                return true;*/
            /*case AlFormatTag.TAG_SECTION:
            case AlFormatTag.TAG_BR:
                if (tag.closed) {

                } else if (!tag.ended) {
                    newParagraph();
                } else {
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                }
                return true;*/
            case AlFormatTag.TAG_IMAGE:
                if (tag.closed) {

                } else if (!tag.ended) {
                    addImages();
                } else {
                    addImages();
                }
                return true;
            case AlFormatTag.TAG_LINE_BREAK:
                if (tag.closed) {

                } else if (!tag.ended) {

                } else {
                    newParagraph();
                    newEmptyTextParagraph();
                }
                return true;
            case AlFormatTag.TAG_TAB:
            case AlFormatTag.TAG_S:
                if (tag.closed) {

                } else if (!tag.ended) {

                } else {
                    doTextChar(' ', false);
                }
                return true;
            case AlFormatTag.TAG_EXTFILE:
                if (tag.closed) {
                    if (allState.isOpened)
                        newParagraph();

                    active_file = UNKNOWN_FILE_SOURCE_NUM;
                    active_type = AlOneZIPRecord.SPECIAL_NONE;
                } else
                if (!tag.ended) {
                    param = tag.getATTRValue(AlFormatTag.TAG_NUMFILES);
                    if (param != null) {
                        active_file = InternalFunc.str2int(param, 10);
                    }

                    param = tag.getATTRValue(AlFormatTag.TAG_IDREF);
                    if (param != null) {
                        active_type = InternalFunc.str2int(param, 10);
                    }

                } else {

                }
                return true;
        }

                return false;
    }

}
