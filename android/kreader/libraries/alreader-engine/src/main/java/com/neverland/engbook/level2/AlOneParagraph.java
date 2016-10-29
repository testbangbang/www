package com.neverland.engbook.level2;

import com.neverland.engbook.util.AlStyles;

public class AlOneParagraph {
	public int				positionS;
	public int				positionE;
	public int				start;
	public int				length;
	public long				iType;
	public int				addon;
	public long				level;
	public long 			stack[] = null;
	public int  			cp[] = null;

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
				append(Integer.toHexString(addon)).append('/').
				append(Integer.toString((stack != null ? 1 : 0) + (cp != null ? 2 : 0))).append('/').
				append(Integer.toString((int) (level >> 31))).append('/').
				append(Integer.toString((int) (level & 0x7fffffff)));
		return s.toString();
	}
}
