package com.neverland.engbook.level2;


import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlOneZIPRecord;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.InternalFunc;

import java.util.HashMap;

public class AlFormatFB3 extends AlAXML {

    private static final String LEVELE2_FB3_COVER_TYPE = "http://schemas.openxmlformats.org/package/2006/relationships/metadata/thumbnail";

    protected int active_file = UNKNOWN_FILE_SOURCE_NUM;
    protected int active_type = 0x00;

    private int section_count;

    private int image_start;
    private int image_stop;
    private String image_name;
    private int content_start;

    private boolean isGenre;
    private boolean isAuthor;
    private boolean isAuthorFirst;
    private boolean isAuthorLast;
    private boolean isAuthorNick;
    private boolean isAuthorMiddle;
    private boolean isBookTitle;
    private boolean isProgramUsed;
    private boolean isTitle0;
    private boolean isTitle1;

    private String firstAuthor = null;
    private String middleAuthor = null;
    private String lastAuthor = null;
    private String nickAuthor = null;

    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref, AlStylesOptions stl) {
        allState.isOpened = true;

        xml_mode = true;
        ident = "FB2";

        aFiles = myParent;

        if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG) != 0)
            aFiles.needUnpackData();

        preference = pref;
        styles = stl;

        size = 0;
        noUseCover = bookOptions.noUseCover;

        autoCodePage = false;
        use_cpR0 = TAL_CODE_PAGES.CP65001;

        allState.state_parser = STATE_XML_SKIP;
        allState.state_skipped_flag = true;

        allState.state_parser = 0;
        parser(0, aFiles.getSize());
        newParagraph();

        allState.isOpened = false;
    }

    public static boolean isFB3(AlFiles a) {
        if (a.getIdentStr().contentEquals("fb3"))
            return true;
        return false;
    }

    public AlFormatFB3() {
        section_count = 0;
        image_start = -1;
        image_stop = -1;
        image_name = null;
        isGenre = false;
        isAuthor = false;
        isAuthorFirst = false;
        isAuthorLast = false;
        isAuthorNick = false;
        isAuthorMiddle = false;
        isBookTitle = false;
        isProgramUsed = false;
        isTitle0 = false;
        isTitle1 = false;
        content_start = 0;
        firstAuthor = null;
        middleAuthor = null;
        lastAuthor = null;
        nickAuthor = null;

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
                    target = aFiles.getAbsoluteName("/word/", sTarget.toString());
                }

                allId.put(sId.toString(), target);
            }
        } else
        if (active_type == AlOneZIPRecord.SPECIAL_CONTENT) {
            StringBuilder sId = tag.getATTRValue(AlFormatTag.TAG_TYPE);
            StringBuilder sTarget = tag.getATTRValue(AlFormatTag.TAG_TARGET);
            if (sId != null && sId.indexOf(LEVELE2_FB3_COVER_TYPE) == 0 && sTarget != null && sId.length() > 0 && sTarget.length() > 0) {
                if (sTarget.indexOf(":") == -1) {
                    coverName = aFiles.getAbsoluteName("/", sTarget.toString());
                }
            }
        }
    }

    @Override
    void doSpecialGetParagraph(long iType, int addon, long level, long[] stk, int[] cpl) {
        paragraph = iType;
        allState.state_parser = 0;
        active_type = addon & 0xffff;
        active_file = (addon >> 16) & 0xfff;
        paragraph_level = (int) (level & LEVEL2_MASK_FOR_LEVEL);
        paragraph_tag = (int) ((level >> 31) & 0xffffffff);
        allState.state_skipped_flag = (addon & LEVEL2_FRM_ADDON_SKIPPEDTEXT) != 0;
        allState.state_code_flag = (addon & LEVEL2_FRM_ADDON_CODETEXT) != 0;
        //allState.state_special_flag = (addon & LEVEL2_FRM_ADDON_SPECIALTEXT) != 0;
    }

    @Override
    void formatAddonInt() {
        pariType = paragraph;
        parAddon = active_type & 0xffff;
        parAddon += (active_file & 0xfff) << 16;

        parLevel = paragraph_level | (((long)paragraph_tag) << 31);

        if (allState.state_skipped_flag)
            parAddon += LEVEL2_FRM_ADDON_SKIPPEDTEXT;
        if (allState.state_code_flag)
            parAddon += LEVEL2_FRM_ADDON_CODETEXT;
		/*if (allState.state_special_flag)
			parAddon += LEVEL2_FRM_ADDON_SPECIALTEXT;*/
    }

    @Override
    boolean isNeedAttribute(int atr) {
        switch (atr) {
            case AlFormatTag.TAG_NUMBER:
            case AlFormatTag.TAG_NUMFILES:
            case AlFormatTag.TAG_CONTENT_TYPE:
            case AlFormatTag.TAG_TARGET:
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

    private void addSeries() {
        StringBuilder s = new StringBuilder();
        s.setLength(0);

        StringBuilder s1 = tag.getATTRValue(AlFormatTag.TAG_NAME);
        if (s1 != null)
            s.append(s1);

        s1 = tag.getATTRValue(AlFormatTag.TAG_NUMBER);
        if (s1 != null) {
            s.append(" \u2022 ");
            s.append(s1);
        }

        if (s.length() > 0) {
            if (allState.isOpened)
                bookSeries.add(s.toString());
            boolean saved2 = allState.state_skipped_flag;
            allState.state_skipped_flag = false;
            addTextFromTag(s, true);
            allState.state_skipped_flag = saved2;
        }
    }

    private void addtestImage() {
        if (allState.isOpened) {
            if (image_start > 0) {
                image_stop = tag.start_pos;
                im.add(AlOneImage.add(image_name, image_start, image_stop, AlOneImage.IMG_BASE64));
            }
        }
        image_start = -1;
    }

    private void setSpecialText(boolean flag) {
        if (flag) {
            allState.state_special_flag0 = true;
            state_specialBuff0.setLength(0);
        } else {

            if (isAuthorFirst) {
                firstAuthor = state_specialBuff0.toString();
                isAuthorFirst = false;
            } else
            if (isAuthorMiddle) {
                middleAuthor = state_specialBuff0.toString();
                isAuthorMiddle = false;
            } else
            if (isAuthorLast) {
                lastAuthor = state_specialBuff0.toString();
                isAuthorLast = false;
            } else
            if (isAuthorNick) {
                if (state_specialBuff0.length() > 0) {
                    nickAuthor = '\"' + state_specialBuff0.toString() + '\"';
                }
                isAuthorNick = false;
            } else
            if (isGenre) {
                if (allState.isOpened)
                    bookGenres.add(state_specialBuff0.toString());
                isGenre = false;
            } else
            if (isBookTitle) {
                if (allState.isOpened) {
                    bookTitle = state_specialBuff0.toString().trim();
                    addTestContent(bookTitle, section_count);
                }
                isBookTitle = false;
            } else
            if (isTitle0) {
                addTestContent(state_specialBuff0.toString().trim(), section_count);
                isTitle0 = false;
            } else
            if (isTitle1) {
                addTestContent(state_specialBuff0.toString().trim(), section_count + 1);
                isTitle1 = false;
            } else
            if (isProgramUsed) {
                if (program_used_position == -2) {
                    program_used_position = allState.start_position_par;
                    if (state_specialBuff0.indexOf(LEVEL2_PRGUSEDTEST) != -1)
                        program_used_position = -1;
                }
            }
            allState.state_special_flag0 = false;
        }
    }

    private void addAuthor() {
        StringBuilder s = new StringBuilder();
        s.setLength(0);

        if (lastAuthor != null)
            s.append(lastAuthor.trim());
        if (firstAuthor != null)
            if (s.length() == 0) s.append(firstAuthor.trim()); else {s.append(' '); s.append(firstAuthor.trim());}
        if (middleAuthor != null)
            if (s.length() == 0) s.append(middleAuthor.trim()); else {s.append(' '); s.append(middleAuthor.trim());}
        if (nickAuthor != null)
            if (s.length() == 0) s.append(nickAuthor.trim()); else {s.append(' '); s.append(nickAuthor.trim());}

        if (s.length() > 0) {
            bookAuthors.add(s.toString());
        }

        firstAuthor = null;
        middleAuthor = null;
        lastAuthor = null;
        nickAuthor = null;
    }

    private boolean addNotes() {
        StringBuilder s;

        s = tag.getATTRValue(AlFormatTag.TAG_HREF);

        if (s != null) {
            if (allState.isOpened) {
				/*if (s[0] == '#' && s.length() > 1) {
					//arr_usedlink.put(s.substr(1), true);
				} else {
					//arr_usedlink.put(s, true);
				}*/
            }

            addCharFromTag((char) AlStyles.CHAR_LINK_S, false);
            addTextFromTag(s, false);
            addCharFromTag((char) AlStyles.CHAR_LINK_E, false);
            return true;
        }

        return false;
    }

    private void testImage() {
        image_start = -1;
        if (allState.isOpened) {
            StringBuilder s1 = tag.getATTRValue(AlFormatTag.TAG_ID);
            if (s1 != null) {
                image_name = s1.toString();
                image_start = allState.start_position;
            }
        }
    }

    private boolean addImages() {
        StringBuilder s = tag.getATTRValue(AlFormatTag.TAG_HREF);

        if (s != null) {
            if ((paragraph & AlStyles.PAR_COVER) != 0) {
                if (s.length() > 0 && s.charAt(0) == '#')
                    s.delete(0, 1);
                coverName = s.toString();
            } else {
                addCharFromTag((char)AlStyles.CHAR_IMAGE_S, false);
                addTextFromTag(s, false);
                addCharFromTag((char)AlStyles.CHAR_IMAGE_E, false);

                s = tag.getATTRValue(AlFormatTag.TAG_TITLE);
                if (s != null) {
                    addCharFromTag((char)AlStyles.CHAR_TITLEIMG_START, false);
                    addTextFromTag(s, false);
                    addCharFromTag((char)AlStyles.CHAR_TITLEIMG_STOP, false);
                }
            }
            return false;
        }
        return false;
    }

    @Override
    protected boolean externPrepareTAG() {
        StringBuilder param;

        if (allState.isOpened && tag.tag != AlFormatTag.TAG_BINARY) {
            param = tag.getATTRValue(AlFormatTag.TAG_ID);
            if (param != null)
                addtestLink(param.toString());
        }


        switch (tag.tag) {
            case AlFormatTag.TAG_RELATIONSHIP:
                if (tag.closed) {

                } else
                if (!tag.ended) {
                    addRelationShip();
                } else {
                    addRelationShip();
                }
                return true;
            case AlFormatTag.TAG_EXTFILE:
                if (tag.closed) {
                    closeOpenNotes();
                    active_file = 0xfff;
                    clearParagraphStyle(AlStyles.PAR_NOTE);
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                } else
                if (!tag.ended) {
                    param = tag.getATTRValue(AlFormatTag.TAG_IDREF);
                    if (param != null) {
                        switch (InternalFunc.str2int(param, 10)) {
                            case AlOneZIPRecord.SPECIAL_FIRST:
                                active_type = AlOneZIPRecord.SPECIAL_FIRST;

                                if (coverName.length() > 0 && !noUseCover) {
                                    boolean bs = allState.state_skipped_flag;
                                    if (allState.isOpened) {
                                        allState.state_skipped_flag = false;
                                        newParagraph();
                                        setParagraphStyle(AlStyles.PAR_COVER);
                                    }
                                    addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                                    addTextFromTag(LEVEL2_COVERTOTEXT_STR, false);
                                    addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
                                    if (allState.isOpened) {
                                        newParagraph();
                                        allState.state_skipped_flag = bs;
                                        clearParagraphStyle(AlStyles.PAR_COVER);
                                    }
                                }

                                break;
                            case AlOneZIPRecord.SPECIAL_CONTENT:
                                active_type = AlOneZIPRecord.SPECIAL_CONTENT;
                                break;
                            default:
                                active_type = AlOneZIPRecord.SPECIAL_NONE;
                                break;
                        }

                        param = tag.getATTRValue(AlFormatTag.TAG_NUMFILES);
                        if (param != null) {
                            active_file = InternalFunc.str2int(param, 10);
                        } else {
                            active_file = 0xfff;
                            active_type = AlOneZIPRecord.SPECIAL_NONE;
                        }
                    } else {
                        active_file = 0xfff;
                        active_type = AlOneZIPRecord.SPECIAL_NONE;
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_PERIODICAL:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {

                }
                return true;

// paragraph
            case AlFormatTag.TAG_DIV:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {

                }
                return true;
            case AlFormatTag.TAG_P:
                newParagraph();
                return true;
            case AlFormatTag.TAG_LI:
                if (tag.closed) {
                    newParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                } else {

                }
                return true;
            case AlFormatTag.TAG_BR:
            case AlFormatTag.TAG_EMPTY_LINE:
                newParagraph();
                newEmptyTextParagraph();
                return true;
            case AlFormatTag.TAG_PRE:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_PRE);
                    newParagraph();
                    allState.state_code_flag = false;
                } else
                if (!tag.ended) {
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_PRE);
                    allState.state_code_flag = true;
                } else {

                }
                return true;
            case AlFormatTag.TAG_SUBTITLE:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_SUBTITLE);
                    newParagraph();
                    newEmptyStyleParagraph();
                    if (allState.isOpened && (tune & 0x01) != 0x00)
                        setSpecialText(false);
                } else
                if (!tag.ended) {
                    newParagraph();
                    newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_SUBTITLE);
                    if (allState.isOpened && (tune & 0x01) != 0) {
                        isTitle1 = true;
                        content_start = size;
                        setSpecialText(true);
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_TITLE:
                if (active_type == AlOneZIPRecord.SPECIAL_CONTENT) {
                    if (tag.closed) {

                    } else
                    if (!tag.ended) {

                    } else {

                    }
                } else {
                    if (tag.closed) {
                        clearParagraphStyle(AlStyles.PAR_TITLE);// | AlStyles.PAR_BREAKPAGE);
                        newParagraph();
                        newEmptyStyleParagraph();
                        if (allState.isOpened)
                            setSpecialText(false);
                    } else
                    if (!tag.ended) {
                        newParagraph();
                        newEmptyStyleParagraph();
                        setParagraphStyle(AlStyles.PAR_TITLE);
                        if (allState.isOpened) {
                            isTitle0 = true;
                            content_start = size;
                            setSpecialText(true);
                        }
                    } else {

                    }
                }
                return true;
            case AlFormatTag.TAG_ANNOTATION:
                if (tag.closed) {
                    if (active_type == AlOneZIPRecord.SPECIAL_CONTENT) {
                        allState.state_skipped_flag = true;
                    }
                    clearParagraphStyle(AlStyles.PAR_ANNOTATION);
                    newParagraph();
                    newEmptyStyleParagraph();
                } else
                if (!tag.ended) {
                    if (active_type == AlOneZIPRecord.SPECIAL_CONTENT)
                        allState.state_skipped_flag = false;
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_ANNOTATION);
                } else {

                }
                return true;
            case AlFormatTag.TAG_EPIGRAPH:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_EPIGRAPH);
                    newParagraph();
                    newEmptyStyleParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_EPIGRAPH);
                } else {

                }
                return true;
            case AlFormatTag.TAG_POEM:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_POEM);
                    newParagraph();
                    newEmptyStyleParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                    newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_POEM);
                } else {

                }
                return true;
            case AlFormatTag.TAG_V:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_V);
                } else
                if (!tag.ended) {
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_V);
                } else {
                    newParagraph();
                }
                return true;
            case AlFormatTag.TAG_STANZA:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_STANZA);
                    newParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                    newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_STANZA);
                } else {

                }
                return true;
            case AlFormatTag.TAG_LANG:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {

                }
                return true;
            case AlFormatTag.TAG_WRITTEN:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {

                }
                return true;
            case AlFormatTag.TAG_DATE:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {

                }
                return true;
            case AlFormatTag.TAG_SUBSCRIPTION:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_AUTHOR);
                    newParagraph();
                    newEmptyStyleParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                    if ((paragraph & (AlStyles.PAR_STANZA | AlStyles.PAR_POEM)) != 0)
                        newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_AUTHOR);
                } else {

                }
                return true;
            case AlFormatTag.TAG_CODE:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_CODE);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_CODE);
                } else {

                }
                return true;
            case AlFormatTag.TAG_BLOCKQUOTE:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_CITE);
                    newParagraph();
                    newEmptyStyleParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                    newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_CITE);
                } else {

                }
                return true;
