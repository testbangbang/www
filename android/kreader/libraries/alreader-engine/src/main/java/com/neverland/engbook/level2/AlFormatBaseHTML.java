package com.neverland.engbook.level2;


import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;

public class AlFormatBaseHTML extends AlAXML {
    public String		currentFile;

    public int			lastBreakLineSize = 0;

    public AlFormatBaseHTML() {
        currentFile = EngBookMyType.AL_ROOT_RIGHTPATH_STR + "_";
    }

    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref, AlStylesOptions stl) {

    }

    public void addTestContent(String s, int level) {
        if (s == null)
            return;
        s = s.trim();
        if (s.length() == 0)
            return;

        if (!allState.isNoteSection)
            addContent(AlOneContent.add(s, allState.content_start, level));
    }

    public void setSpecialText(boolean flag) {
        if (flag) {
            allState.state_special_flag = true;
            specialBuff.clear();
        } else {

            if (specialBuff.isBookTitle) {
                bookTitle = specialBuff.buff.toString().trim();
                addTestContent(bookTitle, allState.section_count);
                specialBuff.isBookTitle = false;
            } else
            if (specialBuff.isTitle) {
                addTestContent(specialBuff.buff.toString().trim(), allState.section_count);
                specialBuff.isTitle0 = false;
            } else
            if (specialBuff.isCSSStyle) {
                cssStyles.parseBuffer(specialBuff.buff, currentFile);
                specialBuff.isCSSStyle = false;
            }

            allState.state_special_flag = false;
        }
    }

    @Override
    protected boolean externPrepareTAG() {

        switch (tag.tag) {
            case AlFormatTag.TAG_SCRIPT:
                if (tag.closed) {
                    allState.decSkipped();
                    if (allState.skipped_flag > 0) {
                        allState.state_parser = STATE_XML_SKIP;
                    }
                } else
                if (!tag.ended) {
                    allState.incSkipped();
                    allState.state_parser = STATE_XML_SKIP;
                } else {

                }
                return true;
            case AlFormatTag.TAG_STYLE:
                if (tag.closed) {
                    allState.decSkipped();

                    setSpecialText(false);
                } else
                if (!tag.ended) {
                    allState.incSkipped();

                    specialBuff.isCSSStyle = true;
                    setSpecialText(true);

                } else {

                }
                return true;
            case AlFormatTag.TAG_Q:
            case AlFormatTag.TAG_BLOCKQUOTE:
            case AlFormatTag.TAG_CITE:
                if (tag.closed) {
                    newParagraph();
                    newEmptyTextParagraph();
                } else if (!tag.ended) {
                    newParagraph();
                    newEmptyTextParagraph();
                    setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
                } else {

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
                    clearTextStyle(AlStyles.STYLE_CODE);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_CODE);
                } else {

                }
                return true;
            case AlFormatTag.TAG_SUP:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_SUP);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_SUP);
                } else {

                }
                return true;
            case AlFormatTag.TAG_SUB:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_SUB);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_SUB);
                } else {

                }
                return true;
            case AlFormatTag.TAG_B:
            case AlFormatTag.TAG_STRONG:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_BOLD);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_BOLD);
                } else {

                }
                return true;
            case AlFormatTag.TAG_I:
            case AlFormatTag.TAG_EM:
            case AlFormatTag.TAG_EMPHASIS:
            case AlFormatTag.TAG_DFM:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_ITALIC);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_ITALIC);
                } else {

                }
                return true;
            case AlFormatTag.TAG_U:
            case AlFormatTag.TAG_INS:
            case AlFormatTag.TAG_UNDERLINE:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_UNDER);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_UNDER);
                } else {

                }
                return true;
            case AlFormatTag.TAG_S:
            case AlFormatTag.TAG_STRIKE:
            case AlFormatTag.TAG_STRIKETHROUGH:
            case AlFormatTag.TAG_DEL:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_STRIKE);
                } else if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_STRIKE);
                } else {

                }
                return true;
            case AlFormatTag.TAG_A:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_LINK);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_LINK);
                } else {

                }
                return true;
            case AlFormatTag.TAG_SPACING:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_RAZR);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_RAZR);
                } else {

                }
                return true;
            case AlFormatTag.TAG_CODE:
                if (tag.closed) {
                    allState.state_code_flag = false;
                    clearTextStyle(AlStyles.STYLE_CODE);
                } else
                if (!tag.ended) {
                    setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH/* | AlStyles.SL_PRESERVE_SPACE*/);
                    setTextStyle(AlStyles.STYLE_CODE);
                    allState.state_code_flag = true;
                } else {

                }
                return true;
            case AlFormatTag.TAG_PRE:
                if (tag.closed) {
                    newParagraph();
                    allState.state_code_flag = false;
                } else if (!tag.ended) {
                    newParagraph();
                    setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH/* | AlStyles.SL_PRESERVE_SPACE*/);
                    //setPropStyle(AlParProperty.SL2_EMPTY_BEFORE | AlParProperty.SL2_EMPTY_AFTER);
                    allState.state_code_flag = true;
                } else {

                }
                return true;
            case AlFormatTag.TAG_UL:
            case AlFormatTag.TAG_OL:
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
            case AlFormatTag.TAG_EMPTY_LINE:
                if (tag.closed) {
                    newParagraph();
                    newEmptyTextParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                    newEmptyTextParagraph();
                } else {
                    newParagraph();
                    if (lastBreakLineSize == size)
                        newEmptyTextParagraph();
                    lastBreakLineSize = size;
                }
                return true;

            case AlFormatTag.TAG_TABLE:
            case AlFormatTag.TAG_TH:
            case AlFormatTag.TAG_TD:
            case AlFormatTag.TAG_TR:
                return prepareTable();
        }

        return false;
    }

}
