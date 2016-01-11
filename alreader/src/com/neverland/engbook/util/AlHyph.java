package com.neverland.engbook.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.app.Application;

import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_HYPH_LANG;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;

public class AlHyph {
	
	private AlEngineOptions     opt = null;
	
	private int 				LetterCount = 0;
	private int 				PatternLen = 0;
	
	private static final char[]	isLetterLower = new char[0x3000];
	private static final int[]	allLetter = new int[0x3000];
	
	//private HashMap<Character, Integer> allLetter = null;
	char[]						pattern = null;
	private byte[] 				mask = null;
	char[]						wordIn = new char [EngBookMyType.AL_WORD_LEN * 2 + 3];
	private int[]				wordAdr = new int [EngBookMyType.AL_WORD_LEN * 2 + 3];
	private int					space_adr = -1;

	TAL_HYPH_LANG				lang = TAL_HYPH_LANG.NONE;
	
	static {
		for (int i = 0; i < 0x3000; i++) {
			isLetterLower[i] = 0x00;
			if (i == AlStyles.CHAR_IMAGE_E) {
				isLetterLower[i] = AlStyles.CHAR_IMAGE_E;
			} else
			if (AlUnicode.isHyphWordChar((char) i)) {
				isLetterLower[i] = AlStyles.CHAR_IMAGE_E;
			} else
			if (AlUnicode.isLetter((char)i)) {
				isLetterLower[i] = (char) Character.toLowerCase(i);
			}
		}		
	}
	
	private final int getAdrHyphPattern(char let) {
		/*if (allLetter == null)
			return -1;
		Integer adr = allLetter.get(let);
		if (adr == null)
			adr = -1;		
		return adr;*/
		
		return allLetter[let];
	}

	public void getHyph(final char[] text, final byte[] hyph, final int count, AlIntHolder flag) {
		int i, j;
		wordIn[0] = 0x20; wordAdr[0] = space_adr; hyph[0] = InternalConst.TAL_HYPH_INPLACE_DISABLESPACE;//'A';
		for (i = 0, j = 1; i < count; i++, j++) {
			if (text[i] >= 0x3000) {
				wordIn[j]  = InternalConst.TAL_HYPH_INPLACE_DISABLESPACE;//'A';			
				wordAdr[j] = space_adr; 
				if (((i == count - 1) || isLetterLower[text[j]] > 0x20)) {
					hyph[j] = InternalConst.TAL_HYPH_INPLACE_PREDISABLE;//'B';
				} else {					
					if (i < count - 2 && (text[j] == 8220 || text[j] == 8221)) {
						hyph[j] = InternalConst.TAL_HYPH_INPLACE_PREDISABLE;//'B';
					} else 
						hyph[j] = '8';
				}
			} else {
				switch (isLetterLower[text[i]]) {
				case 0x00:
					hyph[j] = '8';
					wordIn[j]  = ' ';			
					wordAdr[j] = space_adr;
					break;				
				case AlStyles.CHAR_IMAGE_E:
					wordIn[j]  = ' ';			
					wordAdr[j] = space_adr;
					hyph[j] = InternalConst.TAL_HYPH_INPLACE_PREDISABLE;//'B';
					break;
				default:
					hyph[j] = InternalConst.TAL_HYPH_INPLACE_DISABLE;//'0';
					wordIn[j]  = isLetterLower[text[i]];
					wordAdr[j] = getAdrHyphPattern(wordIn[j]);
					break;
				}
			}
		}
		/*for (i = 0, j = 1; i < count; i++, j++) {		
			if (text[i] >= 0x3000) {
				wordIn[j]  = InternalConst.TAL_HYPH_INPLACE_DISABLESPACE;//'A';			
				wordAdr[j] = space_adr; 
				if (((i == count - 1) || isLetterLower[text[j]] != 0x00)) {
					hyph[j] = InternalConst.TAL_HYPH_INPLACE_PREDISABLE;//'B';
				} else {					
					if (i < count - 2 && (text[j] == 8220 || text[j] == 8221)) {
						hyph[j] = InternalConst.TAL_HYPH_INPLACE_PREDISABLE;//'B';
					} else 
						hyph[j] = '8';
				}
			} else
			if (isLetterLower[text[i]] != 0) {
				hyph[j] = InternalConst.TAL_HYPH_INPLACE_DISABLE;//'0';
				wordIn[j]  = isLetterLower[text[i]];
				wordAdr[j] = getAdrHyphPattern(wordIn[j]);
			} else
			if (text[i] == AlStyles.CHAR_IMAGE_E) {
				wordIn[j]  = ' ';			
				wordAdr[j] = space_adr;
				hyph[j] = InternalConst.TAL_HYPH_INPLACE_PREDISABLE;//'B';
			} else
			if (AlUnicode.isHyphWordChar(text[i])) {
				wordIn[j]  = ' ';			
				wordAdr[j] = space_adr;
				hyph[j] = InternalConst.TAL_HYPH_INPLACE_PREDISABLE;//'B';
			} else {
				hyph[j] = '8';
				wordIn[j]  = ' ';			
				wordAdr[j] = space_adr;
			}
		}*/
		wordIn[count + 1] = 0x20; wordAdr[count + 1] = space_adr;
		hyph[1] = InternalConst.TAL_HYPH_INPLACE_DISABLESPACE;//'A';
		for (i = 1; i <= count + 1; i++) {
			if (wordIn[i] == ' ') {
				if (i + 1 <= count && hyph[i + 1] != InternalConst.TAL_HYPH_INPLACE_PREDISABLE/*'B'*/)
					hyph[i + 1] = InternalConst.TAL_HYPH_INPLACE_DISABLESPACE;//'A';
				if (i - 1 > 0 && hyph[i - 1] != InternalConst.TAL_HYPH_INPLACE_PREDISABLE/*'B'*/)
					hyph[i - 1] = InternalConst.TAL_HYPH_INPLACE_DISABLESPACE;//'A';
				if (i - 2 > 0 && hyph[i - 2] != InternalConst.TAL_HYPH_INPLACE_PREDISABLE/*'B'*/)
					hyph[i - 2] = InternalConst.TAL_HYPH_INPLACE_DISABLESPACE;//'A';
			}
		}	
		
		if (pattern != null) {
			int	start_adr = 0;
			int letter_num;
			int len_pattern;			
			
			next_letter:
				for (letter_num = count; letter_num >= 0; letter_num--) {
					start_adr = wordAdr[letter_num];
					if (start_adr == -1)
						continue;
					//
					len_pattern = pattern[start_adr];
					if (len_pattern == 1) {
						if (letter_num > 0 && mask[start_adr] > hyph[letter_num - 1])
							hyph[letter_num - 1] = mask[start_adr];
						if (mask[start_adr + 1] > hyph[letter_num])
							hyph[letter_num] = mask[start_adr + 1];
					}
					//
					
					while (true) {	
						if (start_adr >= PatternLen)
							break;
						
						len_pattern = pattern[start_adr];	
						if (pattern[start_adr + 1] != wordIn[letter_num])						
							continue next_letter;
						
						
						if (len_pattern <= count - letter_num + 2) {
							for (i = 1; i < len_pattern; i++) {
								//int diff = pattern[start_adr + i + 1] - wordIn[letter_num + i];
								
								if (pattern[start_adr + i + 1] < wordIn[letter_num + i]) {
									break;
								} else
								if (pattern[start_adr + i + 1] > wordIn[letter_num + i]) {
									continue next_letter;
								} else
								
								/*if (diff < 0) {
									break;
								} else
								if (diff > 0) {
									continue next_letter;
								} else*/
								
								if (i == len_pattern - 1) {
									/*diff = 0;
									if (letter_num == 0)
										diff++;*/
									for (j = (letter_num == 0 ? 1 : 0)/*diff*/; j <= len_pattern; j++) {
										if (mask[start_adr + j] > hyph[letter_num + j - 1])
											hyph[letter_num + j - 1] = mask[start_adr + j];
									}
								}
							}
						}

						start_adr += len_pattern + 1;
					}
				}
		}
		
		for (i = 1; i <= count; i++) {
			switch (hyph[i]) {			
			case '1': case '3': case '5': case '7': case '9':
				hyph[i] = InternalConst.TAL_HYPH_INPLACE_ENABLE;//'-';
				break;
			case '0': case '2': case '4': case '6': case '8': 
				hyph[i] = InternalConst.TAL_HYPH_INPLACE_DISABLE;//'0';
				break;			
			}			
		}		

		flag.value |= InternalConst.AL_ONEWORD_FLAG_DOHYPH;
	}