// addon
            case AlFormatTag.TAG_IMG:
                if (tag.closed) {

                } else
                if (!tag.ended) {
                    addImages();
                } else {
                    addImages();
                }
                return true;

	/**/
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
// manage
            case AlFormatTag.TAG_SECTION:
                if (tag.closed) {
                    clearULNumber();
                    section_count--;
                    newParagraph();
                    if ((paragraph & AlStyles.PAR_NOTE) == 0)
                        setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                    closeOpenNotes();
                } else
                if (!tag.ended) {
                    section_count++;
                    newParagraph();
                    isFirstParagraph = true;
                } else {

                }
                return true;
            case AlFormatTag.TAG_NOTES:
                if (tag.closed) {
                    closeOpenNotes();
                    section_count = 0;
                    clearParagraphStyle(AlStyles.PAR_NOTE);
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                } else
                if (!tag.ended) {
                    content_start = size;
                    addTestContent("Notes", section_count);
                    setParagraphStyle(AlStyles.PAR_NOTE);
                    newParagraph();
                } else {

                }
                return true;
            case AlFormatTag.TAG_FB3_BODY:
                if (tag.closed) {
                    closeOpenNotes();
                    section_count = 0;
                    clearParagraphStyle(AlStyles.PAR_NOTE);
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                } else
                if (!tag.ended) {
                    section_count = 0;
                    allState.state_skipped_flag = false;
                    newParagraph();
                } else {

                }
                return true;
            case AlFormatTag.TAG_RELATIONS:
                if (tag.closed) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0)
                        clearParagraphStyle(AlStyles.PAR_DESCRIPTION2);
                } else
                if (!tag.ended) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0)
                        setParagraphStyle(AlStyles.PAR_DESCRIPTION2);
                } else {

                }
                return true;
            case AlFormatTag.TAG_SEQUENCE:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {

                }
                return true;
            case AlFormatTag.TAG_SUBJECT:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {

                }
                return true;
            case AlFormatTag.TAG_MAIN:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {

                }
                return true;
            case AlFormatTag.TAG_LINK:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {

                }
                return true;
            case AlFormatTag.TAG_CLASSIFICATION:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                }
                else {

                }
                return true;

	/*case TAG_GENRE:
		if (tag.closed) {
			if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0)
				setSpecialText(false);
		} else
		if (!tag.ended) {
			if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
				isGenre = true;
				setSpecialText(true);
			}
		} else {

		}
		return true;*/

            case AlFormatTag.TAG_FIRST_NAME:
                if (tag.closed) {
			/*if (isAuthor)
				setSpecialText(false);*/
                } else
                if (!tag.ended) {
			/*if (isAuthor) {
				isAuthorFirst = true;
				setSpecialText(true);
			}*/
                } else {

                }
                return true;
            case AlFormatTag.TAG_MIDDLE_NAME:
                if (tag.closed) {
			/*if (isAuthor)
				setSpecialText(false);*/
                } else
                if (!tag.ended) {
			/*if (isAuthor) {
				isAuthorMiddle = true;
				setSpecialText(true);
			}*/
                } else {

                }
                return true;
            case AlFormatTag.TAG_LAST_NAME:
                if (tag.closed) {
			/*if (isAuthor)
				setSpecialText(false);*/
                } else
                if (!tag.ended) {
			/*if (isAuthor) {
				isAuthorLast = true;
				setSpecialText(true);
			}*/
                } else {

                }
                return true;

