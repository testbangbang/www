package com.neverland.engbook.level2;

import com.neverland.engbook.allstyles.AlCSSHtml;
import com.neverland.engbook.allstyles.AlCSSStyles;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;

public class AlFormatFB2 extends AlFormatBaseHTML {


	private static final int FB2_TEST_BUF_LENGTH = 1024;
	private static final String FB2_TEST_STR_1 = "<fictionbook";
	private static final String FB2_TEST_STR_2 = "<?xml";
	private static final int FB2_BODY_TEXT = 0;
	private static final int FB2_BODY_NOTES = 1;
	private static final int FB2_BODY_COMMENT = 2;

	private String firstAuthor = null;
	private String middleAuthor = null;
	private String lastAuthor = null;
	private String nickAuthor = null;

	public static boolean isFB2(AlFiles a) {

		char[] buf_uc = new char[FB2_TEST_BUF_LENGTH];
		String s;

		if (getTestBuffer(a, TAL_CODE_PAGES.CP1251, buf_uc, FB2_TEST_BUF_LENGTH, true)) {
			s = String.copyValueOf(buf_uc);

			if (s.contains(FB2_TEST_STR_1) && s.contains(FB2_TEST_STR_2))
				return true;
		}

		if (getTestBuffer(a, TAL_CODE_PAGES.CP1200, buf_uc, FB2_TEST_BUF_LENGTH, true)) {
			s = String.copyValueOf(buf_uc);
			if (s.contains(FB2_TEST_STR_1) && s.contains(FB2_TEST_STR_2))
				return true;
		}

		if (getTestBuffer(a, TAL_CODE_PAGES.CP1201, buf_uc, FB2_TEST_BUF_LENGTH, true)) {
			s = String.copyValueOf(buf_uc);
			if (s.contains(FB2_TEST_STR_1) && s.contains(FB2_TEST_STR_2))
				return true;
		}

		return false;
	}

	public AlFormatFB2() {
		allState.section_count = 0;
		firstAuthor = null;
		middleAuthor = null;
		lastAuthor = null;
		nickAuthor = null;
		cssStyles = new AlCSSHtml();
	}

