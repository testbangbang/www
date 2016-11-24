package com.neverland.engbook.util;

import com.neverland.engbook.forpublic.EngBookMyType;

public class AlOneWord {
	
	public int		count;
	public int		complete;
	public Integer	need_flags;
	public final int[]  	pos = new int [EngBookMyType.AL_WORD_LEN + 2];
	public final int[]	base_line_up = new int [EngBookMyType.AL_WORD_LEN + 2];
	public final int[]	base_line_down = new int [EngBookMyType.AL_WORD_LEN + 2];
	public final long[]	style = new long [EngBookMyType.AL_WORD_LEN + 2];
	public final char[]	text = new char [EngBookMyType.AL_WORD_LEN + 2];
	public final byte[]	hyph = new byte [EngBookMyType.AL_WORD_LEN + 4];
	public final int[]	width = new int [EngBookMyType.AL_WORD_LEN + 2];
}
