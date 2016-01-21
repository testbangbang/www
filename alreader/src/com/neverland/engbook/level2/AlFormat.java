package com.neverland.engbook.level2;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.util.Log;

import com.neverland.engbook.bookobj.AlBookEng.PairTextStyle;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.AlOneSearchResult;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlRandomAccessFile;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.AlOneContent;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlOneLink;
import com.neverland.engbook.util.AlOneStyleStack;
import com.neverland.engbook.util.AlOneTable;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlProfileOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.InternalConst;

public abstract class AlFormat {
	
	public static final char LEVEL2_TABLETOTEXT	= 	':';
	public static final String LEVEL2_TABLETOTEXT_STR	= 	":";
	public static final char  LEVEL2_COVERTOTEXT =	'*';
	public static final String  LEVEL2_COVERTOTEXT_STR =	"*";
	public static final char  LEVEL2_SPACE =			' ';
	public static final String LEVEL2_LIST0TOTEXT =		"\u2022\u00a0";
	public static final String LEVEL2_LIST1TOTEXT =		"\u25e6\u00a0";
	public static final String LEVEL2_LIST2TOTEXT = 		"\u25aa\u00a0";
	public static final String LEVEL2_PRGUSED =			", AlReader.NEW";
	public static final String LEVEL2_PRGUSEDTEST = 		"AlReader.NEW";
	public static final int LEVEL2_FRM_ADDON_SKIPPEDTEXT = 0x10000000;
	public static final int LEVEL2_FRM_ADDON_CODETEXT =	0x20000000;
	public static final int LEVEL2_FRM_ADDON_SPECIALTEXT = 0x40000000;
	
	public long						lastPageCount;
	public long						lastCalcTime;
	
	AlPreferenceOptions		preference = new AlPreferenceOptions();
	AlStylesOptions			styles = new AlStylesOptions();
	int						size;
	
	protected int				use_cpR0;
	protected char[]			data_cp = null;
	
	public ArrayList<String> 			bookAuthors = new ArrayList<String>(0);
	ArrayList<String> 			bookGenres = new ArrayList<String>(0);
	ArrayList<String> 			bookSeries = new ArrayList<String>(0);
	public String			  		bookTitle = null;
	public AlFiles 					aFiles = null;
	public ArrayList<AlOneSearchResult>	resfind = new ArrayList<AlOneSearchResult>(0);
	
	ArrayList<AlOneParagraph>		par = new ArrayList<AlOneParagraph>(0);
	ArrayList<AlOneImage>			im = new ArrayList<AlOneImage>(0);
	ArrayList<AlOneTable>			ta = new ArrayList<AlOneTable>(0);
	ArrayList<AlOneLink>			lnk = new ArrayList<AlOneLink>(0);
	ArrayList<AlOneContent>		ttl = new ArrayList<AlOneContent>(0);
	
	boolean						autoCodePage;
	
	String					coverName;
	
	int 					parLength;
	int 					parPositionS;
	int 					parPositionE;
	int 					parStart;
	long 					pariType;
	int						parAddon;	
	
	AlStateLevel2				allState = new AlStateLevel2();
	
	
	long						paragraph;

	AlStoredPar					stored_par = new AlStoredPar();
	StringBuilder				state_specialBuff0 = new StringBuilder();
	
	private AlSlotData		slotText = new AlSlotData();
	private AlSlotData		slotNote = new AlSlotData();

	int 					tune;
	boolean						isFirstParagraph;

	int						program_used_position;
	
	String					ident;
	
	byte[]					parser_inBuff = new byte [AlFiles.LEVEL1_FILE_BUF_SIZE + 2];
	
	protected ArrayList<AlOneStyleStack> styleStack = new ArrayList<AlOneStyleStack>(0);
	protected int styleStack_point = 0;

	
	
	public AlFormat() {
		styleStack.clear();
		//styleStack_point = 0;	
		//styleStack.push_back(TAL_OneStyleStack(0, 0));

		coverName = null;
		bookAuthors.clear();
		bookGenres.clear();
		bookSeries.clear();
		bookTitle = null;		

		state_specialBuff0.setLength(0);
		program_used_position = -2;
		tune = 0;		
		
		parLength = 0;
		parPositionS = 0;
		parPositionE = 0;
		parStart = 0;
		pariType = 0;
		parAddon = 0;
		
		isFirstParagraph = true;
		paragraph = 0;
		styleStack_point = 0;

		clearAllArray();

		autoCodePage = true;
	}

	@Override
	public void finalize() {
		clearAllArray();
		styleStack.clear();
		stored_par.data = null;
	}

	void clearAllArray() {
		par.clear();
		im.clear();
		ta.clear();
		lnk.clear();
		ttl.clear();
		resfind.clear();
	}

	void addContent(AlOneContent ap) {
		if (ap.iType > 9)
			ap.iType = 9;
		ttl.add(ap);
	}

	void doSpecialGetParagraph(long iType, int addon, long[] stk, int[] cpl) {
		paragraph = iType;
		allState.state_parser = 0;
		int cp = ((addon & 0x80000000) != 0) ? -1 : addon & 0x0000ffff;
		
		if (cp != use_cpR0) {
			setCP(cp);
		}
		
		allState.state_skipped_flag = (addon & LEVEL2_FRM_ADDON_SKIPPEDTEXT) != 0;
		allState.state_code_flag = (addon & LEVEL2_FRM_ADDON_CODETEXT) != 0;
		//allState.state_special_flag = (addon & LEVEL2_FRM_ADDON_SPECIALTEXT) != 0;
	}
		
	void formatAddonInt() {		
		pariType = paragraph; 
		parAddon = use_cpR0 & 0x8000ffff;
		if (allState.state_skipped_flag)
			parAddon += LEVEL2_FRM_ADDON_SKIPPEDTEXT;
		if (allState.state_code_flag)
			parAddon += LEVEL2_FRM_ADDON_CODETEXT;
		/*if (allState.state_special_flag)
			parAddon += LEVEL2_FRM_ADDON_SPECIALTEXT;*/
	}


	void newEmptyTextParagraph() {
		paragraph |= AlStyles.PAR_PREVIOUS_EMPTY_1;
		allState.text_present = false;
		if (allState.state_special_flag0)
			state_specialBuff0.append(' ');
	}

	void newEmptyStyleParagraph() {
		paragraph |= AlStyles.PAR_PREVIOUS_EMPTY_0;
		allState.text_present = false;
		if (allState.state_special_flag0)
			state_specialBuff0.append(' ');
	}

	boolean addTable(AlOneTable ap) {		
		ta.add(ap);
		return true;
	}

	void addtestLink(String s) {
		if (allState.isOpened) {			
			AlOneLink a = AlOneLink.add(s, size, ((paragraph & AlStyles.PAR_NOTE) != 0) ? 1 : 0);
			lnk.add(a);		
		}
	}

	void addtestLink(String s, int tp) {
		if (allState.isOpened) 			
			lnk.add(AlOneLink.add(s, size, tp));		
	}


	void decULNumber() {
		long tmp = (paragraph >> AlStyles.PAR_UL_SHIFT) & AlStyles.PAR_UL_MASK;
		if (tmp > 0x00)
			tmp--;
		if (tmp == 0)
			clearParagraphStyle(AlStyles.PAR_UL);
		tmp <<= AlStyles.PAR_UL_SHIFT;
		paragraph &= ~AlStyles.PAR_UL_BASE;
		paragraph |= tmp;
	}

	void incULNumber() {
		long tmp = (paragraph >> AlStyles.PAR_UL_SHIFT) & AlStyles.PAR_UL_MASK;
		if (tmp == 0)
			setParagraphStyle(AlStyles.PAR_UL);
		if (tmp < 0x0f)
			tmp++;
		tmp <<= AlStyles.PAR_UL_SHIFT;
		paragraph &= ~AlStyles.PAR_UL_BASE;
		paragraph |= tmp;
	}

	void clearULNumber() {
		paragraph &= ~AlStyles.PAR_UL_BASE;
	}


	void closeOpenNotes() {
		if (allState.isOpened) {
			if (lnk.size() > 0) {
				AlOneLink al = lnk.get(lnk.size() - 1); 
				if (al.iType == 1 && al.positionE == -1) 
					lnk.get(lnk.size() - 1).positionE = size;
			}
		}
	}


