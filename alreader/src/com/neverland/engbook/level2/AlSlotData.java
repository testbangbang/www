package com.neverland.engbook.level2;

import android.util.Log;

import com.neverland.engbook.level1.AlFiles;

public class AlSlotData {
	public int 		active 	= 0;
	public int 		shtamp 	= -1;
	public int[] 	start 	= { 0,  0};
	public int[] 	end	= {-1, -1};
	public char[] 	txt[] 	= {null, null};
	public long[] 	stl[] 	= {null, null};
	
	public final void initBuffer() {
		if (txt[active] == null) {
			txt[active] = new char [AlFiles.LEVEL1_FILE_BUF_SIZE];
		}
		if (stl[active] == null) {
			stl[active] = new long [AlFiles.LEVEL1_FILE_BUF_SIZE];
		}
	}
}
