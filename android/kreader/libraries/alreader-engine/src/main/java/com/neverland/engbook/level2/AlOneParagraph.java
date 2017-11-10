package com.neverland.engbook.level2;

import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlStyles;

public class AlOneParagraph {
	public int				positionS;
	public int				positionE;
	public int				start;
	public int				length;

	public long				paragraph;
	public long				prop;
	public long				level;

	public int				table_start = -1;
	public int				table_counter = 0;

	public char				ptext[] = null;
	public boolean  		is_prepared = false;

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		switch ((int)((prop & (AlParProperty.SL2_EMPTY_BEFORE | AlParProperty.SL2_BREAK_BEFORE)) >> 32L)) {
		case (int) ((AlParProperty.SL2_EMPTY_BEFORE | AlParProperty.SL2_BREAK_BEFORE) >> 32L):
										   	s.append("*@\r\n"); break;
		case (int) (AlParProperty.SL2_EMPTY_BEFORE >> 32L): s.append("*\r\n"); break;
		case (int) (AlParProperty.SL2_BREAK_BEFORE >> 32L): s.append("@\r\n"); break;
		}

		s.append(String.format("%d/%d/%d/%d 0x%016x-0x%016x %d %d %d",
				positionS, positionE, start, length, paragraph, prop, level, table_start, table_counter));
		/*s.append(String.valueOf(positionS)).append('/').append(positionE).append('/').append(start).append('/').
				append(length).append("/0x").append(Long.toHexString(paragraph)).append('/').append(Long.toHexString(prop)).append('/').
				append(Long.toString(level)).append('/').
				append(Integer.toString(table_start)).append('/').
				append(Integer.toHexString(table_counter));*/
		return s.toString();
	}
}
