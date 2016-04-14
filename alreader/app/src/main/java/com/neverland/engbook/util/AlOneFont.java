package com.neverland.engbook.util;

import java.io.File;

public class AlOneFont {
	
	public final String aName;
	public File[] aFile = {null, null, null, null};
	
	public AlOneFont(final String Name, int style, File f) {
		aName = Name;
		style &= 0x03;
		if (f != null) 
			aFile[style] = f;
	}		
	
	public static void addFontInfo(AlOneFont fontInfo, int style, File f) {
		style &= 0x03;
		if (f != null) {
			fontInfo.aFile[style] = f;			
		}
	}

}
