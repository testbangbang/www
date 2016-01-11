package com.neverland.engbook.util;

import com.neverland.engbook.forpublic.AlEngineOptions;

public class AlImage {

	private int multiplexer = 1;
	
	public void init(AlEngineOptions opt ) {		
		
		switch (opt.DPI) {
		case TAL_SCREEN_DPI_320:
			multiplexer = 2;
			break;
		case TAL_SCREEN_DPI_480:
			multiplexer = 3;
			break;
		case TAL_SCREEN_DPI_640:
			multiplexer = 4;
			break;
		default:
			break;
		}	
	
	}
	
}
