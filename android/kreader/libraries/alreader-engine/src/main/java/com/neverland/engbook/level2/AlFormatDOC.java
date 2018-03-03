package com.neverland.engbook.level2;

import com.neverland.engbook.allstyles.AlCSSHtml;
import com.neverland.engbook.allstyles.AlCSSStyles;
import com.neverland.engbook.allstyles.AlOneCSS;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlFilesDOC;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.unicode.CP932;
import com.neverland.engbook.unicode.CP936;
import com.neverland.engbook.unicode.CP949;
import com.neverland.engbook.unicode.CP950;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlOneLink;
import com.neverland.engbook.util.AlOneXMLAttrClass;
import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;

@SuppressWarnings("ConstantConditions")
public class AlFormatDOC extends AlFormat {

    private static final String NOTEFORMAT = "_@%d@@@%d@";

    public static boolean isDOC(AlFiles a) {
        //noinspection RedundantIfStatement
        if (a.getIdentStr().contentEquals("doc"))
            return true;
        return false;
    }

    AlFilesDOC aDoc = null;
    private final StringBuilder			titles = new StringBuilder();
    private int 					section_count = 0;
    private boolean is_hidden = false;
    static final int FRM_DOC_HIDDEN	=	0x80000000;
    private static final int STATE_LINKNO = 0;
    private static final int STATE_LINK13 = 1;
    private static final int STATE_LINK14 = 2;

