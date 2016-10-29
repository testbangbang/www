package com.neverland.engbook.level2;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlFilesMOBI;
import com.neverland.engbook.util.AlOneLink;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.Base32Hex;
import com.neverland.engbook.util.InternalFunc;

public class AlFormatMOBI extends AlAXML {

    public static boolean isMOBI(AlFiles a) {
        //noinspection RedundantIfStatement
        if (a.getIdentStr().startsWith("mobi"))
            return true;
        return false;
    }

    @Override
    protected void prepareCustom() {

    }

    protected boolean 	is_content = false;
    protected int 		content_start = 0;
    protected int 		section_count = 0;

    private int         firstInsertCover = -1;

    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref, AlStylesOptions stl) {
        allState.isOpened = true;

        xml_mode = true;
        ident = "MOBI";

        aFiles = myParent;

        if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG) != 0)
            aFiles.needUnpackData();

        preference = pref;
        styles = stl;
        size = 0;

        autoCodePage = false;
        setCP(aFiles.getCodePage());

        if (((AlFilesMOBI)aFiles).getCover() != -1)
            coverName = Integer.toString(((AlFilesMOBI)aFiles).getCover());

        bookTitle = ((AlFilesMOBI)aFiles).getTitle();
        bookGenres.addAll(((AlFilesMOBI)aFiles).getGanres());
        bookAuthors.addAll(((AlFilesMOBI) aFiles).getAuthors());

        allState.state_parser = STATE_XML_SKIP;
        allState.state_skipped_flag = true;

        firstInsertCover = -1;
        parser(0, -1);
        newParagraph();

        convertLinkFromPosition();

        allState.isOpened = false;
    }

    protected final int findParagraphBySourcePos1(int start, int end, int pos) {
        final int tmp = (end + start) >> 1;
        final AlOneParagraph ap = par.get(tmp);

        if (ap.positionS >= pos) {

            if (tmp == 0)
                return ap.start;

            if (par.get(tmp - 1).positionS < pos)
                return ap.start;

            if (tmp == end)
                return  ap.start;

            return findParagraphBySourcePos1(start, tmp, pos);
        }

        if (tmp == start)
            return  ap.start;

        return findParagraphBySourcePos1(tmp, end, pos);
    }

    private void convertLinkFromPosition() {
		if (lnk != null && lnk.size() > 0) {
            for (int i = 0; i < lnk.size(); i++) {
                AlOneLink ap = lnk.get(i);

                if (ap.iType == 1)
                    continue;

                ap.positionE = ap.positionS;

                try {
                    ap.positionS = findParagraphBySourcePos1(0, par.size(), ap.positionS);
                } catch (Exception e) {
                    ap.positionS = 0;
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    protected  boolean addNotes() {
        String s = null;

        StringBuilder param = tag.getATTRValue(AlFormatTag.TAG_HREF);

        int val;
		/*if (s != null && s.startsWith("kindle:pos")) {
			val = s.lastIndexOf(':');
			if (val > 0)
				s = s.substring(val + 1);
			try {
				val = Integer.parseInt(s, 16);
				s = Integer.toString(val);
			} catch (Exception e) {
				s = null;
			}

			if (s != null) {
				addCharFromTag((char)AlStyles.CHAR_LINK_S, false);
                addTextFromTag(s, false);
                addCharFromTag((char) AlStyles.CHAR_LINK_E, false);

				if (isOpened) {
					addLink(AlLink.addLink(s, val, 0));
				}

				return true;
			}
		}*/

        if (param != null) {
            addCharFromTag((char)AlStyles.CHAR_LINK_S, false);
            addTextFromTag(param.toString(), false);
            addCharFromTag((char) AlStyles.CHAR_LINK_E, false);
            return true;
        }

        param = tag.getATTRValue(AlFormatTag.TAG_FILEPOS);
        if (param != null) {

            try {
                val = Integer.parseInt(param.toString());
            } catch (Exception e) {
                val = -1;
            }

            if (val >= 0) {
                addCharFromTag((char)AlStyles.CHAR_LINK_S, false);
                addTextFromTag(param.toString(), false);
                addCharFromTag((char) AlStyles.CHAR_LINK_E, false);

                addtestLink(param.toString(), val, 0);

                return true;
            }
        }

        return false;
    }


    @Override
    protected boolean isNeedAttribute(int atr) {
        switch (atr) {
            case AlFormatTag.TAG_NAME:
            case AlFormatTag.TAG_ID:
            case AlFormatTag.TAG_HREF:
            case AlFormatTag.TAG_TYPE:
            case AlFormatTag.TAG_ALIGN:
            case AlFormatTag.TAG_SRC:
            case AlFormatTag.TAG_NUMFILES:
            case AlFormatTag.TAG_REALFILE:
            case AlFormatTag.TAG_NOTE:
            case AlFormatTag.TAG_CHARSET:
            case AlFormatTag.TAG_CONTENT:
            case AlFormatTag.TAG_FILEPOS:
            case AlFormatTag.TAG_RECINDEX:
            case AlFormatTag.TAG_FILE_AS:
            case AlFormatTag.TAG_ROLE:
            case AlFormatTag.TAG_VALUE:
                return true;
        }
        return false;
    }


    protected boolean addImages() {
        StringBuilder param = tag.getATTRValue(AlFormatTag.TAG_SRC);

        if (param != null) {
            String s = param.toString();

            if (s.startsWith("kindle:embed:")/* || s.startsWith("kindle:flow:")*/) {
                /*s = s.substring(12);

                if (s.charAt(0) == ':')
                    s = s.substring(1);*/
                s = s.substring(13);

                int i = s.indexOf('?');
                if (i > 1)
                    s = s.substring(0, i);

                try {
                    int n = (int) Base32Hex.decode2int(s, false) - 1;
                    if (n >= 0) {
                        param.setLength(0);
                        param.append(Integer.toString(n));
                    } else {
                        param = null;
                    }
                } catch (Exception e) {
                    param = null;
                }
            }
        }

        //if (param == null)
        //    param = tag.getATTRValue(AlFormatTag.TAG_HREF);

        if (param == null) {
            param = tag.getATTRValue(AlFormatTag.TAG_RECINDEX);
            if (param != null) {
                int n = InternalFunc.str2int(param, 10) - 1;

                if (n >= 0) {
                    param.setLength(0);
                    param.append(Integer.toString(n));
                } else {
                    param = null;
                }
            }
        }

        if (param != null) {
            if ((paragraph & AlStyles.PAR_COVER) != 0) {
                String s = param.toString();
                if (s.length() > 0 && s.charAt(0) == '#')
                    s = s.substring(1);
                coverName = s;
            } else {
                addCharFromTag((char)AlStyles.CHAR_IMAGE_S, false);
                addTextFromTag(param.toString(), false);
                addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
            }
            return false;
        }

        return false;
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
            if (is_content) {
                addTestContent(state_specialBuff0.toString(), section_count);
                is_content = false;
            }
            allState.state_special_flag0 = false;
        }
    }

    @Override
    public boolean externPrepareTAG() {
        StringBuilder param;

        if (allState.isOpened &&
                (paragraph & (AlStyles.PAR_DESCRIPTION1 | AlStyles.PAR_DESCRIPTION2 | AlStyles.PAR_DESCRIPTION3 | AlStyles.PAR_DESCRIPTION4)) == 0) {
            param = tag.getATTRValue(AlFormatTag.TAG_ID);
            if (param != null)
                addtestLink(param.toString());
        }

        switch (tag.tag) {
            case AlFormatTag.TAG_SCRIPT:
            case AlFormatTag.TAG_STYLE:
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
                    allState.skip_count++;
                    allState.state_skipped_flag = true;
                    allState.state_parser = STATE_XML_SKIP;
                } else {

                }
                return true;

            case AlFormatTag.TAG_TITLE:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {

                }
                return true;
            case AlFormatTag.TAG_A:
                if (tag.closed) {
                    if ((paragraph & AlStyles.PAR_STYLE_LINK) != 0)
                        clearTextStyle(AlStyles.PAR_STYLE_LINK);
                } else
                if (!tag.ended) {
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
                    allState.state_skipped_flag = false;
                    clearParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                    newParagraph();
                } else
                if (!tag.ended) {
                    if (allState.isOpened) {
                        newParagraph();
                        setParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                    }
                    allState.state_skipped_flag = true;
                } else {

                }
                return true;
            case AlFormatTag.TAG_BODY:
                if (tag.closed) {
                    //state_skipped_flag = true;
                    clearParagraphStyle(AlStyles.PAR_NOTE);
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                } else
                if (!tag.ended) {
                    allState.state_skipped_flag = false;
                    allState.skip_count = 0;
                    newParagraph();
                } else {

                }
                return true;
            case AlFormatTag.TAG_BLOCKQUOTE:
            case AlFormatTag.TAG_CITE:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_CITE);
                    newParagraph();
                    newEmptyTextParagraph();
                } else
                if (!tag.ended) {
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
                } else
                if (!tag.ended) {
                    newParagraph();
                    newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                    setParagraphStyle(AlStyles.PAR_TITLE);// | AlStyles.PAR_BREAKPAGE);
                    if (allState.isOpened) {
                        section_count = 0;
                        is_content = true;
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
                } else
                if (!tag.ended) {
                    newParagraph();
                    newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_SUBTITLE);// | AlStyles.PAR_BREAKPAGE);
                    if (allState.isOpened) {
                        section_count = 1;
                        is_content = true;
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
                } else
                if (!tag.ended) {
                    newParagraph();
                } else {
                    newParagraph();
                    newEmptyTextParagraph();
                }
                return true;
            case AlFormatTag.TAG_TT:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_CODE);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_CODE);
                } else {

                }
                return true;
            case AlFormatTag.TAG_B:
            case AlFormatTag.TAG_STRONG:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_BOLD);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_BOLD);
                } else {

                }
                return true;
            case AlFormatTag.TAG_I:
            case AlFormatTag.TAG_EM:
            case AlFormatTag.TAG_DFM:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_ITALIC);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_ITALIC);
                } else {

                }
                return true;
            case AlFormatTag.TAG_U:
            case AlFormatTag.TAG_S:
            case AlFormatTag.TAG_INS:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_UNDER);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_UNDER);
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
            case AlFormatTag.TAG_SPAN:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_NATIVEJUST | AlStyles.SL_JUST_MASK);
                } else
                if (!tag.ended) {

                } else {
                    clearParagraphStyle(AlStyles.PAR_NATIVEJUST | AlStyles.SL_JUST_MASK);
                }
                return true;
            case AlFormatTag.TAG_STRIKE:
            case AlFormatTag.TAG_DEL:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_STRIKE);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_STRIKE);
                } else {

                }
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
            case AlFormatTag.TAG_HTML:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                    if (coverName != null) {
                        if (allState.isOpened) {
                            if (firstInsertCover == -1) {
                                firstInsertCover = allState.start_position;

                                boolean bs = allState.state_skipped_flag;
                                allState.state_skipped_flag = false;
                                newParagraph();
                                setParagraphStyle(AlStyles.PAR_COVER);
                                addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                                addCharFromTag(LEVEL2_COVERTOTEXT, false);
                                addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
                                newParagraph();
                                allState.state_skipped_flag = bs;
                                setParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                                clearParagraphStyle(AlStyles.PAR_COVER);
                            }
                        } else {
                            if (firstInsertCover == allState.start_position) {
                                addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                                addCharFromTag(LEVEL2_COVERTOTEXT, false);
                                addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
                            }
                        }
                    }

                    /*if (*//*coverName != null && *//*(allState.isOpened && firstInsertCover == -1) || (!allState.isOpened && firstInsertCover == allState.start_position)) {

                        if (firstInsertCover == -1)
                            firstInsertCover = allState.start_position;

                        final
                        if (allState.isOpened) {
                            allState.state_skipped_flag = false;
                            newParagraph();
                            setParagraphStyle(AlStyles.PAR_COVER);
                            addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                            addCharFromTag(LEVEL2_COVERTOTEXT, false);
                            addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
                            newParagraph();
                            allState.state_skipped_flag = bs;
                            setParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                            clearParagraphStyle(AlStyles.PAR_COVER);
                        } else {
                            if (coverName != null) {
                                addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                                addCharFromTag(LEVEL2_COVERTOTEXT, false);
                                addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
                            } else {
                                addTextFromTag("" + (char) 0xa0 + (char) 0xa0 + (char) 0xa0, false);
                            }
                        }
                    }*/
                } else {

                }
                return true;
            case AlFormatTag.TAG_HR:
            case AlFormatTag.TAG_BR:
                if (tag.closed) {

                } else
                if (!tag.ended) {
                    newParagraph();
                } else {
                    newParagraph();
                }
                return true;
            case AlFormatTag.TAG_IMG:
                if (tag.closed) {

                } else
                if (!tag.ended) {
                    addImages();
                } else {
                    addImages();
                }
                return true;
            case AlFormatTag.TAG_PAGEBREAK:
                if (tag.closed) {

                } else
                if (!tag.ended) {
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                } else {
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                }
                return true;
        }

        return false;
        ///////////////////////////////////////////////////////

        /*if (tag.closed) {
            switch (tag.tag) {
                case AlFormatTag.TAG_SCRIPT:
                case AlFormatTag.TAG_STYLE:
                    if (allState.skip_count > 0)
                        allState.skip_count--;
                    if (allState.skip_count == 0) {
                        allState.state_skipped_flag = false;
                    } else {
                        allState.state_parser = STATE_XML_SKIP;
                    }
                    return true;

                case AlFormatTag.TAG_TITLE:

                    return true;

                case AlFormatTag.TAG_A:
                    if ((paragraph & AlStyles.PAR_STYLE_LINK) != 0)
                        clearTextStyle(AlStyles.PAR_STYLE_LINK);
                    return true;

                case AlFormatTag.TAG_HEAD:
                    allState.state_skipped_flag = false;
                    clearParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                    newParagraph();
                    return true;
                case AlFormatTag.TAG_BODY:
                    //state_skipped_flag = true;
                    clearParagraphStyle(AlStyles.PAR_NOTE);
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                    return true;

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

                case AlFormatTag.TAG_SUP:
                    clearTextStyle(AlStyles.PAR_STYLE_SUP);
                    return true;
                case AlFormatTag.TAG_SUB:
                    clearTextStyle(AlStyles.PAR_STYLE_SUB);
                    return true;

                case AlFormatTag.TAG_UL:
                case AlFormatTag.TAG_OL:
                    decULNumber();
                    return true;

                case AlFormatTag.TAG_SPAN:
                    clearParagraphStyle(AlStyles.PAR_NATIVEJUST | AlStyles.SL_JUST_MASK);
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
            param = tag.getATTRValue(AlFormatTag.TAG_ID);
            if (param != null)
                addtestLink(param.toString());

            if (tag.ended == false) {
                switch (tag.tag) {
                    case AlFormatTag.TAG_HTML:
                        if (coverName != null && (firstInsertCover == -1 || firstInsertCover == allState.start_position)) {

                            if (firstInsertCover == -1)
                                firstInsertCover = allState.start_position;

                            final boolean bs = allState.state_skipped_flag;
                            if (allState.isOpened) {
                                allState.state_skipped_flag = false;
                                newParagraph();
                                setParagraphStyle(AlStyles.PAR_COVER);
                                addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                                addCharFromTag(LEVEL2_COVERTOTEXT, false);
                                addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
                                newParagraph();
                                allState.state_skipped_flag = bs;
                                setParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                                clearParagraphStyle(AlStyles.PAR_COVER);
                            } else {
                                if (coverName != null) {
                                    addCharFromTag((char)AlStyles.CHAR_IMAGE_S, false);
                                    addCharFromTag(LEVEL2_COVERTOTEXT, false);
                                    addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
                                } else {
                                    addTextFromTag("" + (char)0xa0 + (char)0xa0 + (char)0xa0, false);
                                }
                            }
                        }
                        break;
                    case AlFormatTag.TAG_SCRIPT:
                    case AlFormatTag.TAG_STYLE:
                        allState.skip_count++;
                        allState.state_skipped_flag = true;
                        allState.state_parser = STATE_XML_SKIP;
                        return true;

                    case AlFormatTag.TAG_UL:
                    case AlFormatTag.TAG_OL:
                        incULNumber();
                        return true;

                    case AlFormatTag.TAG_LI:
                        newParagraph();
                        //addTextFromTag(listAdd0, true);
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


                        return true;
                    case AlFormatTag.TAG_HEAD:

                        if (allState.isOpened) {
                            newParagraph();
                            setParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                        }
                        allState.state_skipped_flag = true;
                        return true;
                    case AlFormatTag.TAG_BODY:
                        allState.state_skipped_flag = false;
                        allState.skip_count = 0;
                        newParagraph();
                        return true;

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
                            is_content = true;
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
                            is_content = true;
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
                    case AlFormatTag.TAG_B:
                    case AlFormatTag.TAG_STRONG:
                        setTextStyle(AlStyles.PAR_STYLE_BOLD);
                        return true;
                    case AlFormatTag.TAG_I:
                    case AlFormatTag.TAG_EM:
                    case AlFormatTag.TAG_DFM:
                        setTextStyle(AlStyles.PAR_STYLE_ITALIC);
                        return true;

                    case AlFormatTag.TAG_SUP:
                        setTextStyle(AlStyles.PAR_STYLE_SUP);
                        return true;
                    case AlFormatTag.TAG_SUB:
                        setTextStyle(AlStyles.PAR_STYLE_SUB);
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
                    case AlFormatTag.TAG_PAGEBREAK:
                        newParagraph();
                        setParagraphStyle(AlStyles.PAR_BREAKPAGE);
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

                    case AlFormatTag.TAG_SPAN:
                        clearParagraphStyle(AlStyles.PAR_NATIVEJUST | AlStyles.SL_JUST_MASK);
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

                    case AlFormatTag.TAG_PAGEBREAK:
                        newParagraph();
                        setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                        return true;
                }
            }
        }
        return false;*/
    }

}