	public AlHyph() {
		
	}

	@Override
	public void finalize() {
		unload();
	}

	void unload() {
		for (int i = 0; i < 0x3000; i++)
			allLetter[i] = -1;
		/*if (allLetter != null)
			allLetter.clear();
		allLetter = null;*/
		pattern = null;
		mask = null;
	}

	public int init(AlEngineOptions engOptions) {
		lang = TAL_HYPH_LANG.NONE;
		opt = engOptions;
		setLang(engOptions.hyph_lang);		
		return TAL_RESULT.OK;
	}

	void setLang(TAL_HYPH_LANG lng) {
		if (lang == lng)
			return;

		unload();
		lang = lng;

		String file = null;
		switch (lng) {		
		case RUSSIAN:
			file = "Russian.pattern";
			break;
		case ENGLISH:
			file = "English.pattern";
			break;
		case ENGRUS:
			file = "Russian-English.pattern";
			break;
		default:
			return;
		}

		InputStream is = null;
		int	tmp;
		try {
			is = ((Application)opt.appInstance).getAssets().open(file);

			tmp = is.read();
			tmp += is.read() << 8;
			tmp += is.read() << 16;
			tmp += is.read() << 24;
			
			if (tmp == 0x30686c61) {
			
				PatternLen = is.read();
				PatternLen += is.read() << 8;
				PatternLen += is.read() << 16;
				PatternLen += is.read() << 24;
				
				LetterCount = is.read();
				LetterCount += is.read() << 8;
				LetterCount += is.read() << 16;
				LetterCount += is.read() << 24;
				
				if ((PatternLen > 0) && (PatternLen <= 0x000fffff) &&
					(LetterCount > 0) && (LetterCount <= 255)) {
					
					for (int i = 0; i < 0x3000; i++)
						allLetter[i] = -1;
					
					//allLetter = new HashMap<Character, Integer>(LetterCount);
					char c = ' '; int i;
					for (i = 0; i < LetterCount; i++) {				
						c = (char)is.read();
						c += (char)is.read() << 8;
						
						tmp = is.read();
						tmp += is.read() << 8;
						tmp += is.read() << 16;
						tmp += is.read() << 24;				
						
						if (c < 0x3000)
							allLetter[c] = tmp;
						//allLetter.put(c, tmp);
					}
					
					pattern = new char [PatternLen + 1];
					mask = new byte [PatternLen + 1];
					
					for (i = 0; i < PatternLen; i++) {				
						c = (char)is.read();
						c += (char)is.read() << 8;						
						pattern[i] = c;
					}
					pattern[PatternLen] = 0x00;
					
					is.read(mask);
					
					mask[PatternLen] = 0x00;
				}
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();			
		} 
		is = null;
	}


}
