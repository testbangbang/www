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
	public int              blockSize = Integer.MAX_VALUE;
	public boolean			dataState = false;

	public void copy(AlOneParagraph alOneParagraph){
		this.positionS = alOneParagraph.positionS;
		this.positionE = alOneParagraph.positionE;
		this.start = alOneParagraph.start;
		this.paragraph = alOneParagraph.paragraph;
		this.prop = alOneParagraph.prop;
		this.level = alOneParagraph.level;
		this.table_start = alOneParagraph.table_start;
		this.table_counter = alOneParagraph.table_counter;
		this.blockSize = alOneParagraph.blockSize;
		this.dataState = alOneParagraph.dataState;

		if (this.ptext == null || this.length != alOneParagraph.length) {
			this.ptext = new char[alOneParagraph.length];
		}
		this.length = alOneParagraph.length;
		System.arraycopy(alOneParagraph.ptext,0,this.ptext,0,alOneParagraph.length);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		switch ((int)((prop & (AlParProperty.SL2_EMPTY_BEFORE | AlParProperty.SL2_BREAK_BEFORE)) >> 32L)) {
		case (int) ((AlParProperty.SL2_EMPTY_BEFORE | AlParProperty.SL2_BREAK_BEFORE) >> 32L):
										   	s.append("*@\r\n"); break;
		case (int) (AlParProperty.SL2_EMPTY_BEFORE >> 32L): s.append("*\r\n"); break;
		case (int) (AlParProperty.SL2_BREAK_BEFORE >> 32L): s.append("@\r\n"); break;
		}

		s.append(String.format("positionS:%d,positionE:/%d,start:/%d,length:/%d,paragraph,prop: 0x%016x-0x%016x ,level:%d,table_start: %d,table_counter: %d",
				positionS, positionE, start, length, paragraph, prop, level, table_start, table_counter));
		/*s.append(String.valueOf(positionS)).append('/').append(positionE).append('/').append(start).append('/').
				append(length).append("/0x").append(Long.toHexString(paragraph)).append('/').append(Long.toHexString(prop)).append('/').
				append(Long.toString(level)).append('/').
				append(Integer.toString(table_start)).append('/').
				append(Integer.toHexString(table_counter));*/
		return s.toString();
	}
}
