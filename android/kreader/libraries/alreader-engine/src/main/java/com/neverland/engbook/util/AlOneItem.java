package com.neverland.engbook.util;



public class AlOneItem {

		public int				start_pos = 0;
		public boolean				needHeihtImage0 = true;
		public int 				cntImage = 0;
		public boolean				isEnd = false;
		public boolean				isStart = false;
		public int				isRed = 0;
		public int					isLeft = 0;
		public int					isRight = 0;
		public int 				height = 0;
		public int				base_line_up = 0;
		public int				base_line_down = 0;
		public long				justify = 0;

		public long 			prop = 0;
		public int				num = 0;

		public int				count = 0;
		public int				realLength = 256;
		public int					textWidth = 0;
		public int					allWidth = 0;
		public char[]				text = new char[realLength];
		public long[]				style = new long[realLength];
		public int[]				pos = new int[realLength];
		public int[]					width = new int[realLength];
		public boolean				isNote = false;
		public boolean				isPrepare = false;
		public int					spaceAfterHyph0 = 0;	
		public int				interline = 0;
		public int					yDrawPosition = -1;
		public boolean			isArabic = false;
		public boolean				isTableRow = false;
		public int				table_start = -1;
		public int				table_row = 0;



	public int					blockHeight = 0;
		//public int					blockLeft = 0;
		public int					base_line_up4text;
				
		public static void free(AlOneItem a) {		
			a.text = null;
			a.style = null;
			a.pos = null;
			a.width = null;
		}

		public static AlOneItem add_empty() {
			return new AlOneItem();
		}

		public static void incItemLength(AlOneItem a) {
			
			int new_length = a.realLength << 1;
			
			char[] tmp_text = new char[new_length];
			System.arraycopy(a.text, 0, tmp_text, 0, a.realLength);
			a.text = tmp_text;
			
			long[] tmp_style = new long[new_length];
			System.arraycopy(a.style, 0, tmp_style, 0, a.realLength);
			a.style = tmp_style;		
			
			int[] tmp_pos = new int[new_length];
			System.arraycopy(a.pos, 0, tmp_pos, 0, a.realLength);
			a.pos = tmp_pos;
			
			int[] tmp_width = new int[new_length];
			System.arraycopy(a.width, 0, tmp_width, 0, a.realLength);
			a.width = tmp_width;
			
			a.realLength = new_length;
		}

	void dublicate(AlOneItem a) {
		while (realLength < a.realLength)
			incItemLength(this);

		System.arraycopy(a.text, 0, text, 0, a.realLength);
		System.arraycopy(a.style, 0, style, 0, a.realLength);
		System.arraycopy(a.pos, 0, pos, 0, a.realLength);
		System.arraycopy(a.width, 0, width, 0, a.realLength);
		/////////////

		start_pos = a.start_pos;
		needHeihtImage0 = a.needHeihtImage0;
		cntImage = a.cntImage;
		isEnd = a.isEnd;
		isStart = a.isStart;
		isRed = a.isRed;
		isLeft = a.isLeft;
		isRight = a.isRight;
		height = a.height;
		base_line_up = a.base_line_up;
		base_line_down = a.base_line_down;
		justify = a.justify;
		count = a.count;

		num = a.num;
		prop = a.prop;

		textWidth = a.textWidth;
		allWidth = a.allWidth;

		isNote = a.isNote;
		isPrepare = a.isPrepare;
		spaceAfterHyph0 = a.spaceAfterHyph0;
		interline = a.interline;

		yDrawPosition = a.yDrawPosition;
		isArabic = a.isArabic;
		isTableRow = a.isTableRow;

		blockHeight = a.blockHeight;
		//blockLeft = a.blockLeft;
		base_line_up4text = a.base_line_up4text;

		table_start = a.table_start;
		table_row = a.table_row;
	}
}
