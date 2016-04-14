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
		
		s.append(String.valueOf(positionS)).append('/').append(positionE).append('/').append(start).append('/').
				append(length).append("/0x").append(Long.toHexString(iType)).append('/').
				append(Integer.toHexString(addon)).append('/').append(0);
		return s.toString();
	}
}