///////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
// styles
            case AlFormatTag.TAG_B:
            case AlFormatTag.TAG_STRONG:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_BOLD);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_BOLD);
                }
                else {

                }
                return true;
            case AlFormatTag.TAG_I:
            case AlFormatTag.TAG_EM:
            case AlFormatTag.TAG_EMPHASIS:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_ITALIC);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_ITALIC);
                } else {

                }
                return true;
            case AlFormatTag.TAG_STRIKE:
            case AlFormatTag.TAG_DEL:
            case AlFormatTag.TAG_STRIKETHROUGH:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_STRIKE);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_STRIKE);
                } else {

                }
                return true;
            case AlFormatTag.TAG_S:
            case AlFormatTag.TAG_INS:
            case AlFormatTag.TAG_U:
            case AlFormatTag.TAG_UNDERLINE:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_UNDER);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_UNDER);
                } else {

                }
                return true;
            case AlFormatTag.TAG_SPACING:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_RAZR);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_RAZR);
                } else {

                }
                return true;
            case AlFormatTag.TAG_NOTE:
            case AlFormatTag.TAG_A:
                if (tag.closed) {
                    if ((paragraph & AlStyles.PAR_STYLE_LINK) != 0)
                        clearTextStyle(AlStyles.PAR_STYLE_LINK);
                } else
                if (!tag.ended) {
                    if (addNotes())
                        setTextStyle(AlStyles.PAR_STYLE_LINK);
                } else {

                }
                return true;
            case AlFormatTag.TAG_SUP:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_SUP);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_SUP);
                } else {

                }
                return true;
            case AlFormatTag.TAG_SUB:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_SUB);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_SUB);
                } else {

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
