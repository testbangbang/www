package com.neverland.engbook.level2;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlOneTable;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;

public class AlFormatFB2 extends AlAXML {
	private static final int FB2_TEST_BUF_LENGTH = 1024;
	private static final String FB2_TEST_STR_1 = "<FictionBook";
	private static final String FB2_TEST_STR_2 = "<?xml";
	private static final int FB2_BODY_TEXT  = 0;
	private static final int FB2_BODY_NOTES = 1;
	private static final int FB2_BODY_COMMENT = 2;

	private static final boolean is_support_table = true;
	
	private int			section_count;

	private int			image_start;
	private int			image_stop;
	private String				image_name;
	private int			table_start;
	private int			table_stop;
	private int			table_present;
	private int 			content_start;
	
	private boolean			isGenre;
	private boolean			isAuthor;
	private boolean			isAuthorFirst;
	private boolean			isAuthorLast;
	private boolean			isAuthorNick;
	private boolean			isAuthorMiddle;
	private boolean			isBookTitle;
	private boolean			isProgramUsed;
	private boolean			isTitle0;
	private boolean			isTitle1;
	
	private String				firstAuthor = null;
	private String				middleAuthor = null;
	private String				lastAuthor = null;
	private String				nickAuthor = null;

	public static boolean isFB2(AlFiles a) {
		
		char[] buf_uc = new char[FB2_TEST_BUF_LENGTH];
		String s;		
		
		if (getTestBuffer(a, TAL_CODE_PAGES.CP1251, buf_uc, FB2_TEST_BUF_LENGTH)) {
			s = String.copyValueOf(buf_uc);

			if  (s.contains(FB2_TEST_STR_1) && s.contains(FB2_TEST_STR_2))
				return true;
		}

		if (getTestBuffer(a, TAL_CODE_PAGES.CP1200, buf_uc, FB2_TEST_BUF_LENGTH)) {
			s = String.copyValueOf(buf_uc);
			if  (s.contains(FB2_TEST_STR_1) && s.contains(FB2_TEST_STR_2))
				return true;
		}

		if (getTestBuffer(a, TAL_CODE_PAGES.CP1201, buf_uc, FB2_TEST_BUF_LENGTH)) {
			s = String.copyValueOf(buf_uc);
			if  (s.contains(FB2_TEST_STR_1) && s.contains(FB2_TEST_STR_2))
				return true;
		}
				
		return false;
	}

	public AlFormatFB2() {
		section_count = 0;
		image_start = -1;
		image_stop = -1;
		image_name = null;
		table_start = -1;
		table_stop = -1;
		table_present = 0;
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

	public void initState(AlBookOptions bookOptions, AlFiles myParent, 
			AlPreferenceOptions pref, AlStylesOptions stl) {
		allState.isOpened = true;

		xml_mode = true;
		ident = "FB2";

		aFiles = myParent;
		
		if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG) != 0)
			aFiles.needUnpackData();
		
		preference = pref;
		styles = stl;

		size = 0;
					
