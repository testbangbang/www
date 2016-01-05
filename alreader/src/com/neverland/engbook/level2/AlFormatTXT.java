package com.neverland.engbook.level2;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStylesOptions;

public class AlFormatTXT extends AlFormat {
	
	static final int STATE_TXT_NORMAL = 0;
	static final int STATE_TXT_WAIT = 1;

	static final int TXT_MODE_NORMAL = 0;
	static final int TXT_MODE_DOUBLEA = 1;
	static final int TXT_MODE_SPACE = 2;

	int txt_mode;
	
	public void initState(AlBookOptions bookOptions, AlFiles myParent, 
			AlPreferenceOptions pref, AlStylesOptions stl) {
		allState.isOpened = true;

		ident = "TEXT";

		aFiles = myParent;
		preference = pref;
		styles = stl;

		size = 0;
				
		autoCodePage = bookOptions.codePage == TAL_CODE_PAGES.AUTO;
		
		allState.state_parser = 0;
		if (autoCodePage) {
			use_cpR = getBOMCodePage(true, true, true, false);
			if (use_cpR == TAL_CODE_PAGES.AUTO)
				use_cpR = bookOptions.codePageDefault;
		} else {
			use_cpR = bookOptions.codePage;
		}
		
		txt_mode = TXT_MODE_NORMAL;
		if ((bookOptions.formatOptions & 0x01) != 0)
			detectTXTMode();

		allState.state_parser = STATE_TXT_NORMAL;
		parser(0, aFiles.getSize());
		newParagraph();
		
		allState.isOpened = false;	
	}

	protected void prepareCustom() {

	}

	void detectTXTMode() {
		/*int i;
		char[]	buf_uc = new char [8192];
		for (i = 0; i < 8192; i++)
			buf_uc[i] = 0x00;
		getTestBuffer(aFiles, use_cpR, buf_uc);
		
		int lengthLines = 0;
		int countChars = 0;
		int style = 0;
		
		ArrayList<Integer> items = new ArrayList<Integer>();
		items.clear();
		
		char ch;
		for (i = 0; i < 8192; i++) {
			ch = buf_uc[i];
			if (ch == 0x00 || ch == 0x0d)				
				continue;
			
			switch (ch) {
			case 0x09: case 0x20: case 0xa0:
				if (lengthLines == 0)
					style |= TEST_START_SPACE;
				lengthLines++;
				countChars++;
				break;
			case 0x0a:
				if (lengthLines == 0)
					style |= TEST_EMPTY_LINE;
				if (lengthLines > 80)
					style |= TEST_80_CHAR;
				
				items.add(style);
				
				style = TEST_ITEM;
				lengthLines = 0;
				break;
			default:
				if (ch > 0x20) {
					countChars++;
					lengthLines++;
					style |= TEST_LINE_WITHCHAR;
				}
			}
		}
		
		int countNormalParagraph = items.size();
		int averageParagraphLength = (int)((float)countChars / ( 0.001f + countNormalParagraph));		
		int countPrevEmptyAParagraph = 0;
		int countIdentParagraph = 0;
		int count80char = 0;
		
		// test normal paragraph
		
		// test paragraph with previous empty
		
		for (i = 0; i < items.size(); i++)
			if (i == 0 || (items.get(i - 1) & TEST_EMPTY_LINE) != 0)
				countPrevEmptyAParagraph++;
		
		// test paragraph with start space 
		for (i = 0; i < items.size(); i++)
			if (i == 0 || (items.get(i) & TEST_START_SPACE) != 0)
				countIdentParagraph++;
		
		// teset 80 chars
		for (i = 0; i < items.size(); i++)
			if ((items.get(i) & TEST_80_CHAR) != 0)
				count80char++;
		//
		
		if (count80char == 0) {
			if (countPrevEmptyAParagraph > countIdentParagraph) {
				if (countPrevEmptyAParagraph * 30 > countNormalParagraph)
					txt_mode |= TXT_MODE_DOUBLEA;
			} else {
				if (countIdentParagraph * 30 > countNormalParagraph)
					txt_mode |= TXT_MODE_SPACE;
			}	
		}
		
		items.clear();*/
	}

	void formatAddonInt() {		
		pariType = paragraph;
	}