	char getConvertChar(int cp, AlIntHolder pos) {
		byte[] tmp_buff = new byte[4];
		aFiles.getByteBuffer(pos.value, tmp_buff, 4);
		AlIntHolder j = new AlIntHolder(0);
		char ch = AlUnicode.byte2Wide(cp, tmp_buff, j);
		pos.value += j.value;
		return ch;
	}

	int getBOMCodePage(boolean bomUTF16, boolean bomUTF8, boolean realUTF8, boolean realAll) {
		char ch;
		AlIntHolder pos = new AlIntHolder(0);

		if (bomUTF16) {
			pos.value = 0;
			ch = getConvertChar(TAL_CODE_PAGES.CP1200, pos);	
			if (ch == 0xfeff) 
				return TAL_CODE_PAGES.CP1200;
			if (ch == 0xfffe) 
				return TAL_CODE_PAGES.CP1201;
		}

		if (bomUTF8) {
			pos.value = 0;
			ch = getConvertChar(TAL_CODE_PAGES.CP65001, pos);	
			if (ch == 0xfeff) 
				return TAL_CODE_PAGES.CP65001;
		}

		if (realUTF8) {
			byte[] data = new byte[4096];
			pos.value = 16384;
			if (pos.value > aFiles.getSize())
				pos.value = aFiles.getSize() - 4096;
			if (pos.value < 0)
				pos.value = 0;
			int cnt = aFiles.getByteBuffer(pos.value, data, 4096);
			int state = 0, seq_ok = 0, seq_all = 0, i;
			boolean noErr = true;
			for (i = 0; i < cnt && noErr; i++) {
				ch = (char) (data[i] & 0xff);
				if ((ch < 0x80) || ((ch & 0xc0) == 0xc0))
					break;
			}
			for (; i < cnt && noErr; i++) {
				ch = (char) (data[i] & 0xff);
				switch (state) {
				case 0:
					if ((ch & 0x80) == 0)
						continue;
					seq_all++;
					if ((ch & 0xfe) == 0xfc)
						state = 62; else
					if ((ch & 0xfc) == 0xf8)
						state = 52; else
					if ((ch & 0xf8) == 0xf0)
						state = 42; else
					if ((ch & 0xf0) == 0xe0)
						state = 32;	else	
					if ((ch & 0xe0) == 0xc0)
						state = 22; else
						noErr = false;
					break;
				case 32: case 42: case 43: case 52: 
				case 53: case 54: case 62: case 63: 
				case 64: case 65:  
					seq_all++;
					if ((ch & 0xc0) == 0x80) state++; else noErr = false;
					break;
				case 22: case 33: case 44: case 55: 
				case 66:
					seq_all++;
					if ((ch & 0xc0) == 0x80) {
						seq_ok++;
						state = 0;
					} else noErr = false;
					break;	
				}
			}
			
			if (noErr) {
				if (seq_all > 0) {
					return TAL_CODE_PAGES.CP65001;
				}
			}
		}

		if (realAll) {

		}

		return TAL_CODE_PAGES.AUTO;
	}

	protected void setCP(int newcp) {
		use_cpR0 = AlUnicode.int2cp(newcp);
		data_cp = AlUnicode.getDataCP(use_cpR0);
	}
	
	int getCP() {
		if (autoCodePage)
			return TAL_CODE_PAGES.AUTO;
		return use_cpR0;
	}

	void addRealParagraph(AlOneParagraph a) {
		par.add(a);
	}

	void setTextStyle(int tag) {
		allState.insertFromTag = true;
		paragraph |= tag;		
		if (allState.text_present)
			doTextChar(getTextStyle(), false);
		allState.insertFromTag = false;
	}
		
	void clearTextStyle(int tag) {
		allState.insertFromTag = true;
		paragraph &= (~tag);		
		if (allState.text_present)
			doTextChar(getTextStyle(), false);
		allState.insertFromTag = false;
	}

	char getTextStyle() {
		return (char) (AlStyles.STYLE_BASE0 + (paragraph & AlStyles.PAR_STYLE_MASK));
	}
		
	void setParagraphStyle(long tag) {		
		paragraph |= tag;		
	}
		
	void clearParagraphStyle(long tag) {		
		paragraph &= (~tag);
	}

	void newParagraph() {
		AlOneParagraph a = new AlOneParagraph();

		int len = size - parStart;
		if (len != 0 && allState.text_present) {
			
			if ((pariType & AlStyles.PAR_PARAGRAPH_MASK) == 0) {
				if (isFirstParagraph) {
					pariType |= AlStyles.PAR_FIRSTP;
					isFirstParagraph = false;
				}
			}
			
			a.positionS = parPositionS;
			a.positionE = parPositionE;
			a.start = parStart;
			a.length = len;
			a.iType = pariType;
			a.addon = parAddon;
			//a.stack = parStack;
			//a.cp = parCP;

			addRealParagraph(a);
			clearParagraphStyle(AlStyles.PAR_PREVIOUS_EMPTY_0 | AlStyles.PAR_PREVIOUS_EMPTY_1 |
				AlStyles.PAR_BREAKPAGE | AlStyles.PAR_FIRSTP);
			
			if (!allState.letter_present)
				newEmptyTextParagraph();
		}
		allState.text_present = false;	
		allState.letter_present = false;
		if (allState.state_special_flag0)
			state_specialBuff0.append(' ');
	}

	void addTextFromTag(String s, boolean addSpecial) {
		int i; final int l = s.length();
		allState.insertFromTag = true;
		for (i = 0; i < l; i++)
			doTextChar(s.charAt(i), addSpecial);
		allState.insertFromTag = false;
	}
	
	void addTextFromTag(StringBuilder s, boolean addSpecial) {
		int i; final int l = s.length();
		allState.insertFromTag = true;
		for (i = 0; i < l; i++)
			doTextChar(s.charAt(i), addSpecial);
		allState.insertFromTag = false;
	}

	void addCharFromTag(char s, boolean addSpecial) {
		allState.insertFromTag = true;
		doTextChar(s, addSpecial);
		allState.insertFromTag = false;
	}

	void incStylePoint(long style, int tag) {
		styleStack_point++;
		if (styleStack_point >= styleStack.size()) {
			styleStack.add(AlOneStyleStack.addStyleStack(style, tag));
		} else {
			(styleStack.get(styleStack_point)).real_style = style;
		}
	}

	long decStylePoint(int tag) {
		long res = styleStack.get(styleStack_point).real_style;
		if (styleStack_point > 0)
			styleStack_point--;
		return 0;
	}

	public int getSize() {
		return size;
	}