		autoCodePage = bookOptions.codePage == TAL_CODE_PAGES.AUTO;		
		if (autoCodePage) {
			setCP(getBOMCodePage(true, true, true, false));		
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

	boolean isNeedAttribute(int atr) {
		switch (atr) {		
		case AlFormatTag.TAG_NAME:
		case AlFormatTag.TAG_ID:
		case AlFormatTag.TAG_NUMBER:
		case AlFormatTag.TAG_HREF:
		case AlFormatTag.TAG_CONTENT_TYPE:
		case AlFormatTag.TAG_TYPE:
		case AlFormatTag.TAG_TITLE:
			return true;
		}
		return super.isNeedAttribute(atr);
	}

	@Override
	protected void prepareCustom() {

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

	private boolean addEndTableColumn(boolean mode) {
		if (mode) {
			addTextFromTag(" ║ ", false);
		} else {
			addTextFromTag(" | ", false);
		}
		return true;
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
			
			s.insert(0, (char)AlStyles.CHAR_LINK_S);
			s.append((char)AlStyles.CHAR_LINK_E);
			addTextFromTag(s, false);
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

	private int verifyBody() {
		StringBuilder s1 = tag.getATTRValue(AlFormatTag.TAG_NAME);
		if (s1 != null) {
			if (s1.toString().equalsIgnoreCase("notes"))
				return FB2_BODY_NOTES;
			if (s1.toString().equalsIgnoreCase("footnotes"))
				return FB2_BODY_NOTES;
			if (s1.toString().equalsIgnoreCase("comments"))
				return FB2_BODY_COMMENT;
		}
		return FB2_BODY_TEXT;
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
            case AlFormatTag.TAG_P:
            case AlFormatTag.TAG_LI:
                newParagraph();
                return true;

// styles

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
            case AlFormatTag.TAG_EMPHASIS:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_ITALIC);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_ITALIC);
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
            case AlFormatTag.TAG_CSTYLE:
                if (tag.closed) {
                    clearTextStyle(AlStyles.PAR_STYLE_CSTYLE);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.PAR_STYLE_CSTYLE);
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

// paragraph
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
                return true;
            case AlFormatTag.TAG_ANNOTATION:
                if (tag.closed) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0) {
                        allState.state_skipped_flag = true;
                    }
                    clearParagraphStyle(AlStyles.PAR_ANNOTATION);
                    newParagraph();
                    newEmptyStyleParagraph();
                } else
                if (!tag.ended) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0)
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
            case AlFormatTag.TAG_DATE:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_DATE);
                    newParagraph();
                    newEmptyStyleParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                    if ((paragraph & (AlStyles.PAR_STANZA | AlStyles.PAR_POEM)) != 0)
                        newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_DATE);
                } else {

                }
                return true;
            case AlFormatTag.TAG_TEXT_AUTHOR:
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
            case AlFormatTag.TAG_CITE:
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
            case AlFormatTag.TAG_EMPTY_LINE:
                newParagraph();
                newEmptyTextParagraph();
                return true;
