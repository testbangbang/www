package com.neverland.engbook.util;

import com.neverland.engbook.forpublic.AlResourceFont;

import java.io.File;

public class AlOneFont {
	
	public final String aName;
	public final File[] aFile = {null, null, null, null};
	public AlResourceFont res = null;
	
	public AlOneFont(final String Name, int style, File f) {
		aName = Name;
		style &= 0x03;
		if (f != null) 
			aFile[style] = f;
	}

    public AlOneFont(AlResourceFont resourceFont) {
        res = resourceFont;
        aName = resourceFont.aName;
    }
	
	public static void addFontInfo(AlOneFont fontInfo, int style, File f) {
		style &= 0x03;
		if (f != null) {
			fontInfo.aFile[style] = f;			
		}
	}

}
