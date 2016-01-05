package com.neverland.engbook.util;



public class AlOneContent {

	
		public String		name;
		public int			positionS;
		public int			iType;
		public boolean		isBookmark;

		public static AlOneContent add(String s, int content_start, int level) {
			AlOneContent a = new AlOneContent();
			a.name = s;
			a.positionS = content_start;
			a.iType = level;
			return a;
		}

		@Override
		public String toString() {
			return String.valueOf(iType) + '/' + name + '/' + positionS;
		}
		
	
	
}