    AlCSSStyles			cssStyles = null;
    public AlFormatDOC() {
        cssStyles = new AlCSSHtml();
    }

    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref)  {

        super.initState(bookOptions, myParent, pref);

        ident = "DOC";

        aDoc = (AlFilesDOC)myParent;

        if (aDoc.isUnicode()) {
            autoCodePage = false;
            setCP(TAL_CODE_PAGES.CP1200);
        } else {
            autoCodePage = bookOptions.codePage == TAL_CODE_PAGES.AUTO;
            if (autoCodePage) {
                //setCP(aDoc.getCodePage());
                int cp = aFiles.getCodePage();
                if (cp == 1252) {
                    int c = getBOMCodePage(false, false, false, true);
                    if (c != TAL_CODE_PAGES.AUTO)
                        cp = c;
                }
                setCP(cp);
            } else {
                setCP(bookOptions.codePage);
            }
        }

        cssStyles.init(this, TAL_CODE_PAGES.CP65001, AlCSSHtml.CSSHTML_SET_EMPTY, pref.cssSupportLevel/*-AlEngineOptions.CSS_SUPPORT_FONT_SIZE*/);
        if ((bookOptions.formatOptions & AlFiles.BOOKOPTIONS_DISABLE_CSS) != 0)
            cssStyles.disableExternal = true;

        titles.setLength(0);
        is_hidden = false;
        section_count = 0;
        allState.state_parser = 0;
        customSize = 0;
        parser(0, aFiles.getSize());
        newParagraph();

        convertLinkFromPosition(aDoc.isUnicode());
    }

    private void convertLinkFromPosition(boolean isUnicode) {
        if (lnk.size() > 0) {
            for (int i = 0; i < lnk.size(); i++) {
                AlOneLink ap = lnk.get(i);

                if (ap.iType == 1)
                    break;

                lnk.get(i).positionE = ap.positionS;
                lnk.get(i).positionS = findParagraphPositionBySourcePos(0, par0.size(), isUnicode ? ap.positionS << 1 : ap.positionS);
            }
        }
    }

    @Override
    protected void doTextChar(char ch, boolean addSpecial) {
        if (allState.skipped_flag > 0) {
            if (allState.state_special_flag && addSpecial)
                specialBuff.add(ch);
        } else {
            if (allState.state_code_flag && addSpecial)
                appendTitle(ch);

            if (parText.length > 0) {
                if (ch == 0xad) {
                    softHyphenCount++;
                } else
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

    @Override
    protected void newParagraph() {
        if (parText.length != 0) {

        } else {
            setPropStyle(AlParProperty.SL2_EMPTY_BEFORE);
        }

        super.newParagraph();

        if (allState.state_code_flag)
            appendTitle(' ');
    }


    @Override
    protected void newEmptyTextParagraph() {
        super.newEmptyTextParagraph();
        if (allState.state_code_flag)
            appendTitle(' ');
    }

    private void appendTitle(char ch) {
        if (ch == 0x20 &&
                ((titles.length() == 0) ||
                        (titles.length() > 0 && titles.charAt(titles.length() - 1) == 0x20)))
            return;
        titles.append(ch);
        if (titles.length() > EngBookMyType.AL_WORD_LEN) {
            titles.append((char)0x2026);
            insertTitle(section_count);
        }
    }

    private void insertTitle(int cnt) {
        String s = titles.toString().trim();
        if (s.length() == 0)
            s = "\u2026";
        addContent(AlOneContent.add(s, allState.start_position_par, cnt));
        allState.state_code_flag = false;
    }

    private void prepareLink(char ch) {
        switch (ch) {
            case 0x13:
                if (parText.length == 0)
                    addCharFromTag((char)0xa0, false);

                switch (allState.state_parser) {
                    case STATE_LINK14:
                        clearTextStyle(AlStyles.STYLE_LINK);
                    default:
                        allState.incSkipped();
                        allState.state_special_flag = true;
                        specialBuff.clear();
                        allState.state_parser = STATE_LINK13;
                }
                break;
            case 0x14:
                switch (allState.state_parser) {
                    case STATE_LINKNO:
                    case STATE_LINK14:
                        break;
                    default:
                        allState.decSkipped();
                        allState.state_special_flag = false;

                        String slink = getHyperLink(specialBuff.buff.toString());
                        if (slink.length() > 0) {
                            addCharFromTag((char)AlStyles.CHAR_LINK_S, false);
                            addTextFromTag(slink, false);
                            addCharFromTag((char)AlStyles.CHAR_LINK_E, false);
                            setTextStyle(AlStyles.STYLE_LINK);
                            allState.state_parser = STATE_LINK14;


                                Integer it = aDoc.bookmarks != null ?
                                        aDoc.bookmarks.get(slink) : null;
                                if (it != null) {
                                    lnk.add(AlOneLink.add(slink, it, 0));
                                }


                            break;
                        }

                        /*String s = state_specialBuff0.toString().trim();
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

                                    addCharFromTag((char)AlStyles.CHAR_LINK_S, false);
                                    addTextFromTag(s2, false);
                                    addCharFromTag((char)AlStyles.CHAR_LINK_E, false);
                                    setTextStyle(AlStyles.STYLE_LINK);
                                    allState.state_parser = STATE_LINK14;

                                    if (allState.isOpened) {
                                        Integer it = aDoc.bookmarks != null ?
                                                aDoc.bookmarks.get(s2.toString()) : null;
                                        if (it != null) {
                                            lnk.add(AlOneLink.add(s2.toString(), it, 0));
                                        }
                                    }

                                    break;
                                }
                            }
                        }*/
                        allState.state_parser = STATE_LINKNO;
                }
                break;
            case 0x15:
                switch (allState.state_parser) {
                    case STATE_LINKNO:
                    case STATE_LINK13:
                        allState.decSkipped();
                        allState.state_special_flag = false;
                        break;
                    default:
                        clearTextStyle(AlStyles.STYLE_LINK);
                }
                allState.state_parser = STATE_LINKNO;
                break;
        }
    }

    private boolean prev_tag_special = false;
    private int prev_format_special = 0;
    private int prev_font_size = 0;
    private final AlOneXMLAttrClass cls = new AlOneXMLAttrClass();


    @Override
    protected void parser(int start_pos, int stop_pos) {
        // this code must be in any parser without change!!!
        int 	buf_cnt;
        char 	ch, ch1;

        for (int i = start_pos, j; i < stop_pos;) {
            if (isBookLoadingAborted()) {
                return;
            }

            buf_cnt = AlFiles.LEVEL1_FILE_BUF_SIZE;
            if (i + buf_cnt > stop_pos) {
                buf_cnt = aFiles.getByteBuffer(i, parser_inBuff, stop_pos - i + 2);
                if (buf_cnt > stop_pos - i)
                    buf_cnt = stop_pos - i;
            } else {
                buf_cnt = aFiles.getByteBuffer(i, parser_inBuff, buf_cnt + 2);
                buf_cnt -= 2;
            }

            for (j = 0; j < buf_cnt;) {
                if (isBookLoadingAborted()) {
                    return;
                }

                allState.start_position = i + j;

                ch = (char)parser_inBuff[j++];
                ch &= 0xff;
                //if (ch >= 0x80) {
                    switch (use_cpR0) {
                        case TAL_CODE_PAGES.CP65001:
                            if ((ch & 0x80) == 0) { } else
                            if ((ch & 0x20) == 0) {
                                ch = (char)((ch & 0x1f) << 6);
                                ch1 = (char)parser_inBuff[j++];
                                ch += (char)(ch1 & 0x3f);
                            } else {
                                ch = (char)((ch & 0x1f) << 6);
                                ch1 = (char)parser_inBuff[j++];
                                ch += (char)(ch1 & 0x3f);
                                ch <<= 6;
                                ch1 = (char)parser_inBuff[j++];
                                ch += (char)(ch1 & 0x3f);
                            }
                            break;
                        case TAL_CODE_PAGES.CP1201:
                            ch <<= 8;
                            ch1 = (char)parser_inBuff[j++];
                            ch |= ch1 & 0xff;
                            break;
                        case TAL_CODE_PAGES.CP1200:
                            ch1 = (char)parser_inBuff[j++];
                            ch |= ch1 << 8;
                            break;
                        case 932:
                            if (ch > 0x80) {
                                switch (ch) {
                                    case 0x80:
                                    case 0xfd:
                                    case 0xfe:
                                    case 0xff:
                                        ch = 0x0000;
                                        break;
                                    default:
                                        if (ch >= 0xa1 && ch <= 0xdf) {
                                            ch = (char) (ch + 0xfec0);
                                            break;
                                        }
                                        ch1 = (char) (parser_inBuff[j++] & 0xff);
                                        ch = (ch1 >= 0x40 && ch1 <= 0xfc) ? CP932.getChar(ch, ch1) : 0x00;
                                        break;
                                }
                            }
                            break;
                        case 936:
                            if (ch > 0x80) {
                                switch (ch) {
                                    case 0x80:
                                        ch = 0x20AC;
                                        break;
                                    case 0xff:
                                        ch = 0x0000;
                                        break;
                                    default:
                                        ch1 = (char) (parser_inBuff[j++] & 0xff);
                                        ch = (ch1 >= 0x40 && ch1 <= 0xfe) ? CP936.getChar(ch, ch1) : 0x00;
                                        break;
                                }
                            }
                            break;
                        case 949:
                            if (ch > 0x80) {
                                switch (ch) {
                                    case 0x80:
                                    case 0xff:
                                        ch = 0x0000;
                                        break;
                                    default:
                                        ch1 = (char) (parser_inBuff[j++] & 0xff);
                                        ch = (ch1 >= 0x41 && ch1 <= 0xfe) ? CP949.getChar(ch, ch1) : 0x00;
                                        break;
                                }
                            }
                            break;
                        case 950:
                            if (ch > 0x80) {
                                switch (ch) {
                                    case 0x80:
                                    case 0xff:
                                        ch = 0x0000;
                                        break;
                                    default:
                                        ch1 = (char) (parser_inBuff[j++] & 0xff);
                                        ch = (ch1 >= 0x40 && ch1 <= 0xfe) ? CP950.getChar(ch, ch1) : 0x00;
                                        break;
                                }
                            }
                            break;

                        default:
                            if (ch > 0x80)
                                ch = data_cp[ch - 0x80];
                            break;
                    }
                //}
                if ((ch & AlStyles.STYLE_MASK_4CODECONVERT) == AlStyles.STYLE_BASE_4CODECONVERT)
                    ch = 0x00;

                // end must be code
                /////////////////// Begin Real Parser

                if (allState.start_position == 0 ||
                        allState.start_position < aDoc.format.start ||
                                allState.start_position >= aDoc.format.limit) {



                    //aDoc.getFormat(allState.start_position == 0 ? 0 : allState.start_position - (aDoc.isUnicode() ? 2 : 1));
                    //int prev_special = aDoc.format.getSpecial();
                    aDoc.getFormat(allState.start_position);
                    int real_format = aDoc.format.getFormat();
                    int real_special = aDoc.format.getSpecial();

                    styleStack.clearTo(1);

                    if ((real_format & AlFilesDOC.Format.STYLE_NEWPAR) != 0) {
                        styleStack.clearTo(0);

                        allState.start_position_par = allState.start_position;
                        if (parText.length > 0) {
                            newParagraph();
                        } else {
                            newEmptyTextParagraph();
                        }

                        cls.clear();
                        switch (aDoc.format.getLevel()) {
                            case 1:
                            case 2:
                                prev_tag_special = true;
                                styleStack.push(AlFormatTag.TAG_TITLE, cls);
                                cssStyles.apply1ClassX(AlFormatTag.TAG_TITLE, cls, styleStack);
                                newEmptyTextParagraph();
                                setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);

                                if ((cssStyles.supportLevel & AlEngineOptions.CSS_SUPPORT_JUSTIFY) != 0) {
                                    clearPropStyle(AlParProperty.SL2_JUST_MASK);
                                    switch (aDoc.format.getAlign()) {
                                        case AlFilesDOC.Format.LEFT:
                                            setPropStyle(AlParProperty.SL2_JUST_LEFT);
                                            break;
                                        case AlFilesDOC.Format.RIGHT:
                                            setPropStyle(AlParProperty.SL2_JUST_RIGHT);
                                            break;
                                        case AlFilesDOC.Format.CENTER:
                                            setPropStyle(AlParProperty.SL2_JUST_CENTER);
                                            break;
                                    }
                                }
                                break;
                            case 3:
                                prev_tag_special = true;
                                styleStack.push(AlFormatTag.TAG_SUBTITLE, cls);
                                cssStyles.apply1ClassX(AlFormatTag.TAG_SUBTITLE, cls, styleStack);

                                newEmptyTextParagraph();
                                setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);

                                if ((cssStyles.supportLevel & AlEngineOptions.CSS_SUPPORT_JUSTIFY) != 0) {
                                    clearPropStyle(AlParProperty.SL2_JUST_MASK);
                                    switch (aDoc.format.getAlign()) {
                                        case AlFilesDOC.Format.LEFT:
                                            setPropStyle(AlParProperty.SL2_JUST_LEFT);
                                            break;
                                        case AlFilesDOC.Format.RIGHT:
                                            setPropStyle(AlParProperty.SL2_JUST_RIGHT);
                                            break;
                                        case AlFilesDOC.Format.CENTER:
                                            setPropStyle(AlParProperty.SL2_JUST_CENTER);
                                            break;
                                    }
                                }
                                break;
                            default:
                                if (prev_tag_special)
                                    newEmptyTextParagraph();
                                prev_tag_special = false;

                                switch (aDoc.format.getAlign()) {
                                    case AlFilesDOC.Format.LEFT:
                                        cls.addClass(AlFormatTag.TAG_LEFT);
                                        break;
                                    case AlFilesDOC.Format.RIGHT:
                                        cls.addClass(AlFormatTag.TAG_RIGHT);
                                        break;
                                    case AlFilesDOC.Format.CENTER:
                                        cls.addClass(AlFormatTag.TAG_CENTER);
                                        break;
                                    default:
                                        break;
                                }

                                styleStack.push(AlFormatTag.TAG_P, cls);
                                cssStyles.apply1ClassX(AlFormatTag.TAG_P, cls, styleStack);
                        }

                        if (aDoc.format.getPageBreakBefore())
                            setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
                        if ((cssStyles.supportLevel & AlEngineOptions.CSS_SUPPORT_TEXT_INDENT) != 0)
                            setTextIndent(aDoc.format.getIndFirst());
                        if ((cssStyles.supportLevel & AlEngineOptions.CSS_SUPPORT_HORIZONTAL_MARGINS) != 0) {
                            setLMargin(aDoc.format.getIndLeft());
                            setRMargin(aDoc.format.getIndRight());
                        }
                        if ((cssStyles.supportLevel & AlEngineOptions.CSS_SUPPORT_VERTICAL_MARGINS) != 0) {
                            setTMargin(aDoc.format.getIndBefore());
                            setBMargin(aDoc.format.getIndAfter());
                        }

                    }

                    cls.clear();
                    styleStack.push(AlFormatTag.TAG_NOVALUE, cls);

                    is_hidden = (real_format & AlFilesDOC.Format.STYLE_HIDDEN) != 0;

                    int new_setstyle = real_format & DOCSTYLEMASK;
                    clearTextStyle((~new_setstyle) & DOCSTYLEMASK);
                    setTextStyle(new_setstyle);

                    int old_section_count = section_count;
                    section_count = aDoc.format.getLevel();

                    if (allState.state_code_flag && old_section_count != section_count && old_section_count > 0)
                        insertTitle(old_section_count);
                    if (section_count != 0 && old_section_count != section_count) {
                        allState.state_code_flag = true;
                        allState.start_position_par = size;
                        titles.setLength(0);
                    }

                    //
                    //clearParagraphStyle(AlStyles.SL_SIZE_MASK);
                    int new_size = aDoc.format.getFontSize() * 5;
                    //
                    if ((cssStyles.supportLevel & AlEngineOptions.CSS_SUPPORT_FONT_SIZE) != 0)
                        styleStack.setActualSize(new_size);
                    //
                    if (parText.length != 0 && prev_font_size != new_size)
                        doTextChar(getTextSize(), false);
                    prev_font_size = new_size;
                    //

                    if (prev_format_special != real_special) {
                        switch (prev_format_special) {
                            case 0x02: // FOOTREF
                            case 0x04: // ENDREF
                                clearTextStyle(AlStyles.STYLE_LINK);
                                break;

                            case 0x03: // FOOTTEXT
                            case 0x05: // ENDTEXT

                                break;
                        }
                        switch (real_special) {
                            case 0x02: // FOOTREF
                            case 0x04: // ENDREF
                                addTextFromTag((char)AlStyles.CHAR_LINK_S + String.format(NOTEFORMAT,
                                        real_special, aDoc.format.xnote) + (char)AlStyles.CHAR_LINK_E, false);

                                setTextStyle(AlStyles.STYLE_LINK);

                                if (ch == 0x02)
                                    addTextFromTag(String.format("{%d}", aDoc.format.xnote), false);
                                break;


                            case 0x05: // ENDTEXT
                                /*if (preference.onlyPopupFootnote)
                                    clearTextStyle(AlStyles.PAR_STYLE_HIDDEN);*/
                            case 0x03: // FOOTTEXT
                                closeOpenNotes();

                                //if (allState.isOpened)
                                    lnk.add(AlOneLink.add(String.format(NOTEFORMAT,
                                            real_special - 1, aDoc.format.xnote), size, 1));

                                if (preference.onlyPopupFootnote)
                                    setTextStyle(AlStyles.STYLE_HIDDEN);

                                if (ch == 0x02)
                                    addTextFromTag(String.format("%d ", aDoc.format.xnote), false);
                                break;
                        }

                        prev_format_special = real_special;
                    }
                }

                if (ch < 0x20) {
                    switch (ch) {
                        case 0x02:

                            break;
                        case 0x09:
                        case 0x08:
                        case 0x07:
                            doTextChar(' ', true);
                            break;
                        case 0x13:
                        case 0x14:
                        case 0x15:
                            prepareLink(ch);
                            break;
                        case 0x01:
                            if (allState.skipped_flag == 0 && aDoc.format.getSpecial() == 0x01) {
                                addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                                addTextFromTag(String.format("%d_%d", aDoc.format.xdata, aDoc.format.getFormat()), false);
                                addCharFromTag((char)AlStyles.CHAR_IMAGE_E, false);
                            }
                            break;
                        case 0x0b:
                            //if (allState.isOpened) {
                                if (parText.length > 0) {
                                    allState.start_position_par = allState.start_position;
                                    newParagraph();
                                }/* else {
						 newEmptyTextParagraph();
						 }*/
                           // }
                            break;
                        default:
					/*addTextFromTag(String.format("-0x%02x(%x,%x,%x)-", (int)ch,
					fdoc.format.xdata,
					fdoc.format.xnote,
					fdoc.format.special()), true);*/
                            break;
                    }
                }
                else {
                    if (!is_hidden)
                        switch (ch) {
                            case 0xf0b7:
                                doTextChar((char)0x2022, true);
                                break;
                            default:
                                doTextChar(ch, true);
                        }
                }


                /////////////////// End Real Parser
                // this code must be in any parser without change!!!
            }
            i += j;
        }
        newParagraph();
        // end must be cod
    }

    private void setTextIndent(int val) {
        val /= 20/* form 1440 to 72 */ * 6 /* pt to half em */;
        if (val < 0)
            val = 0;
        if (val > AlOneCSS.MARG_MAX_VALUE)
            val = (int) AlOneCSS.MARG_MAX_VALUE;
        clearPropStyle(AlParProperty.SL2_INDENT_MASK);
        if (val > 0)
            setPropStyle(AlParProperty.SL2_INDENT_EM | (((long)val) << AlParProperty.SL2_INDENT_SHIFT));
    }

    private void setLMargin(int val) {
        val /= 20/* form 1440 to 72 */ * 6 /* pt to half em */;
        if (val < 0)
            val = 0;
        if (val > AlOneCSS.MARG_MAX_VALUE)
            val = (int) AlOneCSS.MARG_MAX_VALUE;
        clearPropStyle(AlParProperty.SL2_MARGL_EM_MASK);
        if (val > 0)
            setPropStyle(((long)val) << AlParProperty.SL2_MARGL_EM_SHIFT);
    }

    private void setRMargin(int val) {
        val /= 20/* form 1440 to 72 */ * 6 /* pt to half em */;
        if (val < 0)
            val = 0;
        if (val > AlOneCSS.MARG_MAX_VALUE)
            val = (int) AlOneCSS.MARG_MAX_VALUE;
        clearPropStyle(AlParProperty.SL2_MARGR_EM_MASK);
        if (val > 0)
            setPropStyle(((long)val) << AlParProperty.SL2_MARGR_EM_SHIFT);
    }

    private void setTMargin(int val) {
        val /= 20/* form 1440 to 72 */ * 6 /* pt to half em */;
        if (val < 0)
            val = 0;
        if (val > AlOneCSS.MARG_MAX_VALUE)
            val = (int) AlOneCSS.MARG_MAX_VALUE;
        clearPropStyle(AlParProperty.SL2_MARGT_MASK);
        if (val > 0)
            setPropStyle(((long)val) << AlParProperty.SL2_MARGT_SHIFT);
    }

    private void setBMargin(int val) {
        val /= 20/* form 1440 to 72 */ * 6 /* pt to half em */;
        if (val < 0)
            val = 0;
        if (val > AlOneCSS.MARG_MAX_VALUE)
            val = (int) AlOneCSS.MARG_MAX_VALUE;
        clearPropStyle(AlParProperty.SL2_MARGB_MASK);
        if (val > 0)
            setPropStyle(((long)val) << AlParProperty.SL2_MARGB_SHIFT);
    }


    @Override
    public AlOneImage getImageByName(String name) {
        int i;
        if (im.size() > 0) {
            for (i = 0; i < im.size(); i++) {
                if (name.equalsIgnoreCase(im.get(i).name))
                    return im.get(i);
            }
        }

        AlOneImage a = new AlOneImage();
        a.name = name;
        aDoc.getExternalImage(a);
        im.add(a);

        return im.get(im.size() - 1);
    }

    private static final int DOCSTYLEMASK = (int) (AlStyles.STYLE_ITALIC | AlStyles.STYLE_BOLD | AlStyles.STYLE_UNDER |
                AlStyles.STYLE_STRIKE | AlStyles.STYLE_SUP | AlStyles.STYLE_SUB);
}
