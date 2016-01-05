package com.neverland.engbook.level2;

import com.neverland.engbook.util.AlStyles;

public class AlOneParagraph {
	public int				positionS;
	public int				positionE;
	public int				start;
	public int				length;
	public long				iType;
	public int				addon;
	//vector<int64_t>		stack;
	//vector<TAL_CODE_PAGES>	cp;
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		switch ((int)(iType & (AlStyles.PAR_PREVIOUS_EMPTY_0 | AlStyles.PAR_PREVIOUS_EMPTY_1))) {
		case AlStyles.PAR_PREVIOUS_EMPTY_0 | AlStyles.PAR_PREVIOUS_EMPTY_1: 
										   	s.append("*@\r\n"); break;
		case AlStyles.PAR_PREVIOUS_EMPTY_0: s.append("*\r\n"); break;
		case AlStyles.PAR_PREVIOUS_EMPTY_1: s.append("@\r\n"); break;		
		}
		
		s.append(String.valueOf(positionS) + '/' + positionE + '/'+ 
				start + '/' + length + "/0x" + Long.toHexString(iType) + '/' + Integer.toHexString(addon) + '/' + 0/*(stack == null ? '0' : '1')*/);
		return s.toString();
	}
}
