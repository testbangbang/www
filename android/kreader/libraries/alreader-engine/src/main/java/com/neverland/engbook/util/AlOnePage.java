package com.neverland.engbook.util;

import java.util.ArrayList;

public class AlOnePage {

	public final ArrayList<AlOneItem>		items = new ArrayList<>(0);
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

	public final AlOneBlock	block = new AlOneBlock();
	public int 			textHeightWONotes;

    public InternalConst.TAL_PAGE_MODE		mode;

	public static void init(AlOnePage a, InternalConst.TAL_PAGE_MODE m) {
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
		a.mode = m;
	}

	public static void	addItem(AlOnePage a) {
		a.items.add(new AlOneItem());		
		a.realLength = a.items.size();
	}

	public static void free(AlOnePage a) {			
		a.items.clear();
	}

	public void dublicate(AlOnePage a) {
		while (realLength < a.realLength)
			addItem(this);

		for (int i = 0; i < a.realLength; i++)
			items.get(i).dublicate(a.items.get(i));

		//AlOneBlock		block;
		block.height = a.block.height;
		block.left = a.block.left;
		block.use = a.block.use;
		//

		start_position = a.start_position;
		end_position = a.end_position;
		countItems = a.countItems;
		textHeight = a.textHeight;
		pageHeight = a.pageHeight;
		topMarg = a.topMarg;
		overhead = a.overhead;
		selectStart = a.selectStart;
		selectEnd = a.selectEnd;
		notePresent = a.notePresent;

		notesShift = a.notesShift;

		textHeightWONotes = a.textHeightWONotes;
	}
}