	public void prepareAll() {
		int i, j, k;
		
		if (ttl.size() > 0) {
			
			int m = 100;
	        for (i = 0; i < ttl.size(); i++)
	        	if (ttl.get(i).iType < m)
					m = ttl.get(i).iType;
	        if (m != 0)
	        	for (i = 0; i < ttl.size(); i++)
		        	ttl.get(i).iType -= m;
			
			AlOneContent content = null;
			StringBuilder sb = new StringBuilder();
			sb.setLength(0);
			for (i = 0; i < ttl.size(); i++) {
				content = ttl.get(i);
			
				sb.setLength(0);
				sb.append(content.name);				
				
				boolean invisible = false;
				for (j = 0; j < sb.length(); j++) {
					if (sb.charAt(j) == AlStyles.CHAR_IMAGE_S || sb.charAt(j)  == AlStyles.CHAR_LINK_S)
						invisible = true;			
					
					while (invisible && j < sb.length()) {
						if (sb.charAt(j)  == AlStyles.CHAR_IMAGE_E || sb.charAt(j)  == AlStyles.CHAR_LINK_E)
							invisible = false;
						sb.deleteCharAt(j);
					}
				}
				
				for (j = 0; j < sb.length(); j++) {
					if ((sb.charAt(j)  & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0 || sb.charAt(j)  < 0x20) {
						sb.setCharAt(j, (char) 0x00);
					} else
					if (sb.charAt(j)  == 0xa0) {
						sb.setCharAt(j, (char) 0x20);
					}					
				}
				
				j = sb.length() - 1;
				while (j >= 0) {
					if (sb.charAt(j)  == 0x00 || sb.charAt(j)  == 0xad) {
						sb.deleteCharAt(j);
					} else j--;
				}
				
				for (j = 0; j < sb.length(); j++) {
					while (sb.charAt(j)  == 0x20 && j + 1 < sb.length() && sb.charAt(j + 1) == 0x20)
						sb.deleteCharAt(j);
				}
				
				while (sb.length() > 0)
					if (sb.charAt(0) == 0x20) {
						sb.deleteCharAt(0);
					} else {
						break;
					}
				while (sb.length() > 0)
					if (sb.charAt(sb.length() - 1) == 0x20) {
						sb.deleteCharAt(sb.length() - 1);
					} else {
						break;
					}				
				
				content.name = sb.toString();
			}
		}
				
		prepareCustom();
		
		if (lnk.size() > 0) {
			boolean need_end = false;
			AlOneLink link0, link1 = null;
			for (i = 0; i < lnk.size(); i++) {
				link0 = lnk.get(i);
				if (link0.iType == 1 && link0.positionE == -1) {
					k = 0; need_end = false; link1 = null;
					for (j = i + 1; j < lnk.size(); j++) {
						link1 = lnk.get(j);
						if (link1.positionS == link0.positionS) {
							k++;
							if (link1.positionE != -1) {
								need_end = true;
								break;
							}
							continue;
						}
						if (link1.iType == 1)						
							break;
					}
					
					if (link1 == null) {
						lnk.get(i).positionE = size;
					} else {
						for (j  = i; j <= i + k; j++) {
							if (link1.iType == 1) {
								lnk.get(i).positionE = need_end ? link1.positionE : link1.positionS;
							} else {
								lnk.get(i).positionE = size;
							}
						}
					}
				}
			}
		}
		
		int ie = par.size(); 
		long iv0 = 0, iv1;
		
		for (i = 0; i < ie; i++) {
			iv1 = par.get(i).iType & AlStyles.PAR_PARAGRAPH_MASK;
			if (iv1 == iv0 && iv1 != AlStyles.PAR_CITE)  
				par.get(i).iType &= AlStyles.PAR_PREVIOUS_EMPTY_MASK;
			iv0 = iv1;
		}		
	}

	/*public int getTextBuffer_Notes(int pos, char[] text, long[] style, AlProfileOptions profiles) {
		pos &= AlFiles.LEVEL1_FILE_BUF_MASK;
		int end = pos + getParagraphSlot(pos, text, style, profiles);		
		return end - pos;
	}*/

	
	
	public int getNoteBuffer(int pos, PairTextStyle textAndStyle, int shtamp, AlProfileOptions profiles) {
		return getAllBuffer(pos, textAndStyle, slotNote, shtamp, profiles);
	}
	
	public int getTextBuffer(int pos, PairTextStyle textAndStyle, int shtamp, AlProfileOptions profiles) {
		return getAllBuffer(pos, textAndStyle, slotText, shtamp, profiles);
	}
	
	private int getAllBuffer(int pos, PairTextStyle textAndStyle, AlSlotData slot, int shtamp, AlProfileOptions profiles) {
		pos &= AlFiles.LEVEL1_FILE_BUF_MASK;

		if (shtamp != slot.shtamp) {
			slot.end[0] = slot.end[1] = -1;
			slot.shtamp = shtamp;
		}
		
		if (slot.start[slot.active] == pos && slot.end[slot.active] > slot.start[slot.active]) {		
			textAndStyle.txt = slot.txt[slot.active];
			textAndStyle.stl = slot.stl[slot.active];
			return slot.end[slot.active] - pos;			
		}

		slot.active = 1 - slot.active;
		
		if (slot.start[slot.active] == pos && slot.end[slot.active] > slot.start[slot.active]) {			
			textAndStyle.txt = slot.txt[slot.active];
			textAndStyle.stl = slot.stl[slot.active];
			return slot.end[slot.active] - pos;
		}
			
		slot.initBuffer();
		
		//Log.e("fill buffer " + Integer.toString(pos), Integer.toString(slot.active) + '_' + slot.toString());
		
		slot.start[slot.active] = pos;
		slot.end[slot.active] = pos + getParagraphSlot(pos, slot.txt[slot.active], slot.stl[slot.active], profiles);		

		textAndStyle.txt = slot.txt[slot.active];
		textAndStyle.stl = slot.stl[slot.active];
		return slot.end[slot.active] - pos;
	}

	int findParagraphByPos0(int start, int end, int pos) {
		int tmp = (end + start) >> 1;
		AlOneParagraph ap = par.get(tmp);
		if (ap.start > pos) {
			return findParagraphByPos0(start, tmp, pos);
		} else
		if (ap.start + ap.length <= pos) {
			return findParagraphByPos0(tmp, end, pos);
		}
		return tmp;	
	}

	int findParagraphByPos(int start, int end, int pos) {
		int p = pos;
		if (pos >= size)
			pos = size - 1;
		if (pos < 0)
			pos = 0;
		return findParagraphByPos0(start, end, pos);		
	}

	void getPreparedParagraph0(int paragraph_num, AlOneParagraph alp) {
		int len;
		
		if (alp == null)
			alp = par.get(paragraph_num);
		len = alp.length;
		getParagraph(alp);
				
		int i, j; char ch;
		int start_style_point = -3;
		//������ ��������
		for (i = 0; i < len; i++) {
			ch = stored_par.data[i];
			switch (ch) {
			case 0xad:   stored_par.data[i] = 0x00; break;
			case 0x2011: stored_par.data[i] = 0x2d; break;
			case 0x3000: stored_par.data[i] = 0x20; break;
			}
		}
		
		// �������������� code /code � pre /pre
		if ((alp.iType & (AlStyles.PAR_PRE | AlStyles.PAR_STYLE_CODE)) == AlStyles.PAR_STYLE_CODE) {
			start_style_point = 1; j = AlStyles.PAR_STYLE_CODE;
			for (i = 0; i < len; i++) {
				ch = stored_par.data[i];
				if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
					if ((ch & AlStyles.PAR_STYLE_CODE) == 0) {
						j = ch & AlStyles.PAR_STYLE_MASK; 
						continue;
					}
				}
				if ((j & AlStyles.PAR_STYLE_CODE) == 0 && ch != 0x20 && ch != 0xa0) {
					start_style_point = 0;
					break;
				}
			}			
			if (start_style_point == 1) {
				alp.iType |= AlStyles.PAR_PRE;
			}
		}
		// �������� ����������� ��������
		if (preference.delete0xA0 && (alp.iType & AlStyles.PAR_PRE) == 0) {
			j = (int) (alp.iType & AlStyles.PAR_STYLE_CODE); 
			for (i = 0; i < len; i++) {
				ch = stored_par.data[i];
				if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
					if ((ch & AlStyles.PAR_STYLE_CODE) == 0) {
						j = (int) (ch & AlStyles.PAR_STYLE_CODE); 
						continue;
					}
				}
				if (ch == 0xa0 && j == 0 && i < len - 1 && !AlUnicode.isDashPunctuation(stored_par.data[i + 1])) {
					stored_par.data[i] = 0x20;
				}
			}	
		}
		
		// ��������� ���������
		
		//
		