	void doSpecialGetParagraph(long iType, int addon, long stk[], int cpl[]) {
		paragraph = iType;
		allState.state_parser = 0;
	}

	@Override
	protected void doTextChar(char ch, boolean addSpecial) {
		if (allState.isOpened) {
			if (allState.text_present) {
				size++;
				parPositionE = allState.start_position;		
				allState.letter_present = (allState.letter_present) || (ch != 0xa0 && ch != 0x20);
				if (size - parStart > EngBookMyType.AL_MAX_PARAGRAPH_LEN) {
					if (!AlUnicode.isLetterOrDigit(ch) && !allState.insertFromTag && allState.state_parser == 0)
						newParagraph();
				}
			} else 
			if (ch != 0x20) {
				parPositionS = allState.start_position;
				formatAddonInt();
				parStart = size;
				allState.text_present = true;
				allState.letter_present = (allState.letter_present) || (ch != 0xa0 && ch != 0x20);
				size++;
				parPositionE = allState.start_position;
			}		
		} else {
			if (allState.text_present) {					
				stored_par.data[stored_par.cpos++] = ch;
			} else 
			if (ch != 0x20) {
				stored_par.data[stored_par.cpos++] = ch;
				allState.text_present = true;
			}			
		}
	}

	@Override
	protected void parser(final int start_pos, final int stop_pos) {
		// this code must be in any parser without change!!!
		int 	buf_cnt = 0;
		char 	ch;

		allState.text_present = false;
		int j;
		AlIntHolder jVal = new AlIntHolder(0);
		
		for (int i = start_pos; i < stop_pos;) {			
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
				allState.start_position = i + j;	
				
				jVal.value = j;
				ch = AlUnicode.byte2Wide(use_cpR, parser_inBuff, jVal);
				j = jVal.value;
				
		// end must be code				
				/////////////////// Begin Real Parser
				switch (txt_mode) {				
				case TXT_MODE_DOUBLEA:
					switch (allState.state_parser) {
					case STATE_TXT_NORMAL:
						if (ch < 0x20) {
							if (ch == 0x0a) {
								allState.state_parser = STATE_TXT_WAIT;
							} else	
							if (ch == 0x09) {
								doTextChar(' ', true);
							}
						} else {
							doTextChar(ch, true);
						}
						break;
					case STATE_TXT_WAIT:
						if (ch < 0x20) {
							if (ch == 0x0a) {
								if (allState.text_present) {
									newParagraph();
								} else {
									newEmptyTextParagraph();
								}
								allState.state_parser = STATE_TXT_NORMAL;
							} else	
							if (ch == 0x09) {
								doTextChar(' ', true);
								allState.state_parser = STATE_TXT_NORMAL;
							}
						} else {
							doTextChar(' ', true);
							doTextChar(ch, true);
							allState.state_parser = STATE_TXT_NORMAL;
						}
						break;
					}				
					break;	
				case TXT_MODE_SPACE:
					switch (allState.state_parser) {
					case STATE_TXT_NORMAL:
						if (ch < 0x20) {
							if (ch == 0x0a) {
								allState.state_parser = STATE_TXT_WAIT;
							} else	
							if (ch == 0x09) {
								doTextChar(' ', true);
							}
						} else {
							doTextChar(ch, true);
						}
						break;
					case STATE_TXT_WAIT:
						if (ch == 0x20 || ch == 0xa0 || ch == 0x09) {
							if (allState.text_present) {
								newParagraph();
							} else {
								newEmptyTextParagraph();
							}
							allState.state_parser = STATE_TXT_NORMAL;
						} else
						if (ch < 0x20) {
							
						} else {
							doTextChar(' ', true);
							doTextChar(ch, true);
							allState.state_parser = STATE_TXT_NORMAL;
						}
						break;
					}				
					break;	
				default:				
					if (ch < 0x20) {
						if (ch == 0x0a) {
							if (allState.text_present) {
								newParagraph();
							} else {
								newEmptyTextParagraph();
							}
						} else	
						if (ch == 0x09) {
							doTextChar(' ', true);
						}
					} else {
						doTextChar(ch, true);
					}
					break;
				}
				/////////////////// End Real Parser
		// this code must be in any parser without change!!!
			}
			i += j;
		}
		if (allState.isOpened)
			newParagraph();
		// end must be cod
	}
}
