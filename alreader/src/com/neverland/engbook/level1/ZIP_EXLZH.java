package com.neverland.engbook.level1;

public class ZIP_EXLZH {
	
	long	version;	// version needed to extract
	long	us;	    // general purpose flag
	long	hz1;	// compression method
	long	cs;// last modification time
	long	hz2;
		
	public static final void ReadEXLZH(ZIP_EXLZH zipEXLZH, AlFiles a) { 		
		zipEXLZH.version 	= a.getDWord();
		zipEXLZH.us 		= a.getDWord();
		zipEXLZH.hz1  		= a.getDWord();
		zipEXLZH.cs  		= a.getDWord();
		zipEXLZH.hz2		= a.getDWord();
  	}
}
