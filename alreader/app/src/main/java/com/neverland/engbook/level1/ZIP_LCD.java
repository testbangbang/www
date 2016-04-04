package com.neverland.engbook.level1;

@SuppressWarnings("WeakerAccess")
public class ZIP_LCD {
	 long	sig;		// central file header signature   4 bytes  (0x02014b50)
     char   ver1;		// version made by                 2 bytes
     char	ver2;		// version needed to extract       2 bytes
     char   flag;		// general purpose bit flag        2 bytes
     char   compressed;	// compression method              2 bytes
     char	T1;			// last mod file time              2 bytes
     char	T2;			// last mod file date              2 bytes
     long   crc32;		// crc-32                          4 bytes
     long   csize;		// compressed size                 4 bytes
     long	usize;		// uncompressed size               4 bytes
     char	namelength; // filename length                 2 bytes
     char	extralength;// extra field length              2 bytes
     char	commlength; // file comment length             2 bytes
     char   dnumstart;	// disk number start               2 bytes
     char   ifattr;		// internal file attributes        2 bytes
     long	efattr;		// external file attributes        4 bytes
     long	offset;		// relative offset of local header 4 bytes  
     
     /////////////////
     long	version;	// version needed to extract
     long	us;	    	// general purpose flag
     long	hz1;		// compression method
     long	cs;			// last modification time
     long	hz2;
 	 ////////////////
     
     char[] tmp;
     int	len_tmp;
     
     String	fName;
     
     public static void ReadBuffer(ZIP_LCD zipLCD, AlFiles a, int len) {
    	 if (zipLCD.tmp == null || zipLCD.tmp.length < len) {
    		 zipLCD.len_tmp = len;
    		 zipLCD.tmp = new char[zipLCD.len_tmp];
    	 }
    	 int k, j = a.read_pos;
    	 char ch;
    	 for (k = 0; a.read_pos < j + len; k++) {
    		 ch = (char)a.getUByte();
    		 if (ch == ':') {
    			 zipLCD.tmp[k] = '_';
    		 } else
    			 zipLCD.tmp[k] = ch;
    	 }
     }
     
     public static void ReadExtra(ZIP_LCD zipLCD, AlFiles a) {
    	 zipLCD.version 	= a.getDWord();	
    	 zipLCD.us 			= a.getDWord();	
    	 zipLCD.hz1 		= a.getDWord();	
    	 zipLCD.cs 			= a.getDWord();	
    	 zipLCD.hz2 		= a.getDWord(); 
     }
     
     public static void ReadLCD(ZIP_LCD zipLCD, AlFiles a) {
    	zipLCD.sig 			= a.getDWord();
    	zipLCD.ver1 		= a.getWord();
    	zipLCD.ver2  		= a.getWord();
 		zipLCD.flag  		= a.getWord();
 		zipLCD.compressed  	= a.getWord();
 		zipLCD.T1  			= a.getRevWord();
 		zipLCD.T2  			= a.getWord();
 		zipLCD.crc32  		= a.getDWord();
 		zipLCD.csize  		= a.getDWord();
 		zipLCD.usize  		= a.getDWord();
 		zipLCD.namelength  	= a.getWord();
 		zipLCD.extralength  = a.getWord();
 		zipLCD.commlength  	= a.getWord();
 		zipLCD.dnumstart  	= a.getWord();
 		zipLCD.ifattr  		= a.getWord();
 		zipLCD.efattr  		= a.getDWord();
 		zipLCD.offset  		= a.getDWord(); 		
  	}
}