// addon

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
            case AlFormatTag.TAG_IMAGE:
                if (tag.closed) {

                } else {
                    addImages();
                }
                return true;
            case AlFormatTag.TAG_TABLE:
                if (tag.closed) {
					//noinspection PointlessBooleanExpression
					if (!is_support_table) {
                        clearParagraphStyle(AlStyles.PAR_TABLE);
                        newParagraph();
                    } else {
                        if (allState.isOpened) {
                            table_present--;
                            if (table_present == 0 && table_start != -1) {
                                table_stop = tag.start_pos;
                                addTable(AlOneTable.add(table_start, table_start, table_stop));
                                table_start = -1;
                                allState.state_skipped_flag = false;
                            }
                            newParagraph();
                        }
                    }
                } else
                if (!tag.ended) {
                    newParagraph();
					//noinspection PointlessBooleanExpression
					if (!is_support_table) {
                        setParagraphStyle(AlStyles.PAR_TABLE);
                    } else {
                        String s1 = (char)AlStyles.CHAR_LINK_S + "table:" + Integer.toString(allState.start_position_par) + (char)AlStyles.CHAR_LINK_E;
                        StringBuilder s2 = tag.getATTRValue(AlFormatTag.TAG_TITLE);
                        String s3 = (char)AlStyles.CHAR_IMAGE_S + LEVEL2_TABLETOTEXT_STR + (char)AlStyles.CHAR_IMAGE_E;

                        if (allState.isOpened) {
                            table_present++;
                            if (table_present == 1) {
                                table_start = allState.start_position_par;
                                newEmptyTextParagraph();
                                setParagraphStyle(AlStyles.PAR_NATIVEJUST | AlStyles.SL_JUST_CENTER);
                                addTextFromTag(s1, false);
                                setTextStyle(AlStyles.PAR_STYLE_LINK);
                                addTextFromTag(s3, false);
                                addTextFromTag(s2 == null ? "Table" : s2.toString(), false);
                                clearTextStyle(AlStyles.PAR_STYLE_LINK);
                                clearParagraphStyle(AlStyles.PAR_NATIVEJUST | AlStyles.SL_JUST_MASK);
                                newParagraph();
                                newEmptyTextParagraph();
                            }
                            allState.state_skipped_flag = true;
                        } else {
                            addTextFromTag(s1, false);
                            setTextStyle(AlStyles.PAR_STYLE_LINK);
                            addTextFromTag(s3, false);
                            addTextFromTag(s2 == null ? "Table" : s2.toString(), false);
                            clearTextStyle(AlStyles.PAR_STYLE_LINK);
                        }

                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_TH:
            case AlFormatTag.TAG_TD:
                if (tag.closed) {
                    addEndTableColumn(false);
                } else
                if (!tag.ended) {

                } else {

                }
                return true;
            case AlFormatTag.TAG_TR:
                if (tag.closed) {
                    newParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                    addEndTableColumn(true);
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
            case AlFormatTag.TAG_BODY:
                if (tag.closed) {
                    closeOpenNotes();
                    section_count = 0;
                    clearParagraphStyle(AlStyles.PAR_NOTE);
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                } else
                if (!tag.ended) {
                    switch (verifyBody()) {
                        case FB2_BODY_NOTES:
                            if (allState.isOpened) {
                                content_start = size;
                                addTestContent("Notes", section_count);
                            }
                            setParagraphStyle(AlStyles.PAR_NOTE);
                            break;
                        case FB2_BODY_COMMENT:
                            if (allState.isOpened) {
                                content_start = size;
                                addTestContent("Comments", section_count);
                            }
                            //if (allState.isOpened && (tune & 0x02) != 0x00)
                            setParagraphStyle(AlStyles.PAR_NOTE);
                            break;
                    }
                    section_count = 0;
                    allState.state_skipped_flag = false;
                    newParagraph();
                } else {

                }
                return true;
            case AlFormatTag.TAG_BINARY:
                if (tag.closed) {
                    addtestImage();
                } else
                if (!tag.ended) {
                    newParagraph();
                    allState.state_parser = STATE_XML_SKIP;
                    testImage();
                } else {

                }
                return true;
            case AlFormatTag.TAG_COVERPAGE:
                if (tag.closed) {
                    allState.state_skipped_flag = true;
                    clearParagraphStyle(AlStyles.PAR_COVER);
                } else
                if (!tag.ended) {
                    allState.state_skipped_flag = false;
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_COVER);
                } else {

                }
                return true;
            case AlFormatTag.TAG_DESCRIPTION:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_BREAKPAGE);
                } else
                if (!tag.ended) {
                    if (allState.isOpened) {
                        boolean bs = allState.state_skipped_flag;
                        allState.state_skipped_flag = false;
                        newParagraph();
                        setParagraphStyle(AlStyles.PAR_COVER);
                        addCharFromTag((char)AlStyles.CHAR_IMAGE_S, false);
                        addCharFromTag(LEVEL2_COVERTOTEXT, false);
                        addCharFromTag((char)AlStyles.CHAR_IMAGE_E, false);
                        newParagraph();
                        allState.state_skipped_flag = bs;
                        setParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                        clearParagraphStyle(AlStyles.PAR_COVER);
                    } else {
                        if (coverName != null) {
                            addCharFromTag((char)AlStyles.CHAR_IMAGE_S, false);
                            addCharFromTag(LEVEL2_COVERTOTEXT, false);
                            addCharFromTag((char)AlStyles.CHAR_IMAGE_E, false);
                        } else {
                            addCharFromTag((char) 0xa0, false);
                            addCharFromTag((char) 0xa0, false);
                            addCharFromTag((char) 0xa0, false);
                        }
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_TITLE_INFO:
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
            case AlFormatTag.TAG_DOCUMENT_INFO:
                if (tag.closed) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0)
                        clearParagraphStyle(AlStyles.PAR_DESCRIPTION3);
                } else
                if (!tag.ended) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0)
                        setParagraphStyle(AlStyles.PAR_DESCRIPTION3);
                } else {

                }
                return true;
            case AlFormatTag.TAG_PROGRAM_USED:
                if (tag.closed) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION3) != 0)
                        setSpecialText(false);
                } else
                if (!tag.ended) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION3) != 0) {
                        isProgramUsed = true;
                        setSpecialText(true);
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_GENRE:
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
                return true;
            case AlFormatTag.TAG_AUTHOR:
                if (tag.closed) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
                        isAuthor = false;
                        if (allState.isOpened)
                            addAuthor();
                    }
                } else
                if (!tag.ended) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0)
                        isAuthor = true;
                } else {

                }
                return true;
            case AlFormatTag.TAG_FIRST_NAME:
                if (tag.closed) {
                    if (isAuthor)
                        setSpecialText(false);
                } else
                if (!tag.ended) {
                    if (isAuthor) {
                        isAuthorFirst = true;
                        setSpecialText(true);
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_MIDDLE_NAME:
                if (tag.closed) {
                    if (isAuthor)
                        setSpecialText(false);
                } else
                if (!tag.ended) {
                    if (isAuthor) {
                        isAuthorMiddle = true;
                        setSpecialText(true);
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_LAST_NAME:
                if (tag.closed) {
                    if (isAuthor)
                        setSpecialText(false);
                } else
                if (!tag.ended) {
                    if (isAuthor) {
                        isAuthorLast = true;
                        setSpecialText(true);
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_NICKNAME:
                if (tag.closed) {
                    if (isAuthor)
                        setSpecialText(false);
                } else
                if (!tag.ended) {
                    if (isAuthor) {
                        isAuthorNick = true;
                        setSpecialText(true);
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_BOOK_TITLE:
                if (tag.closed) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
                        setSpecialText(false);
                        allState.state_skipped_flag = true;
                        newParagraph();
                        newEmptyStyleParagraph();
                        clearParagraphStyle(AlStyles.PAR_TITLE);
                    }
                } else
                if (!tag.ended) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
                        isBookTitle = true;
                        setSpecialText(true);
                        allState.state_skipped_flag = false;
                        newParagraph();
                        setParagraphStyle(AlStyles.PAR_TITLE);
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_SEQUENCE:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
                        if (allState.isOpened) {
                            setParagraphStyle(AlStyles.PAR_AUTHOR);
                            newParagraph();
                        }
                        addSeries();
                        if (allState.isOpened)
                            clearParagraphStyle(AlStyles.PAR_AUTHOR);
                    }
                }
                return true;
        }

        return false;

		/*if (tag.closed) {
			switch (tag.tag) {		
			case AlFormatTag.TAG_P:
			case AlFormatTag.TAG_LI:
				newParagraph();
				return true;
				
			case AlFormatTag.TAG_A:
				if ((paragraph & AlStyles.PAR_STYLE_LINK) != 0)
					clearTextStyle(AlStyles.PAR_STYLE_LINK);
				return true;
			case AlFormatTag.TAG_B:	
			case AlFormatTag.TAG_STRONG:
				clearTextStyle(AlStyles.PAR_STYLE_BOLD);
				return true;
			case AlFormatTag.TAG_I:	
			case AlFormatTag.TAG_EM:	
			case AlFormatTag.TAG_EMPHASIS:
				clearTextStyle(AlStyles.PAR_STYLE_ITALIC);
				return true;
			case AlFormatTag.TAG_SUP:
				clearTextStyle(AlStyles.PAR_STYLE_SUP);
				return true;
			case AlFormatTag.TAG_SUB:
				clearTextStyle(AlStyles.PAR_STYLE_SUB);
				return true;
			case AlFormatTag.TAG_CSTYLE:
				clearTextStyle(AlStyles.PAR_STYLE_CSTYLE);
				return true;	
			case AlFormatTag.TAG_STRIKE:
			case AlFormatTag.TAG_DEL:	
			case AlFormatTag.TAG_STRIKETHROUGH:
				clearTextStyle(AlStyles.PAR_STYLE_STRIKE);
				return true;
			case AlFormatTag.TAG_S:
			case AlFormatTag.TAG_INS:	
			case AlFormatTag.TAG_U:
			case AlFormatTag.TAG_UNDERLINE:
				clearTextStyle(AlStyles.PAR_STYLE_UNDER);
				return true;
			case AlFormatTag.TAG_SPACING:
				clearTextStyle(AlStyles.PAR_STYLE_RAZR);
				return true;
			case AlFormatTag.TAG_SECTION:
				clearULNumber();
				section_count--;
				newParagraph();
				if ((paragraph & AlStyles.PAR_NOTE) == 0)
					setParagraphStyle(AlStyles.PAR_BREAKPAGE);
				closeOpenNotes();
				return true;	
				
			case AlFormatTag.TAG_BODY:
				closeOpenNotes();
				section_count = 0;
				clearParagraphStyle(AlStyles.PAR_NOTE);
				newParagraph();
				setParagraphStyle(AlStyles.PAR_BREAKPAGE);
				return true;
			case AlFormatTag.TAG_BINARY:
				addtestImage();
				return true;
			case AlFormatTag.TAG_SUBTITLE:				
				clearParagraphStyle(AlStyles.PAR_SUBTITLE);
				newParagraph();
				newEmptyStyleParagraph();
				if (allState.isOpened && (tune & 0x01) != 0x00)
					setSpecialText(false);
				return true;
			case AlFormatTag.TAG_TITLE:				
				clearParagraphStyle(AlStyles.PAR_TITLE);// | AlStyles.PAR_BREAKPAGE);
				newParagraph();
				newEmptyStyleParagraph();
				if (allState.isOpened)
					setSpecialText(false);
				return true;	
			case AlFormatTag.TAG_ANNOTATION:
				if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0) {
					allState.state_skipped_flag = true;
				}
				clearParagraphStyle(AlStyles.PAR_ANNOTATION);
				newParagraph();
				newEmptyStyleParagraph();
				return true;
			case AlFormatTag.TAG_COVERPAGE:
				allState.state_skipped_flag = true;
				clearParagraphStyle(AlStyles.PAR_COVER);
				newParagraph();
				return true;
			case AlFormatTag.TAG_EPIGRAPH:				
				clearParagraphStyle(AlStyles.PAR_EPIGRAPH);
				newParagraph();
				newEmptyStyleParagraph();
				return true;
			case AlFormatTag.TAG_POEM:				
				clearParagraphStyle(AlStyles.PAR_POEM);
				newParagraph();
				newEmptyStyleParagraph();
				return true;
			case AlFormatTag.TAG_V:	
				clearParagraphStyle(AlStyles.PAR_V);
				newParagraph();
			case AlFormatTag.TAG_STANZA:				
				clearParagraphStyle(AlStyles.PAR_STANZA);
				newParagraph();
				return true;
			case AlFormatTag.TAG_DATE:
				clearParagraphStyle(AlStyles.PAR_DATE);
				newParagraph();
				newEmptyStyleParagraph();
				return true;
			case AlFormatTag.TAG_TEXT_AUTHOR:				
				clearParagraphStyle(AlStyles.PAR_AUTHOR);
				newParagraph();
				newEmptyStyleParagraph();
				return true;
			case AlFormatTag.TAG_CODE:
				clearTextStyle(AlStyles.PAR_STYLE_CODE);
				return true;
			case AlFormatTag.TAG_TABLE:
				if (!is_support_table) {
					clearParagraphStyle(AlStyles.PAR_TABLE);
					newParagraph();	
				} else {
					if (allState.isOpened) {
						table_present--;
						if (table_present == 0 && table_start != -1) {
							table_stop = tag.start_pos;
							addTable(AlOneTable.add(table_start, table_start, table_stop));
							table_start = -1;							
							allState.state_skipped_flag = false;
						}
						newParagraph();	
					}
				}
				return true;
			case AlFormatTag.TAG_TH:
			case AlFormatTag.TAG_TD:
				addEndTableColumn(false);
				return true;
			case AlFormatTag.TAG_TR:
				newParagraph();
				return true;
			case AlFormatTag.TAG_CITE:	
				clearParagraphStyle(AlStyles.PAR_CITE);
				newParagraph();	
				newEmptyStyleParagraph();
				return true;
			case AlFormatTag.TAG_DESCRIPTION:
				clearParagraphStyle(AlStyles.PAR_DESCRIPTION1);				
				newParagraph();
				setParagraphStyle(AlStyles.PAR_BREAKPAGE);
				return true;
			case AlFormatTag.TAG_TITLE_INFO:
				if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0)
					clearParagraphStyle(AlStyles.PAR_DESCRIPTION2);	
				return true;
			case AlFormatTag.TAG_DOCUMENT_INFO:
				if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0)
					clearParagraphStyle(AlStyles.PAR_DESCRIPTION3);	
				return true;
			case AlFormatTag.TAG_PROGRAM_USED:
				if ((paragraph & AlStyles.PAR_DESCRIPTION3) != 0)
					setSpecialText(false);	
			case AlFormatTag.TAG_GENRE:
				if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0)
					setSpecialText(false);
				return true;
			case AlFormatTag.TAG_AUTHOR:
				if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
					isAuthor = false;
					if (allState.isOpened)
						addAuthor();
				}
				return true;
			case AlFormatTag.TAG_FIRST_NAME:
				if (isAuthor) 
					setSpecialText(false);
				return true;
			case AlFormatTag.TAG_MIDDLE_NAME:
				if (isAuthor) 
					setSpecialText(false);
				return true;
			case AlFormatTag.TAG_LAST_NAME:
				if (isAuthor) 
					setSpecialText(false);
				return true;
			case AlFormatTag.TAG_NICKNAME:
				if (isAuthor) 
					setSpecialText(false);	
				return true;
			case AlFormatTag.TAG_BOOK_TITLE:
				if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
					setSpecialText(false);	
					allState.state_skipped_flag = true;
					newParagraph();
					newEmptyStyleParagraph();
					clearParagraphStyle(AlStyles.PAR_TITLE);
				}
				return true;
			case AlFormatTag.TAG_UL:				
			case AlFormatTag.TAG_OL:
				decULNumber();				
				return true;
			case AlFormatTag.TAG_PRE:
				clearParagraphStyle(AlStyles.PAR_PRE);
				newParagraph();	
				allState.state_code_flag = false;
				return true;
			
			}
		} else {
			if (allState.isOpened && tag.tag != AlFormatTag.TAG_BINARY) {
				StringBuilder s1 = tag.getATTRValue(AlFormatTag.TAG_ID);
				if (s1 != null)
					addtestLink(s1.toString());	
			}
			
			if (tag.ended == false) {
							
				switch (tag.tag) {	
				case AlFormatTag.TAG_P:
					newParagraph();					
					return true;
					
				case AlFormatTag.TAG_A:
					if (addNotes()) 
						setTextStyle(AlStyles.PAR_STYLE_LINK);
					return true;
				case AlFormatTag.TAG_B:
				case AlFormatTag.TAG_STRONG:
					setTextStyle(AlStyles.PAR_STYLE_BOLD);
					return true;
				case AlFormatTag.TAG_I:	
				case AlFormatTag.TAG_EM:	
				case AlFormatTag.TAG_EMPHASIS:
					setTextStyle(AlStyles.PAR_STYLE_ITALIC);
					return true;
				case AlFormatTag.TAG_SUP:
					setTextStyle(AlStyles.PAR_STYLE_SUP);
					return true;
				case AlFormatTag.TAG_SUB:
					setTextStyle(AlStyles.PAR_STYLE_SUB);
					return true;
				case AlFormatTag.TAG_CSTYLE:
					setTextStyle(AlStyles.PAR_STYLE_CSTYLE);
					return true;	
				case AlFormatTag.TAG_STRIKE:
				case AlFormatTag.TAG_DEL:	
				case AlFormatTag.TAG_STRIKETHROUGH:
					setTextStyle(AlStyles.PAR_STYLE_STRIKE);
					return true;		
				case AlFormatTag.TAG_S:
				case AlFormatTag.TAG_INS:
				case AlFormatTag.TAG_U:				
				case AlFormatTag.TAG_UNDERLINE:
					setTextStyle(AlStyles.PAR_STYLE_UNDER);
					return true;
				case AlFormatTag.TAG_SPACING:
					setTextStyle(AlStyles.PAR_STYLE_RAZR);
					return true;	
					
				case AlFormatTag.TAG_BODY:
					switch (verifyBody()) {
					case FB2_BODY_NOTES:	
						if (allState.isOpened) {					
							content_start = size;						
							addTestContent("Notes", section_count);							
						}
						setParagraphStyle(AlStyles.PAR_NOTE);
						break;
					case FB2_BODY_COMMENT:
						if (allState.isOpened) {					
							content_start = size;						
							addTestContent("Comments", section_count);							
						}
						//if (allState.isOpened && (tune & 0x02) != 0x00)
						  setParagraphStyle(AlStyles.PAR_NOTE);
						break;
					}
					section_count = 0;
					allState.state_skipped_flag = false;
					newParagraph();
					return true;
				
				case AlFormatTag.TAG_SECTION:
					section_count++;
					newParagraph();
					isFirstParagraph = true;
					return true;
				case AlFormatTag.TAG_BINARY:
					newParagraph();
					allState.state_parser = STATE_XML_SKIP;
					testImage();
					return true;
				case AlFormatTag.TAG_ANNOTATION:
					if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0)
						allState.state_skipped_flag = false;				
					newParagraph();
					setParagraphStyle(AlStyles.PAR_ANNOTATION);
					return true;
				case AlFormatTag.TAG_COVERPAGE:
					allState.state_skipped_flag = false;				
					newParagraph();
					setParagraphStyle(AlStyles.PAR_COVER);
					return true;
				case AlFormatTag.TAG_TITLE:
					newParagraph();
					newEmptyStyleParagraph();
					setParagraphStyle(AlStyles.PAR_TITLE);
					if (allState.isOpened) {
						isTitle0 = true;
						content_start = size;
						setSpecialText(true);
					}
					return true;
				case AlFormatTag.TAG_SUBTITLE:
					newParagraph();
					newEmptyStyleParagraph();
					setParagraphStyle(AlStyles.PAR_SUBTITLE);
					if (allState.isOpened && (tune & 0x01) != 0) {
						isTitle1 = true;
						content_start = size;
						setSpecialText(true);
					}
					return true;
				case AlFormatTag.TAG_CODE:
					setTextStyle(AlStyles.PAR_STYLE_CODE);
					return true;
				case AlFormatTag.TAG_TABLE:
					newParagraph();
					if (!is_support_table) {						
						setParagraphStyle(AlStyles.PAR_TABLE);
					} else {
						String s1 = (char)AlStyles.CHAR_LINK_S + "table:" + Integer.toString(allState.start_position_par) + (char)AlStyles.CHAR_LINK_E;
						StringBuilder s2 = tag.getATTRValue(AlFormatTag.TAG_TITLE);
						String s3 = (char)AlStyles.CHAR_IMAGE_S + LEVEL2_TABLETOTEXT_STR + (char)AlStyles.CHAR_IMAGE_E;
						
						if (allState.isOpened) {
							table_present++;
							if (table_present == 1) {
								table_start = allState.start_position_par;
								newEmptyTextParagraph();
								setParagraphStyle(AlStyles.PAR_NATIVEJUST | AlStyles.SL_JUST_CENTER);
								addTextFromTag(s1, false);
								setTextStyle(AlStyles.PAR_STYLE_LINK);
								addTextFromTag(s3, false);								
								addTextFromTag(s2 == null ? "Table" : s2.toString(), false);
								clearTextStyle(AlStyles.PAR_STYLE_LINK);
								clearParagraphStyle(AlStyles.PAR_NATIVEJUST | AlStyles.SL_JUST_MASK);
								newParagraph();
								newEmptyTextParagraph();
							}
							allState.state_skipped_flag = true;
						} else {							
							addTextFromTag(s1, false);
							setTextStyle(AlStyles.PAR_STYLE_LINK);	
							addTextFromTag(s3, false);
							addTextFromTag(s2 == null ? "Table" : s2.toString(), false);
							clearTextStyle(AlStyles.PAR_STYLE_LINK);
						}

					}
					return true;
				case AlFormatTag.TAG_TR:
					newParagraph();
					addEndTableColumn(true);
					return true;
				case AlFormatTag.TAG_CITE:
					newParagraph();
					newEmptyStyleParagraph();
					setParagraphStyle(AlStyles.PAR_CITE);					
					return true;
				
				case AlFormatTag.TAG_EPIGRAPH:
					newParagraph();
					setParagraphStyle(AlStyles.PAR_EPIGRAPH);
					return true;
				case AlFormatTag.TAG_POEM:
					newParagraph();
					newEmptyStyleParagraph();
					setParagraphStyle(AlStyles.PAR_POEM);
					return true;
				case AlFormatTag.TAG_STANZA:
					newParagraph();
					newEmptyStyleParagraph();
					setParagraphStyle(AlStyles.PAR_STANZA);
					return true;
				case AlFormatTag.TAG_V:
					newParagraph();
					setParagraphStyle(AlStyles.PAR_V);
					return true;	
				case AlFormatTag.TAG_DATE:
					newParagraph();
					if ((paragraph & (AlStyles.PAR_STANZA | AlStyles.PAR_POEM)) != 0)
						newEmptyStyleParagraph();
					setParagraphStyle(AlStyles.PAR_DATE);
					return true;
				case AlFormatTag.TAG_TEXT_AUTHOR:
					newParagraph();
					if ((paragraph & (AlStyles.PAR_STANZA | AlStyles.PAR_POEM)) != 0)
						newEmptyStyleParagraph();
					setParagraphStyle(AlStyles.PAR_AUTHOR);
					return true;
				case AlFormatTag.TAG_PRE:
					newParagraph();
					setParagraphStyle(AlStyles.PAR_PRE);
					allState.state_code_flag = true;
					return true;	
					
				case AlFormatTag.TAG_DESCRIPTION:
					
					if (allState.isOpened) {
						boolean bs = allState.state_skipped_flag;
						allState.state_skipped_flag = false;
						newParagraph();
						setParagraphStyle(AlStyles.PAR_COVER);
						addCharFromTag((char)AlStyles.CHAR_IMAGE_S, false);
						addCharFromTag(LEVEL2_COVERTOTEXT, false);
						addCharFromTag((char)AlStyles.CHAR_IMAGE_E, false);
						newParagraph();
						allState.state_skipped_flag = bs;
						setParagraphStyle(AlStyles.PAR_DESCRIPTION1);
						clearParagraphStyle(AlStyles.PAR_COVER);
					} else {
						if (coverName != null) {
							addCharFromTag((char)AlStyles.CHAR_IMAGE_S, false);
							addCharFromTag(LEVEL2_COVERTOTEXT, false);
							addCharFromTag((char)AlStyles.CHAR_IMAGE_E, false);
						} else {
							addCharFromTag((char) 0xa0, false);
							addCharFromTag((char) 0xa0, false);
							addCharFromTag((char) 0xa0, false);
						}
					}
					return true;
				case AlFormatTag.TAG_TITLE_INFO:
					if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0)
						setParagraphStyle(AlStyles.PAR_DESCRIPTION2);
					return true;
				case AlFormatTag.TAG_DOCUMENT_INFO:
					if ((paragraph & AlStyles.PAR_DESCRIPTION1) != 0)
						setParagraphStyle(AlStyles.PAR_DESCRIPTION3);
					return true;	
				case AlFormatTag.TAG_PROGRAM_USED:
					if ((paragraph & AlStyles.PAR_DESCRIPTION3) != 0) {
						isProgramUsed = true;
						setSpecialText(true);
					}
					return true;
				case AlFormatTag.TAG_GENRE:
					if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
						isGenre = true;
						setSpecialText(true);
					}
					return true;
				case AlFormatTag.TAG_AUTHOR:
					if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0)
						isAuthor = true;				
					return true;
				case AlFormatTag.TAG_FIRST_NAME:
					if (isAuthor) {
						isAuthorFirst = true;
						setSpecialText(true);
					}
					return true;
				case AlFormatTag.TAG_MIDDLE_NAME:
					if (isAuthor) {
						isAuthorMiddle = true;
						setSpecialText(true);
					}
					return true;
				case AlFormatTag.TAG_LAST_NAME:
					if (isAuthor) {
						isAuthorLast = true;
						setSpecialText(true);
					}
					return true;
				case AlFormatTag.TAG_NICKNAME:
					if (isAuthor) {
						isAuthorNick = true;
						setSpecialText(true);
					}
					return true;	
				case AlFormatTag.TAG_BOOK_TITLE:
					if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
						isBookTitle = true;
						setSpecialText(true);	
						allState.state_skipped_flag = false;
						newParagraph();
						setParagraphStyle(AlStyles.PAR_TITLE);
					}
					return true;
				case AlFormatTag.TAG_EMPTY_LINE:
					newParagraph();
					newEmptyTextParagraph();
					return true;	
				case AlFormatTag.TAG_IMAGE:
					addImages();
					return true;	
				case AlFormatTag.TAG_UL:
				case AlFormatTag.TAG_OL:
					incULNumber();
					return true;	
				case AlFormatTag.TAG_LI:	
					newParagraph();
					return true;
				}				
			} else {				
				switch (tag.tag) {
				case AlFormatTag.TAG_V:
				case AlFormatTag.TAG_P:
					newParagraph();
					return true;
				case AlFormatTag.TAG_EMPTY_LINE:
					newParagraph();
					newEmptyTextParagraph();
					return true;
					
				case AlFormatTag.TAG_IMAGE:
					addImages();
					return true;
					
				case AlFormatTag.TAG_SEQUENCE:
					if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
						if (allState.isOpened) {
							setParagraphStyle(AlStyles.PAR_AUTHOR);
							newParagraph();
						}
						addSeries();
						if (allState.isOpened)
							clearParagraphStyle(AlStyles.PAR_AUTHOR);
					}
					return true;
				}
			}
		}

		return false;*/
	}
}
