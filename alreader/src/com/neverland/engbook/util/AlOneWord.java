package com.neverland.engbook.util;

import com.neverland.engbook.forpublic.EngBookMyType;

public class AlOneWord {
	
	public int		count;
	public int		complete;
	public Integer	need_flags;
	public int[]  	pos = new int [EngBookMyType.AL_WORD_LEN + 2];		
	public int[]	base_line_up = new int [EngBookMyType.AL_WORD_LEN + 2];
	public int[]	base_line_down = new int [EngBookMyType.AL_WORD_LEN + 2];		
	public long[]	style = new long [EngBookMyType.AL_WORD_LEN + 2];
	public char[]	text = new char [EngBookMyType.AL_WORD_LEN + 2];
	public byte[]	hyph = new byte [EngBookMyType.AL_WORD_LEN + 4];
	public int[]	width = new int [EngBookMyType.AL_WORD_LEN + 2];
}
