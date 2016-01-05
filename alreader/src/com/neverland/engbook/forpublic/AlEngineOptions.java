package com.neverland.engbook.forpublic;

import android.app.Application;


import com.neverland.engbook.forpublic.EngBookMyType.TAL_HYPH_LANG;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_DPI;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_PAGES_COUNT;

public class AlEngineOptions {
	public int									magic = 0x1812; 
	public TAL_SCREEN_PAGES_COUNT				useScreenPages = TAL_SCREEN_PAGES_COUNT.SIZE;
	public TAL_SCREEN_DPI						DPI = TAL_SCREEN_DPI.TAL_SCREEN_DPI_160; // for Win32 - only 160	
	public String								font_catalog; // not to use for win32		
	public TAL_HYPH_LANG						hyph_lang = TAL_HYPH_LANG.NONE;
	public Application							appInstance;
	public boolean								chinezeFormatting = true;
}