		// �������������� ������ � SUP, ���� ��������
		start_style_point = -3;
		if (preference.notesAsSUP) {
			for (i = 0; i < len; i++) {
				ch = stored_par.data[i];
				
				if (ch == AlStyles.CHAR_LINK_S && i != 0) {
					start_style_point = -2;
				} else
				//if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
				//	
				//} else
				if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
					if (start_style_point == -1) {
						if ((ch & AlStyles.SL_MASKFORLINK) == AlStyles.STYLE_LINK)
							start_style_point = i;
					} else 
					if (start_style_point >= 0) {
						if ((ch & AlStyles.STYLE_LINK) == 0)
							stored_par.data[start_style_point] |= AlStyles.STYLE_SUP;
						start_style_point = -1;
					}
				} else {
					if (start_style_point == -2 ) {
						if (ch == 0x00)
							continue;
						start_style_point = (ch != '#') ? -1 : -1;
						//start_style_point = (ch != '#') ? -3 : -1;
					} else
					if (start_style_point >= 0) {
						switch (ch) {
						case 0x00: case '[': case ']': case '0': case '1': case '2': case '{': case '}': case '(': case ')':
						case '3' : case '4': case '5': case '6': case '7': case '8': case '9': case '*':
							break;
						default:
							start_style_point = -1;
							break;
						}
					}
				}			
			}
		}
		
		if ((alp.iType & AlStyles.PAR_PRE) != 0 && len > 1) {
			for (i = 0; i < len - 1; i++) {
				ch = stored_par.data[i];
				if (ch != 0x20) {
					i++;
					continue;
				}
				
				for (j = i + 1; j < len; j++) {
					if (stored_par.data[j] < 0x20 || ((stored_par.data[j] & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0)) 
						continue;
					if (stored_par.data[j] == 0x20) {
						stored_par.data[i] = 0xa0;
						break;
					}
				}
			}
			
			return;
		}
		
		if (preference.need_dialog == 2)
			return;
		
		boolean disable_linear = (alp.iType & (AlStyles.PAR_PRE | AlStyles.SL_CODE)) != 0;
		// ������������ ��������
		for (i = 0; i < len; i++) {
			ch = stored_par.data[i];
			if (Character.getType(ch) == Character.SPACE_SEPARATOR) {
				if (disable_linear)
					return;
				stored_par.data[i] = AlStyles.CHAR_NONE;
			} else
			if (ch < 0x20 || ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0)) {
				disable_linear = (ch & (AlStyles.PAR_PRE | AlStyles.SL_CODE)) != 0;
			} else
			//if ((ch & AlStyles.STYLE_BASE_MASK) == STYLE_BASE1) {
			//
			//} else
			if (ch == 0x2022) {
				i++;
				break;
			} else
			if (Character.getType(ch) == Character.DASH_PUNCTUATION) {
				if (preference.need_dialog == 0)
					stored_par.data[i] = 8212;				
				i++;
				break;				
			} else {
				return;
			}
		}
		for (; i < len; i++) {
			ch = stored_par.data[i];
			if (Character.getType(ch) == Character.SPACE_SEPARATOR) {
				stored_par.data[i] = 0xa0;
				i++;
				break;
			} else
			if (ch < 0x20 || ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0)) {
				
			//} else
			//if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
				
			} else {
				return;
			}
		}
		for (; i < len; i++) {
			ch = stored_par.data[i];
			if (Character.getType(ch) == Character.SPACE_SEPARATOR) {
				stored_par.data[i] = AlStyles.CHAR_NONE;
			} else
			if (ch < 0x20 || ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0)) {
				
			//} else
			//if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
				
			} else {
				return;
			}
		}
	}

	void getParagraph(AlOneParagraph ap) {
		if (stored_par.data == null || stored_par.size < ap.length) {
			if (stored_par.data != null)
				stored_par.data = null;
			stored_par.size = ap.length;
			stored_par.data = new char [stored_par.size];
		}
		stored_par.length = ap.length;
		stored_par.cpos = 0;
		doSpecialGetParagraph(ap.iType, ap.addon, null, null);//ap.stack, ap.cp);	
		styleStack_point = 0;
		parser(ap.positionS, ap.positionE + 1);
	}

	int getParagraphSlot(int pos, char[] slot_t, long[] slot_s, AlProfileOptions profiles) {
		boolean isInvisible = false;
		char ch;
					
		int profileType = profiles.showFirstLetter;//ProfileManager.isMarkFirstLetter() & 0x03;
		boolean extfl_pstart = true;//(PrefManager.getInt(R.string.keyoptuser_custom) & (1 << 0)) != 0;
		boolean extfl_pend = true;//extfl_pstart && (PrefManager.getInt(R.string.keyoptuser_custom) & (1 << 1)) != 0;
		boolean extfl_dialog = true;//(PrefManager.getInt(R.string.keyoptuser_custom) & (1 << 2)) != 0;
		long extfl_mask = 0x03;//(PrefManager.getInt(R.string.keyoptuser_custom) & (1 << 4)) != 0 ? 0x03 : 0x00;
		
		int i, j = 0, k;
		AlOneParagraph ap;
		int par_num = findParagraphByPos(0, par.size(), pos);		
		ap = par.get(par_num);
		getPreparedParagraph0(par_num, ap);
		
		char style = (char)(ap.iType & AlStyles.PAR_STYLE_MASK);	
		char style_image = 0;
		style |= AlStyles.SL_PAR;
		if ((ap.iType & AlStyles.PAR_PREVIOUS_EMPTY_0) != 0)
			style |= AlStyles.SL_PREV_EMPTY_0;
		if ((ap.iType & AlStyles.PAR_PREVIOUS_EMPTY_1) != 0)
			style |= AlStyles.SL_PREV_EMPTY_1;
		if ((ap.iType & AlStyles.PAR_BREAKPAGE) != 0 && preference.sectionNewScreen)
			style |= AlStyles.SL_BREAK;
		long style_par = getParagraphRealStyle(ap.iType);
		long style_par_image_title = 0;
		
		long style_bi = styles.style[InternalConst.STYLES_STYLE_BOLDITALIC];
		long style_b = styles.style[InternalConst.STYLES_STYLE_BOLD];
		long style_i = styles.style[InternalConst.STYLES_STYLE_ITALIC];
		long style_c = styles.style[InternalConst.STYLES_STYLE_CODE];
		long style_s = styles.style[InternalConst.STYLES_STYLE_CSTYLE];
		
		long remap_font = 0;
		long remap_color = 0;
		
		int fletter_cnt = 0;
		
		j = pos - ap.start;
		for (i = 0; i < j; i++) {
			ch = stored_par.data[i];			
			
			if (ch < 0x20) {
				if (fletter_cnt > 0) {
					fletter_cnt--;
				}
				
				switch (ch) {
				case AlStyles.CHAR_SOFTPAR:
					style |= AlStyles.SL_PAR;
					break;
				case AlStyles.CHAR_TITLEIMG_START:
					if (isInlineImage()) {
						isInvisible = true;
					} else {
						style_par_image_title = style_par;
						//style_par = PrefManager.getStyle(PrefManager.STYLE_FOOTNOTES);
						style_par = styles.style[InternalConst.STYLES_STYLE_AUTHOR];
						style_par &= ~(AlStyles.SL_JUST_MASK | AlStyles.SL_MARGL_MASK | AlStyles.SL_MARGR_MASK);
						style_par |= AlStyles.SL_JUST_CENTER | AlStyles.SL_MARGL1 | AlStyles.SL_MARGR1;
						style |= AlStyles.SL_PAR;
						ap.iType |= AlStyles.PAR_SKIPFIRSTLet;
					}
					break;
				case AlStyles.CHAR_TITLEIMG_STOP:
					if (isInlineImage()) {
						isInvisible = false;
					} else {
						style_par = style_par_image_title;
						style_par_image_title = 0;
						style |= AlStyles.SL_PAR;
						ap.iType &= ~AlStyles.PAR_SKIPFIRSTLet;
					}
					break;
				case AlStyles.CHAR_LINK_S:
				case AlStyles.CHAR_IMAGE_S:
					isInvisible = true;
					break;
				case AlStyles.CHAR_LINK_E:
					isInvisible = false;
					break;
				case AlStyles.CHAR_IMAGE_E:
					isInvisible = false;
					break;
				}
			} else
			if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
				style &= AlStyles.PAR_STYLE_ICHARMASK;
				style |= ch & AlStyles.PAR_STYLE_MASK;
			} else
			//if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
			//	
			//} else		
			if (((style & AlStyles.SL_PAR) != 0) && (isInvisible == false) && 
					((ch & AlStyles.STYLE_BASE_MASK) != AlStyles.STYLE_BASE0) &&
					//((ch & AlStyles.STYLE_BASE_MASK) != AlStyles.STYLE_BASE1) &&
				((ch > 0x20) || (ch == AlStyles.CHAR_IMAGE_E))
			   ) {
				
				if ((ap.iType & (AlStyles.MASK_FOR_FLETTER - AlStyles.PAR_FIRSTP - extfl_mask)) == 0 && 
						(style & (AlStyles.PAR_STYLE_MASK - extfl_mask)) == 0) {

					switch (profileType) {						
					case 0x02 :
						if ((ap.iType & (AlStyles.MASK_FOR_FLETTER - extfl_mask)) != AlStyles.PAR_FIRSTP)
							break;
					case 0x03 :
					case 0x01 :
						if (Character.isUpperCase(ch) && 
							(i == stored_par.cpos - 1 || !Character.isUpperCase(stored_par.data[i + 1]))) {
							if (extfl_pend)
								fletter_cnt = isValidFLet(InternalConst.FLET_MODE_LETTER, i + 1, extfl_pend, extfl_mask != 0);
						} else
						if (extfl_pstart && AlUnicode.isDigit(ch)) {
							fletter_cnt = isValidFLet(InternalConst.FLET_MODE_LETTER, i + 1, extfl_pend, extfl_mask != 0);
						} else
						if (extfl_pstart && AlUnicode.isCSSFirstLetter(ch)) {
							fletter_cnt = isValidFLet(InternalConst.FLET_MODE_START, i + 1, extfl_pend, extfl_mask != 0);
						} else
						if (extfl_dialog && AlUnicode.isDashPunctuation(ch)) {
							fletter_cnt = isValidFLet(InternalConst.FLET_MODE_DIALOG, i + 1, extfl_pend, extfl_mask != 0);
						}
						break;
					}
				}
				
				style &= (~(AlStyles.SL_PAR | AlStyles.SL_PREV_EMPTY_0 | AlStyles.SL_BREAK | AlStyles.SL_PREV_EMPTY_1));
			} else
			if (fletter_cnt > 0) {
				fletter_cnt--;
			}
		}
		j = 0; boolean is_link = false;
		while (true) {
			for (; i < stored_par.length; i++) {
				ch = stored_par.data[i];
				style_image = 0;
				if (ch < 0x20) {
					slot_t[j] = 0x00;					
					switch (ch) {
					case AlStyles.CHAR_SOFTPAR:
						style |= AlStyles.SL_PAR;
						break;
					case AlStyles.CHAR_TITLEIMG_START:
						if (isInlineImage()) {
							isInvisible = true;
						} else {
							style_par_image_title = style_par;
							//style_par = PrefManager.getStyle(PrefManager.STYLE_FOOTNOTES);
							style_par = styles.style[InternalConst.STYLES_STYLE_AUTHOR];
							style_par &= ~(AlStyles.SL_JUST_MASK | AlStyles.SL_MARGL_MASK | AlStyles.SL_MARGR_MASK);
							style_par |= AlStyles.SL_JUST_CENTER | AlStyles.SL_MARGL1 | AlStyles.SL_MARGR1;
							style |= AlStyles.SL_PAR;
							ap.iType |= AlStyles.PAR_SKIPFIRSTLet;
						}
						break;
					case AlStyles.CHAR_TITLEIMG_STOP:
						if (isInlineImage()) {
							isInvisible = false;
						} else {
							style_par = style_par_image_title;
							style_par_image_title = 0;
							style |= AlStyles.SL_PAR;
							ap.iType &= ~AlStyles.PAR_SKIPFIRSTLet;
						}
						break;
					case AlStyles.CHAR_LINK_S:
					case AlStyles.CHAR_IMAGE_S:
						isInvisible = true;
						break;
					case AlStyles.CHAR_LINK_E:
						isInvisible = false;
						break;
					case AlStyles.CHAR_IMAGE_E:
						isInvisible = false;
						slot_t[j] = ch;
						style_image = (char) AlStyles.SL_IMAGE;						
						break;
					case AlStyles.CHAR_COVER:
						slot_t[j] = ch;
						break;
					}
				} else
				//if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
				//	slot_t[j] = 0x00;
				//} else
				if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
					// mark last link char if present
					if ((style & AlStyles.SL_LINK) != 0 && (ch & AlStyles.SL_LINK) == 0) {
						for (k = j - 1; k >= 0; k--) {
							if (slot_t[k] > 0x20 || slot_t[k] == AlStyles.CHAR_IMAGE_E) {
								slot_s[k] |= AlStyles.SL_MARKNOTE0;
								break;
							}
						}
					}
					//					
					style &= AlStyles.PAR_STYLE_ICHARMASK;
					style |= ch & AlStyles.PAR_STYLE_MASK;
					slot_t[j] = 0x00;					
				} else {
					slot_t[j] = isInvisible ? 0x00 : ch;
				}
				
				slot_s[j] = style + style_image;
				// style from paragraph types
				if (preference.styleSumm) {
					slot_s[j] |= style_par;
				} else {
					slot_s[j] ^= style_par;
				}
				//
				
				is_link = (style & AlStyles.SL_LINK) != 0;
				if (is_link) {
					slot_s[j] &= AlStyles.SL_COLOR_IMASK;
					slot_s[j] |= AlStyles.SL_COLOR_LINK;
				}
				
				if (fletter_cnt > 0) {
					slot_s[j] |= AlStyles.SL_MARKFIRTSTLETTER;
					slot_s[j] &= AlStyles.SL_MASKSTYLESOVER;
					if ((ch == 0xa0 || ch == 0x20) && profiles.classicFirstLetter && 
						(preference.need_dialog) != 2) {
						slot_t[j] = 0x00;
					} else
					if (ch == 0x20 && profiles.classicFirstLetter)
						slot_t[j] = 0xa0;
					fletter_cnt--;
				} 
				
				if (((style & AlStyles.SL_PAR) != 0) &&
					(isInvisible == false) && (slot_t[j] != 0x00) &&
					((ch > 0x20) || (ch == AlStyles.CHAR_IMAGE_E))) {
					
					if ((ap.iType & (AlStyles.MASK_FOR_FLETTER - AlStyles.PAR_FIRSTP - extfl_mask)) == 0 && 
							(style & (AlStyles.PAR_STYLE_MASK - extfl_mask)) == 0) {

						switch (profileType) {						
						case 0x02 :
							if ((ap.iType & (AlStyles.MASK_FOR_FLETTER - extfl_mask)) != AlStyles.PAR_FIRSTP)
								break;
						case 0x03 :
						case 0x01 :
							if (AlUnicode.isUpperCase(ch) && 
									(i == stored_par.cpos - 1 || !AlUnicode.isUpperCase(stored_par.data[i + 1]))) {
								slot_s[j] |= AlStyles.SL_MARKFIRTSTLETTER;
								slot_s[j] &= AlStyles.SL_MASKSTYLESOVER;
								if (extfl_pend)
									fletter_cnt = isValidFLet(InternalConst.FLET_MODE_LETTER, i + 1, extfl_pend, extfl_mask != 0);
							} else
							if (extfl_pstart && AlUnicode.isDigit(ch)) {
								slot_s[j] |= AlStyles.SL_MARKFIRTSTLETTER;
								fletter_cnt = isValidFLet(InternalConst.FLET_MODE_LETTER, i + 1, extfl_pend, extfl_mask != 0);
							} else
							if (extfl_pstart && AlUnicode.isCSSFirstLetter(ch)) {
								if ((fletter_cnt = isValidFLet(InternalConst.FLET_MODE_START, i + 1, extfl_pend, extfl_mask != 0)) > 0)
									slot_s[j] |= AlStyles.SL_MARKFIRTSTLETTER;
							} else
							if (extfl_dialog && AlUnicode.isDashPunctuation(ch)) {
								if ((fletter_cnt = isValidFLet(InternalConst.FLET_MODE_DIALOG, i + 1, extfl_pend, extfl_mask != 0)) > 0)
									slot_s[j] |= AlStyles.SL_MARKFIRTSTLETTER;
							}
							break;
						}
					}
					
					style &= (~(AlStyles.SL_PAR | AlStyles.SL_PREV_EMPTY_0 | AlStyles.SL_BREAK | AlStyles.SL_PREV_EMPTY_1));
				}
				
				if (((slot_s[j] & AlStyles.SL_MARKFIRTSTLETTER) == 0) && 
						(style & (AlStyles.SL_BOLD | AlStyles.SL_ITALIC | AlStyles.SL_CODE | AlStyles.SL_CSTYLE)) != 0) {
					remap_font = slot_s[j];
					remap_color = slot_s[j];
					
					if ((style & AlStyles.SL_CSTYLE) != 0) {
						switch (((int)style_s) & AlStyles.REMAP_MASKF) {
						case AlStyles.REMAP_TEXTF: if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0) remap_font = style_s | AlStyles.SL_REMAPFONT; break;
						case AlStyles.REMAP_FONTF: if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT) remap_font = style_s; break;	
						case AlStyles.REMAP_ALLF: remap_font = style_s; break;
						}
						
						if (!is_link)
						switch (((int)style_s) & AlStyles.REMAP_MASKC) {
						case AlStyles.REMAP_TEXTC: if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0) remap_color = style_s; break;
						case AlStyles.REMAP_FONTC: if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT) remap_color = style_s; break;	
						case AlStyles.REMAP_ALLC: remap_color = style_s; break;
						}
					}
				
					
					if ((style & 0x03) == AlStyles.SL_BOLD) {
						switch (((int)style_b) & AlStyles.REMAP_MASKF) {
						case AlStyles.REMAP_TEXTF: if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0) remap_font = style_b; break;
						case AlStyles.REMAP_FONTF: if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT) remap_font = style_b; break;	
						case AlStyles.REMAP_ALLF: remap_font = style_b; break;
						}
					
						if (!is_link)
						switch (((int)style_b) & AlStyles.REMAP_MASKC) {
						case AlStyles.REMAP_TEXTC: if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0) remap_color = style_b; break;
						case AlStyles.REMAP_FONTC: if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT) remap_color = style_b; break;	
						case AlStyles.REMAP_ALLC: remap_color = style_b; break;
						}
					}
					
			
					if ((style & 0x03) == AlStyles.SL_ITALIC) {
						switch (((int)style_i) & AlStyles.REMAP_MASKF) {
						case AlStyles.REMAP_TEXTF: if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0) remap_font = style_i; break;
						case AlStyles.REMAP_FONTF: if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT) remap_font = style_i; break;	
						case AlStyles.REMAP_ALLF: remap_font = style_i; break;
						}
						
						if (!is_link)
						switch (((int)style_i) & AlStyles.REMAP_MASKC) {
						case AlStyles.REMAP_TEXTC: if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0) remap_color = style_i; break;
						case AlStyles.REMAP_FONTC: if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT) remap_color = style_i; break;	
						case AlStyles.REMAP_ALLC: remap_color = style_i; break;
						}
					}
					
					if ((style & 0x03) == AlStyles.SL_ITALIC + AlStyles.SL_BOLD) {
						switch (((int)style_bi) & AlStyles.REMAP_MASKF) {
						case AlStyles.REMAP_TEXTF: if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0) remap_font = style_bi; break;
						case AlStyles.REMAP_FONTF: if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT) remap_font = style_bi; break;	
						case AlStyles.REMAP_ALLF: remap_font = style_bi; break;
						}
						
						if (!is_link)
						switch (((int)style_bi) & AlStyles.REMAP_MASKC) {
						case AlStyles.REMAP_TEXTC: if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0) remap_color = style_bi; break;
						case AlStyles.REMAP_FONTC: if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT) remap_color = style_bi; break;	
						case AlStyles.REMAP_ALLC: remap_color = style_bi; break;
						}
					}
				
					if ((style & AlStyles.SL_CODE) != 0) {
						switch (((int)style_c) & AlStyles.REMAP_MASKF) {
						case AlStyles.REMAP_TEXTF: if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0) remap_font = style_c; break;
						case AlStyles.REMAP_FONTF: if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT) remap_font = style_c; break;	
						case AlStyles.REMAP_ALLF: remap_font = style_c; break;
						}
						
						if (!is_link)
						switch (((int)style_c) & AlStyles.REMAP_MASKC) {
						case AlStyles.REMAP_TEXTC: if ((ap.iType & AlStyles.MASK_FOR_REMAPTEXT) == 0) remap_color = style_c; break;
						case AlStyles.REMAP_FONTC: if ((slot_s[j] & AlStyles.SL_FONT_MASK) == AlStyles.SL_FONT_TEXT) remap_color = style_c; break;	
						case AlStyles.REMAP_ALLC: remap_color = style_c; break;
						}
					}
					
					slot_s[j] &= AlStyles.SL_FONT_IMASK & AlStyles.SL_COLOR_IMASK;// & AlStyles.SL_SIZE_IMASK;
					slot_s[j] |= (remap_font & (AlStyles.SL_FONT_MASK | AlStyles.SL_REMAPFONT/* AlStyles.SL_SIZE_MASK*/)) | 
							(remap_color & AlStyles.SL_COLOR_MASK);					
				}
				
				j++;
				if (j == AlFiles.LEVEL1_FILE_BUF_SIZE)
					return j;
			}
			
			fletter_cnt = 0;
			
			par_num++;
			if (par_num == par.size()) {
				return j;
			}
			ap = par.get(par_num);
			getPreparedParagraph0(par_num, ap);
			
			style = (char)(ap.iType & AlStyles.PAR_STYLE_MASK);
			style |= AlStyles.SL_PAR;
			if ((ap.iType & AlStyles.PAR_PREVIOUS_EMPTY_0) != 0)
				style |= AlStyles.SL_PREV_EMPTY_0;
			if ((ap.iType & AlStyles.PAR_PREVIOUS_EMPTY_1) != 0)
				style |= AlStyles.SL_PREV_EMPTY_1;
			if ((ap.iType & AlStyles.PAR_BREAKPAGE) != 0 && preference.sectionNewScreen)
				style |= AlStyles.SL_BREAK;
			
			style_par = getParagraphRealStyle(ap.iType);
			
			i = 0;
		}
	}

	int isValidFLet(int flagLetter, int start, boolean punctation_end, boolean bi_mask) {
		int res = 0, res2 = 0;
		switch (flagLetter) {
		case InternalConst.FLET_MODE_LETTER:
			for (; start < stored_par.length; start++)  {
				if (bi_mask && 
						(stored_par.data[start] & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0 &&
						(stored_par.data[start] & (AlStyles.PAR_STYLE_MASK - 0x03)) == 0) {
					res++;
					if (start == stored_par.length - 1)
						return 0;
					continue;
				} else
				if (stored_par.data[start] == 0x00) {
					res++;
					if (start == stored_par.length - 1)
						return 0;
					continue; 
				} else					
				if (AlUnicode.isCSSFirstLetter(stored_par.data[start])) {
					return res + 1;
				} else 
					return 0;					
			}	
			break;
		case InternalConst.FLET_MODE_DIALOG:	
		case InternalConst.FLET_MODE_START:
			for (; start < stored_par.length; start++)  {
				if (bi_mask && 
						(stored_par.data[start] & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0 &&
						(stored_par.data[start] & (AlStyles.PAR_STYLE_MASK - 0x03)) == 0) {
					res++;
					if (start == stored_par.length - 1)
						return 0;
					continue;
				} else
				if (stored_par.data[start] == 0x00) {
					res++;
					if (start == stored_par.length - 1)
						return 0;
					continue; 
				} else 	
				if (stored_par.data[start] == 0xa0 || stored_par.data[start] == 0x20) {
					res++;
					if (start == stored_par.length - 1)
						return 0;
					continue; 
				} else 
				if (Character.isUpperCase(stored_par.data[start])) {
					res++;
					if (!punctation_end)
						return res;
					start++;
					break;
				} else
					return 0;
			}
			
			for (; start < stored_par.length; start++)  {
				if (bi_mask && 
						(stored_par.data[start] & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0 &&
						(stored_par.data[start] & (AlStyles.PAR_STYLE_MASK - 0x03)) == 0) {
					res2++;
					if (start == stored_par.length - 1)
						return res;
					continue;
				} else
				if (stored_par.data[start] == 0x00) {
					res2++;
					if (start == stored_par.length - 1)
						return res;
					continue; 
				} else 	
				if (AlUnicode.isCSSFirstLetter(stored_par.data[start])) {
					return res + res2 + 1;
				} else 
					break;				
			}	
			
			return res;
		}
		return 0;
	}

	long getParagraphRealStyle(long s) {
		long res = AlStyles.LDEFAULT_PAR_STYLE;
		
		if ((s & AlStyles.PAR_NATIVEJUST) != 0) {
			if ((s & AlStyles.PAR_PRE) != 0) {
				res =  styles.style[InternalConst.STYLES_STYLE_PRE];
			} else
			if ((s & AlStyles.PAR_CITE) != 0) {
				res =  styles.style[InternalConst.STYLES_STYLE_CITE];
			} else
			if ((s & AlStyles.PAR_EPIGRAPH) != 0) {
				res =  styles.style[InternalConst.STYLES_STYLE_EPIGRAPH];
			} else
			if ((s & AlStyles.PAR_ANNOTATION) != 0) {
				res =  styles.style[InternalConst.STYLES_STYLE_ANNOTATION];
			}
			res &= 0xffffffffffffffffL - AlStyles.SL_JUST_MASK;
			res |= s & AlStyles.SL_JUST_MASK; 
		} else
		if ((s & AlStyles.PAR_PRE) != 0) {
			res =  styles.style[InternalConst.STYLES_STYLE_PRE];
		} else
		if ((s & AlStyles.PAR_TITLE) != 0) {
			res = AlStyles.SL_MARKTITLE | styles.style[InternalConst.STYLES_STYLE_TITLE];
		} else
		if ((s & AlStyles.PAR_SUBTITLE) != 0) {
			res =  AlStyles.SL_MARKTITLE | styles.style[InternalConst.STYLES_STYLE_STITLE];
		} else				
		if (((s & AlStyles.PAR_AUTHOR) != 0) || ((s & AlStyles.PAR_DATE) != 0)) {
			res =  styles.style[InternalConst.STYLES_STYLE_AUTHOR];
		} else
		if (((s & AlStyles.PAR_POEM) != 0) || ((s & AlStyles.PAR_STANZA) != 0) || ((s & AlStyles.PAR_V) != 0)) {
			res =  styles.style[InternalConst.STYLES_STYLE_POEM] | AlStyles.SL_STANZA;
		} else
		if ((s & AlStyles.PAR_CITE) != 0) {
			res =  styles.style[InternalConst.STYLES_STYLE_CITE];
		} else
		if ((s & AlStyles.PAR_COVER) != 0) {
			res =  AlStyles.SL_MARKCOVER;
		} else
		if ((s & AlStyles.PAR_EPIGRAPH) != 0) {
			res =  styles.style[InternalConst.STYLES_STYLE_EPIGRAPH];
		} else
		if ((s & AlStyles.PAR_ANNOTATION) != 0) {
			res =  styles.style[InternalConst.STYLES_STYLE_ANNOTATION];
		} else
		if ((s & AlStyles.PAR_TABLE) != 0) {
			res =  AlStyles.LDEFAULT_PAR_STYLE + AlStyles.SL_SIZE_M1;
		} 
		
		res |= s & AlStyles.PAR_UL_BASE;
		res |= (res & AlStyles.SL_FONT_MASK) >> 8;
		
		return res;
	}

	boolean isInlineImage() {
		int i;
		boolean isInvisible = false;
		char ch;
		for (i = 0; i < stored_par.length; i++) {
			ch = stored_par.data[i];
			if (ch < 0x20) {
				switch (ch) {
				case AlStyles.CHAR_TITLEIMG_START:
					isInvisible = true;
					break;
				case AlStyles.CHAR_TITLEIMG_STOP:
					isInvisible = false;
					break;
				case AlStyles.CHAR_LINK_S:
				case AlStyles.CHAR_IMAGE_S:
					isInvisible = true;
					break;
				case AlStyles.CHAR_LINK_E:
				case AlStyles.CHAR_IMAGE_E:
					isInvisible = false;
					break;
				}
			} else 
			if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
				
			//} else 
			//if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
				
			} else {
				if (isInvisible)
					continue;
				if (ch > 0x20)
					return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {		
		return "\r\n" + ident + " " + size + " symbols " + par.size() + 
			   " paragraph " + " cp:" + Integer.toString(use_cpR0) + "\r\n";		
	}

	public TAL_NOTIFY_RESULT findText(String find) {
		TAL_NOTIFY_RESULT res = TAL_NOTIFY_RESULT.ERROR;

		resfind.clear();
		if (find == null)
			return TAL_NOTIFY_RESULT.OK;

		if (size < 1)
			return TAL_NOTIFY_RESULT.OK;

		StringBuilder sFind = new StringBuilder(find);
		if (sFind.length() >= InternalConst.FIND_LEN)
			return res;

		int i, correct = 0;
		for (i = 0; i < sFind.length(); i++) {
			char ch = sFind.charAt(i);
			
			if (ch == '?') {
				sFind.setCharAt(i, (char)AlStyles.CHAR_ANYCHAR);			
			} else
				if (AlUnicode.isLetter(ch)) {
				sFind.setCharAt(i, (char)Character.toLowerCase(ch));
				correct++;
			} else
			if (AlUnicode.isDigit(ch)) {
				correct++;
			} else
			if (AlUnicode.isPunctuation(ch)) {
				sFind.setCharAt(i, '.');
				correct++;
			} else {
				sFind.setCharAt(i, ' ');
			}
		}

		if (correct < 2)
			return res;


		boolean isInvisible = false;
		int fLen = sFind.length() - 1;
		char ch, lastChar = sFind.charAt(fLen);
		
		char[] stackChar = new char[InternalConst.FIND_LEN];
		int[] stackPos = new int [InternalConst.FIND_LEN];
		int fPos = 0, pos = 0;
		
		int j = 0;
		AlOneParagraph ap;
		int par_num = findParagraphByPos(0, par.size(), pos);
		ap = par.get(par_num);
		getParagraph(ap);
		j = pos - ap.start;
		for (i = 0; i < j; i++) {
			if (stored_par.data[i] < 0x20) {
				switch (stored_par.data[i]) {
				case AlStyles.CHAR_LINK_S:
				case AlStyles.CHAR_IMAGE_S:
					isInvisible = true;
					break;
				case AlStyles.CHAR_LINK_E:
					isInvisible = false;
					break;
				case AlStyles.CHAR_IMAGE_E:
					isInvisible = false;
					break;
				}
			}
		}
		
		while (true) {
			if (i == 0) {
				if (Character.getType(stackChar[(fPos - 1) & InternalConst.FIND_MASK]) != Character.SPACE_SEPARATOR) { 
					stackChar[fPos & InternalConst.FIND_MASK] = ' ';
					stackPos[fPos & InternalConst.FIND_MASK] = ap.start + i;
					fPos++;
				}
			}
			for (; i < stored_par.length; i++) {
				ch = stored_par.data[i];
				
				if (ch < 0x20) {
					switch (ch) {
					case AlStyles.CHAR_TITLEIMG_START:
					case AlStyles.CHAR_LINK_S:
					case AlStyles.CHAR_IMAGE_S:
						isInvisible = true;
						continue;
					case AlStyles.CHAR_TITLEIMG_STOP:
					case AlStyles.CHAR_LINK_E:
					case AlStyles.CHAR_IMAGE_E:
						isInvisible = false;
						continue;
					case AlStyles.CHAR_COVER:
						ch = ' ';
						break;
					default:
						continue;
					}
				} else
				if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0) {
					continue;				
				//} else
				//if ((ch & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE1) {
				//	continue;				
				} else {
					if (isInvisible)
						continue;					
				}
				
				if (AlUnicode.isLetterOrDigit(ch)) {
					ch = Character.toLowerCase(ch);
				} else
				if (AlUnicode.isPunctuation(ch)) {
					ch = '.';
				} else {
					if (Character.getType(ch) != Character.SPACE_SEPARATOR) {
						continue;
					} else
					if (Character.getType(stackChar[(fPos - 1) & InternalConst.FIND_MASK]) != Character.SPACE_SEPARATOR) { 
						ch = ' ';
					} else {
						continue;
					}
				}
				
				//
				stackChar[fPos & InternalConst.FIND_MASK] = ch;
				stackPos[fPos & InternalConst.FIND_MASK] = ap.start + i;
				
				if (ch == lastChar) {
					for (j = 1; j <= fLen; j++) {
						if (stackChar[(fPos - j) & InternalConst.FIND_MASK] != sFind.charAt(fLen - j) && sFind.charAt(fLen - j) != AlStyles.CHAR_ANYCHAR) {
							break;
						} else 
						if (j == fLen) {
							AlOneSearchResult a = new AlOneSearchResult();
							a.pos_start = stackPos[(fPos - j) & InternalConst.FIND_MASK];
							a.pos_end = stackPos[fPos & InternalConst.FIND_MASK];
							resfind.add(a);
							res = TAL_NOTIFY_RESULT.OK;
						}
					}
				}
				fPos++;
			}
			
			par_num++;
			if (par_num == par.size())
				break;		
			ap = par.get(par_num);
			getParagraph(ap);
			i = 0;
		}

		return res;
	}

	static boolean getTestBuffer(AlFiles a, int cp, char[] dst, int len) {

		byte[] buf = new byte [len << 2];
		int cnt = a.getByteBuffer(0, buf, len << 2);
		Integer opos = 0;
		AlIntHolder ipos = new AlIntHolder(0);		

		while ((ipos.value < cnt) && (opos < len))
			dst[opos++] = AlUnicode.byte2Wide(cp, buf, ipos);
		buf = null;

		return opos > 0;
	}

	public int getNumParagraphByPoint(int pos) {
		if (pos < 0)
			return 0;
		if (pos >= size)
			return par.size() - 1;
		return findParagraphByPos(0, par.size(), pos);		
	}

	public int getStartPragarphByNum(int num) {
		return par.get(num).start;		
	}

	public long getStylePragarphByNum(int num) {
		return par.get(num).iType;		
	}
	
	public String getLinkNameByPos(int pos, boolean getLink) {
		StringBuilder res = new StringBuilder();
		
		if (pos < 0 | pos >= size)
			return null;
		
		final char startChar = (char) (getLink ? AlStyles.CHAR_LINK_S : AlStyles.CHAR_IMAGE_S);
		final char endChar = (char) (getLink ? AlStyles.CHAR_LINK_E : AlStyles.CHAR_IMAGE_E);
		
		AlOneParagraph ap;
		int par_num = findParagraphByPos(0, par.size(), pos);
		int j;
		boolean fl = false;
		while (par_num >= 0) {
			ap = par.get(par_num);
			getParagraph(ap);
			j = pos - ap.start;
			if (j >= ap.length)
				j = ap.length - 1;
			
			for (; j >= 0; j--) {				
				if (fl) {
					if (stored_par.data[j] == startChar) {
						if (res.length() < 1)
							return null;
						if (res.charAt(0) == '#')
							res.delete(0, 1);
						if (res.length() < 1)
							return null;
						return res.toString();
					} else {
						if (res.length() == 0) {
							res.append(stored_par.data[j]);
						} else {
							res.insert(0, stored_par.data[j]);
						}
					}
				} else {
					if (stored_par.data[j] == endChar)
						fl = true;
				}
			}
			
			if (fl)
				return null;
			
			par_num--;
		}
		
		return null;
	}

	public AlOneImage getImageByName(String name) {
		if (LEVEL2_COVERTOTEXT_STR.equalsIgnoreCase(name)) {
			if (coverName == null)
				return null;
			name = coverName;
		}
		
		if (im != null) {			
			for (int i = 0; i < im.size(); i++) {
				if (name.equalsIgnoreCase(im.get(i).name))
					return im.get(i);
			}
		}
		
		/*if (tableToText.equalsIgnoreCase(name)) {
			AlImage al = AlImage.addImage(tableToText, 0, 0, AlImage.IMG_TABLE + AlImage.IMG_UNKNOWN);
			if (addImage(al))
				return im0.get(im0.size() - 1);
			return null;
		}
		
		if (addImage(AlImage.addImage(name, 0, 0, AlImage.NOT_EXTERNAL_IMAGE))) {
			if (aFiles.getExternalImage(im0.get(im0.size() - 1))) {
				return im0.get(im0.size() - 1);
			}
		}*/
		
		return null;
	}


	public AlOneLink getLinkByName(String name, boolean internalOnly) {
		if (lnk == null && internalOnly)
			return null;
		
		int i;
		if (lnk != null)
			for (i = 0; i < lnk.size(); i++) {
				if (name.equalsIgnoreCase(lnk.get(i).name))
					return lnk.get(i);
			}
		
		/*if (internalOnly)
			return null;
		
		{
			String nameLink = name;
			String metka = null;
			
			int indexMetki = name.lastIndexOf('#');
			int indexSlash = name.lastIndexOf('/');
			int indexDouble = name.lastIndexOf(':');
			if (indexMetki > 0 && indexMetki > indexSlash && indexMetki > indexDouble) {			
				String currName = name.substring(0, indexMetki);
				int tmp = currName.lastIndexOf('.');
				if (tmp > 0) {
					currName = currName.substring(tmp, indexMetki);
					if (AlReader3GridOpenFile.isValidFileExt(currName.toLowerCase())) {
						metka = name.substring(indexMetki + 1, name.length());
						nameLink = name.substring(0, indexMetki);
					}
				}
			}
			
			nameLink = aFiles.externalFileExists(nameLink);
			if (nameLink != null) {
				AlLink a = new AlLink();
				a.name = "file://" + nameLink;
				a.iType = AlLink.LINK_TEXT;
				a.positionE = a.positionS = -1;
				if (metka != null)
					a.name = a.name + "#" + metka;
				return a;
			}
		}*/
		
		return null;
	}

	public TAL_NOTIFY_RESULT createDebugFile(String pathForDebug) {
		TAL_NOTIFY_RESULT res;
		res = aFiles.createDebugFile(pathForDebug);
		if (res == TAL_NOTIFY_RESULT.ERROR)
			return res;
		
		AlRandomAccessFile df = new AlRandomAccessFile();	
		
		String ustr;
		byte[] bb = null;
		
		String tmp = pathForDebug + "_taldeb.f";		
		
		if (df.open(tmp, 1) == TAL_RESULT.OK) {
			
			ustr = (char)0xfeff + aFiles.getFullRealName() + "\n\r";
			try {
				bb = ustr.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			df.write(bb);	
			
			ustr = aFiles.toString() + this.toString();
			try {
				bb = ustr.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			df.write(bb);	

			ustr = "\n\rCalculation page time: " + lastCalcTime;
			try {
				bb = ustr.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			df.write(bb);
			ustr = "\n\rCalculation page count: " + lastPageCount;
			try {
				bb = ustr.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			df.write(bb);			

			if (bookTitle != null) {
				ustr = "\n\rTitle: \"" + bookTitle + "\"";
				try {
					bb = ustr.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				df.write(bb);
			}

			if (bookAuthors.size() > 0) {
				ustr = "\n\rAuthors: "; 
				for (int i = 0; i < bookAuthors.size(); i++) {
					ustr += "\"" + bookAuthors.get(i) + "\" ";
				}
				try {
					bb = ustr.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				df.write(bb);
			}

			if (bookGenres.size() > 0) {
				ustr = "\n\rGenres: "; 
				for (int i = 0; i < bookGenres.size(); i++) {
					ustr += "\"" + bookGenres.get(i) + "\" ";
				}
				try {
					bb = ustr.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				df.write(bb);
			}

			if (bookSeries.size() > 0) {
				ustr = "\n\rSeries: "; 
				for (int i = 0; i < bookSeries.size(); i++) {
					ustr += "\"" + bookSeries.get(i) + "\" ";
				}
				ustr += "\n\r";
				try {
					bb = ustr.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				df.write(bb);
			}

			if (lnk.size() > 0) {
				ustr = "\n\rLinks:";
				try {
					bb = ustr.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				df.write(bb);
				for (int i = 0; i < lnk.size(); i++) {
					ustr = "\n\r";
					ustr += lnk.get(i).toString();
					try {
						bb = ustr.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					df.write(bb);
				}
			}

			if (im.size() > 0) {
				ustr = "\n\r\n\rImages:";
				try {
					bb = ustr.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				df.write(bb);
				for (int i = 0; i < im.size(); i++) {
					ustr = "\n\r";
					ustr += im.get(i).toString();
					try {
						bb = ustr.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					df.write(bb);
				}
			}

			if (ta.size() > 0) {
				ustr = "\n\r\n\rTables:";
				try {
					bb = ustr.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				df.write(bb);
				for (int i = 0; i < ta.size(); i++) {
					ustr = "\n\r";
					ustr += ta.get(i).toString();
					try {
						bb = ustr.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					df.write(bb);
				}
			}

			if (ttl.size() > 0) {
				ustr = "\n\r\n\rContent:";
				try {
					bb = ustr.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				df.write(bb);
				for (int i = 0; i < ttl.size(); i++) {
					ustr = "\n\r";
					ustr += ttl.get(i).toString();
					try {
						bb = ustr.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					df.write(bb);
				}
			}

			//////////////////////////////
			int c = 0;
			for (int i = 0; i < par.size(); i++) {
				c++;
				ustr = "\n\r\n\r" + par.get(i).toString() + "\n\r";
				try {
					bb = ustr.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				df.write(bb);
								
				getParagraph(par.get(i));
				ustr = String.copyValueOf(stored_par.data, 0, par.get(i).length);
				try {
					bb = ustr.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				df.write(bb);			
			}
			c--;


			df.close();
		} else {
			res = TAL_NOTIFY_RESULT.ERROR;
		}
		if (df != null)
			df = null;

		return res;
	}
	
	abstract protected 	void doTextChar(char ch, boolean addSpecial);
	abstract protected  void prepareCustom();
	abstract protected  void parser(final int start_pos, final int stop_pos);
	
	abstract public  void initState(AlBookOptions bookOptions, AlFiles myParent, 
			AlPreferenceOptions pref, AlStylesOptions stl);

}
