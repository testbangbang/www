package com.neverland.engbook.level1;

@SuppressWarnings("WeakerAccess")
public class ZIP_LZH {
	
	long	sig;	    // signature
	char	version;	// version needed to extract
	char	flag;	    // general purpose flag
	char	compressed;	// compression method
	long	moddatetime;// last modification time
	long	crc32;
	long	csize;	    // compressed size
	long	usize;	    // uncompressed size
	char	namelength;	// filename length
	char	extralength;// extra field length  
	
	public static void ReadLZH(ZIP_LZH zipLZH, AlFiles a) {
		zipLZH.sig 			= a.getDWord();
		zipLZH.version 		= a.getWord();
    	zipLZH.flag  		= a.getWord();
    	zipLZH.compressed  	= a.getWord();
    	zipLZH.moddatetime	= a.getDWord();
    	zipLZH.crc32  		= a.getDWord();
    	zipLZH.csize  		= a.getDWord();
    	zipLZH.usize  		= a.getDWord();
    	zipLZH.namelength  	= a.getWord();
    	zipLZH.extralength  = a.getWord();
  	}
}
