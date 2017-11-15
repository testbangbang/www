package com.neverland.engbook.util;

import com.neverland.engbook.forpublic.EngBookMyType.TAL_TABLEMODE;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_PAGES_COUNT;



public class AlPreferenceOptions {
	public boolean		notesAsSUP;
	public boolean		delete0xA0;
	public int			need_dialog;
	public boolean		sectionNewScreen;
	//public boolean		styleSumm;
	public int			u301mode;	
	public boolean		chinezeFormatting;	
	public boolean		picture_need_tune;
	//public int			picture_need_tuneK;
	public boolean		justify;
	public boolean		notesOnPage;
	public boolean 		isASRoll;
	public boolean		useSoftHyphen;

	public boolean		useAutoPageSize;
	public boolean		needCalcAutoPageSize;
	public int			pageSize;
	
	public int			maxNotesItemsOnPageRequest;
	public int			maxNotesItemsOnPageUsed;
	public boolean		vjustifyRequest;
	public boolean		vjustifyUsed;
	public TAL_SCREEN_PAGES_COUNT			calcPagesModeRequest;
	public TAL_SCREEN_PAGES_COUNT			calcPagesModeUsed;
	public float		multiplexer = 1.0f;
	public int			value2CalcMargins = 0;
	public boolean 		onlyPopupFootnote = false;
	public TAL_TABLEMODE tableMode;
}
