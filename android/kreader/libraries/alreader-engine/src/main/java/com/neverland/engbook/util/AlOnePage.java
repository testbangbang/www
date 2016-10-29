package com.neverland.engbook.util;

import java.util.ArrayList;

public class AlOnePage {

	public final ArrayList<AlOneItem>		items = new ArrayList<AlOneItem>(0);
	public int			start_position;
	public int			end_position;
	public int			countItems;
	public int			textHeight;
	public int			pageHeight;
	public int			topMarg;
	public int			overhead;
	public int			selectStart;
	public int			selectEnd;
	public boolean		notePresent;
	public int			notesShift;
	public int			realLength;

	public static void init(AlOnePage a) {
		a.realLength = 1;
		a.items.add(new AlOneItem());
		a.start_position = -1;
		a.end_position = -1;
		a.countItems = 0;
		a.textHeight = 0;
		a.pageHeight = 0;
		a.topMarg	= 0;
		a.overhead	= 0;
		a.selectStart = -1;
		a.selectEnd = -1;
		a.notePresent = false;
		a.notesShift = 0;
	}

	public static void	addItem(AlOnePage a) {
		a.items.add(new AlOneItem());		
		a.realLength = a.items.size();
	}

	public static void free(AlOnePage a) {			
		a.items.clear();
	}
	
}