	public void initState(AlBookOptions bookOptions, AlFiles myParent,
						  AlPreferenceOptions pref, AlStylesOptions stl) {
		xml_mode = true;
		ident = "FB2";

		aFiles = myParent;

		if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG) != 0)
			aFiles.needUnpackData();

		preference = pref;
		styles = stl;

		noUseCover = bookOptions.noUseCover;
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
		allState.incSkipped();

		cssStyles.init(this, TAL_CODE_PAGES.CP65001, AlCSSHtml.CSSHTML_SET_FB2);
		if ((bookOptions.formatOptions & AlFiles.BOOKOPTIONS_DISABLE_CSS) != 0)
			cssStyles.disableExternal = true;

		allState.state_parser = 0;
		parser(0, aFiles.getSize());
	}

	@Override
	public boolean haveNotesOnPage() {
		return haveNotesOnPageReal;
	}

	@Override
	boolean isNeedAttribute(int atr) {
		switch (atr) {		
		case AlFormatTag.TAG_NUMBER:
		case AlFormatTag.TAG_CONTENT_TYPE:
		case AlFormatTag.TAG_TITLE:
			return true;
		}
		return super.isNeedAttribute(atr);
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
			bookSeries.add(s.toString());
			allState.clearSkipped();
			addTextFromTag(s, true);
			allState.restoreSkipped();
		}
	}

	private void addtestImage() {
		if (allState.image_start > 0) {
			allState.image_stop = tag.start_pos;
			im.add(AlOneImage.add(allState.image_name, allState.image_start, allState.image_stop, AlOneImage.IMG_BASE64));
		}
		allState.image_start = -1;
	}

	@Override
	public void setSpecialText(boolean flag) {
		if (flag) {
			allState.state_special_flag = true;
			specialBuff.clear();
		} else {
			
			if (specialBuff.isAuthorFirst) {
				firstAuthor = specialBuff.buff.toString();
				specialBuff.isAuthorFirst = false;
			} else 
			if (specialBuff.isAuthorMiddle) {
				middleAuthor = specialBuff.buff.toString();
				specialBuff.isAuthorMiddle = false;
			} else
			if (specialBuff.isAuthorLast) {
				lastAuthor = specialBuff.buff.toString();
				specialBuff.isAuthorLast = false;
			} else
			if (specialBuff.isAuthorNick) {
				if (specialBuff.buff.length() > 0) {
					nickAuthor = '\"' + specialBuff.buff.toString() + '\"';
				}
				specialBuff.isAuthorNick = false;
			} else	
			if (specialBuff.isGenre) {
				bookGenres.add(specialBuff.buff.toString());
				specialBuff.isGenre = false;
			} else
			if (specialBuff.isBookTitle) {
				bookTitle = specialBuff.buff.toString().trim();
				addTestContent(bookTitle, allState.section_count);
				specialBuff.isBookTitle = false;
			} else 
			if (specialBuff.isTitle0) {
				addTestContent(specialBuff.buff.toString().trim(), allState.section_count);
				specialBuff.isTitle0 = false;
			} else
			if (specialBuff.isTitle1) {
				addTestContent(specialBuff.buff.toString().trim(), allState.section_count + 1);
				specialBuff.isTitle1 = false;
			} else	
			if (specialBuff.isProgramUsed) {
				if (program_used_position == -2) {
					program_used_position = allState.start_position_par;				
					if (specialBuff.buff.indexOf(LEVEL2_PRGUSEDTEST) != -1)
						program_used_position = -1;
				}
			}
			allState.state_special_flag = false;
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
			addCharFromTag((char) AlStyles.CHAR_LINK_S, false);
			addTextFromTag(s, false);
			addCharFromTag((char) AlStyles.CHAR_LINK_E, false);
			return true;
		}
		
		return false;
	}

	 private void testImage() {
		allState.image_start = -1;

		StringBuilder s1 = tag.getATTRValue(AlFormatTag.TAG_ID);
		if (s1 != null) {
			allState.image_name = s1.toString();
			allState.image_start = allState.start_position;
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
			if ((styleStack.buffer[styleStack.position].paragraph & AlStyles.SL_COVER) != 0) {
				if (s.length() > 0 && s.charAt(0) == '#')
					s.delete(0, 1);
				coverName = s.toString();				
			} else {
				addCharFromTag((char)AlStyles.CHAR_IMAGE_S, false);
				if (s.charAt(0) == '#')
					s.deleteCharAt(0);
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

        param = tag.getATTRValue(AlFormatTag.TAG_ID);
        if (param != null)
        	addtestLink(param.toString());

        switch (tag.tag) {

			case AlFormatTag.TAG_A:
				if (tag.closed) {
					clearTextStyle(AlStyles.STYLE_LINK);
				} else
				if (!tag.ended) {
					if (addNotes())
						setTextStyle(AlStyles.STYLE_LINK);
				} else {

				}
				return true;
			case AlFormatTag.TAG_SUBTITLE:
				if (tag.closed) {
					newParagraph();
					if ((tune0 & AlFiles.BOOKOPTIONS_FB2_SUBTITLE_2_TOC) != 0x00)
						setSpecialText(false);
				} else
				if (!tag.ended) {
					newParagraph();
					setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
					if ((tune0 & AlFiles.BOOKOPTIONS_FB2_SUBTITLE_2_TOC) != 0) {
						specialBuff.isTitle1 = true;
						allState.content_start = size;
						setSpecialText(true);
					}
				} else {

				}
				return true;
			case AlFormatTag.TAG_TITLE:
				if (tag.closed) {
					newParagraph();
					setSpecialText(false);
				} else
				if (!tag.ended) {
					newParagraph();
					setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
					specialBuff.isTitle0 = true;
					allState.content_start = size;
					setSpecialText(true);
				} else {

				}
				return true;
			case AlFormatTag.TAG_ANNOTATION:
			case AlFormatTag.TAG_EPIGRAPH:
			case AlFormatTag.TAG_POEM:
			case AlFormatTag.TAG_V:
			case AlFormatTag.TAG_DATE:
			case AlFormatTag.TAG_TEXT_AUTHOR:
				if (tag.closed) {
					newParagraph();
				} else
				if (!tag.ended) {
					newParagraph();
					setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
				} else {

				}
				return true;
			case AlFormatTag.TAG_STANZA:
				if (tag.closed) {
					newParagraph();
				} else
				if (!tag.ended) {
					newParagraph();
					newEmptyTextParagraph();
					setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
				} else {

				}
				return true;
			case AlFormatTag.TAG_IMAGE:
				if (tag.closed) {

				} else
				if (!tag.ended) {
					addImages();
				} else {
					addImages();
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
					allState.incSkipped();
					clearParagraphStyle(AlStyles.SL_COVER);
					newParagraph();
				} else
				if (!tag.ended) {
					allState.decSkipped();
					newParagraph();
					setParagraphStyle(AlStyles.SL_COVER);
				} else {

				}

				return true;
			case AlFormatTag.TAG_SEQUENCE:
				if (tag.closed) {

				} else
				if (!tag.ended) {

				} else
				if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
					setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
					newParagraph();
					addSeries();
					newParagraph();
					clearParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
				}
				return true;
			case AlFormatTag.TAG_SECTION:
				if (tag.closed) {
					clearULNumber();
					allState.section_count--;
					newParagraph();
					if (!allState.isNoteSection)
						setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
					closeOpenNotes();
				} else
				if (!tag.ended) {
					allState.section_count++;
					newParagraph();
					isFirstParagraph = true;
				} else {

				}
				return true;
			case AlFormatTag.TAG_BODY:
				if (tag.closed) {
					closeOpenNotes();
					allState.section_count = 0;
					allState.isNoteSection = false;
					newParagraph();
					setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
				} else
				if (!tag.ended) {
					switch (verifyBody()) {
						case FB2_BODY_NOTES:
							allState.content_start = size;
							addTestContent("Notes", allState.section_count);
							allState.isNoteSection = true;
							break;
						case FB2_BODY_COMMENT:
							allState.content_start = size;
							addTestContent("Comments", allState.section_count);
							allState.isNoteSection = true;
							break;
					}
					allState.section_count = 0;
					allState.decSkipped();
					newParagraph();
				} else {

				}
				return true;
			case AlFormatTag.TAG_DESCRIPTION:
				if (tag.closed) {
					clearStateStyle(AlStateLevel2.PAR_DESCRIPTION1);
					newParagraph();
					setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
				} else
				if (!tag.ended) {
					newParagraph();
					allState.clearSkipped();
					setParagraphStyle(AlStyles.SL_COVER);
					addCharFromTag((char)AlStyles.CHAR_IMAGE_S, false);
					addCharFromTag(LEVEL2_COVERTOTEXT, false);
					addCharFromTag((char)AlStyles.CHAR_IMAGE_E, false);
					newParagraph();
					allState.restoreSkipped();
					setStateStyle(AlStateLevel2.PAR_DESCRIPTION1);
					clearParagraphStyle(AlStyles.SL_COVER);
				} else {

				}
				return true;
			case AlFormatTag.TAG_TITLE_INFO:
				if (tag.closed) {
					if ((allState.description & AlStateLevel2.PAR_DESCRIPTION1) != 0)
						clearStateStyle(AlStateLevel2.PAR_DESCRIPTION2);
				} else
				if (!tag.ended) {
					if ((allState.description & AlStateLevel2.PAR_DESCRIPTION1) != 0)
						setStateStyle(AlStateLevel2.PAR_DESCRIPTION2);
				} else {

				}
				return true;
			case AlFormatTag.TAG_DOCUMENT_INFO:
				if (tag.closed) {
					if ((allState.description & AlStateLevel2.PAR_DESCRIPTION1) != 0)
						clearStateStyle(AlStateLevel2.PAR_DESCRIPTION3);
				} else
				if (!tag.ended) {
					if ((allState.description & AlStateLevel2.PAR_DESCRIPTION1) != 0)
						setStateStyle(AlStateLevel2.PAR_DESCRIPTION3);
				} else {

				}
				return true;
			case AlFormatTag.TAG_PROGRAM_USED:
				if (tag.closed) {
					if ((allState.description & AlStateLevel2.PAR_DESCRIPTION3) != 0)
						setSpecialText(false);
				} else
				if (!tag.ended) {
					if ((allState.description & AlStateLevel2.PAR_DESCRIPTION3) != 0) {
						specialBuff.isProgramUsed = true;
						setSpecialText(true);
					}
				} else {

				}
				return true;
			case AlFormatTag.TAG_GENRE:
				if (tag.closed) {
					if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0)
						setSpecialText(false);
				} else
				if (!tag.ended) {
					if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
						specialBuff.isGenre = true;
						setSpecialText(true);
					}
				} else {

				}
				return true;
			case AlFormatTag.TAG_AUTHOR:
				if (tag.closed) {
					if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
						specialBuff.isAuthor = false;
						addAuthor();
					}
				} else
				if (!tag.ended) {
					if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0)
						specialBuff.isAuthor = true;
				} else {

				}
				return true;
			case AlFormatTag.TAG_FIRST_NAME:
				if (tag.closed) {
					if (specialBuff.isAuthor)
						setSpecialText(false);
				} else
				if (!tag.ended) {
					if (specialBuff.isAuthor) {
						specialBuff.isAuthorFirst = true;
						setSpecialText(true);
					}
				} else {

				}
				return true;
			case AlFormatTag.TAG_MIDDLE_NAME:
				if (tag.closed) {
					if (specialBuff.isAuthor)
						setSpecialText(false);
				} else
				if (!tag.ended) {
					if (specialBuff.isAuthor) {
						specialBuff.isAuthorMiddle = true;
						setSpecialText(true);
					}
				} else {

				}
				return true;
			case AlFormatTag.TAG_LAST_NAME:
				if (tag.closed) {
					if (specialBuff.isAuthor)
						setSpecialText(false);
				} else
				if (!tag.ended) {
					if (specialBuff.isAuthor) {
						specialBuff.isAuthorLast = true;
						setSpecialText(true);
					}
				} else {

				}
				return true;
			case AlFormatTag.TAG_NICKNAME:
				if (tag.closed) {
					if (specialBuff.isAuthor)
						setSpecialText(false);
				} else
				if (!tag.ended) {
					if (specialBuff.isAuthor) {
						specialBuff.isAuthorNick = true;
						setSpecialText(true);
					}
				} else {

				}
				return true;
			case AlFormatTag.TAG_BOOK_TITLE:
				if (tag.closed) {
					if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
						setSpecialText(false);
						allState.incSkipped();
						newParagraph();
					}
				} else
				if (!tag.ended) {
					if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
						specialBuff.isBookTitle = true;
						setSpecialText(true);
						allState.decSkipped();
						newParagraph();
						setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
					}
				} else {

				}
				return true;
		}

        return super.externPrepareTAG();
	}
}
