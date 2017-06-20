package com.neverland.engbook.level1;

@SuppressWarnings("WeakerAccess")
public class ZIP_EXLZH {
	
	long	version;	// version needed to extract
	long	us;	    // general purpose flag
	long	hz1;	// compression method
	long	cs;// last modification time
	long	hz2;
		
	public static void ReadEXLZH0(ZIP_EXLZH zipEXLZH, AlFiles a) {
		zipEXLZH.version 	= a.getDWord();
		zipEXLZH.us 		= a.getDWord();
		zipEXLZH.hz1  		= a.getDWord();
		zipEXLZH.cs  		= a.getDWord();
		zipEXLZH.hz2		= a.getDWord();
  	}

	private static final int STRUCTSIZE = 20;
	private final byte[] buff = new byte[STRUCTSIZE];

	public void ReadEXLZH(AlFiles a) {

		a.getByteBuffer(a.read_pos, buff, STRUCTSIZE);
		a.read_pos += STRUCTSIZE;

		version = (long)((((int)buff[0]) & 0xff) +
				((((int)buff[1]) & 0xff) << 8) +
				((((int)buff[2]) & 0xff) << 16) +
				((((int)buff[3]) & 0xff) << 24));
		us = (long)((((int)buff[4]) & 0xff) +
				((((int)buff[5]) & 0xff) << 8) +
				((((int)buff[6]) & 0xff) << 16) +
				((((int)buff[7]) & 0xff) << 24));
		hz1 = (long)((((int)buff[8]) & 0xff) +
				((((int)buff[9]) & 0xff) << 8) +
				((((int)buff[10]) & 0xff) << 16) +
				((((int)buff[11]) & 0xff) << 24));
		cs = (long)((((int)buff[12]) & 0xff) +
				((((int)buff[13]) & 0xff) << 8) +
				((((int)buff[14]) & 0xff) << 16) +
				((((int)buff[15]) & 0xff) << 24));
		hz2 = (long)((((int)buff[16]) & 0xff) +
				((((int)buff[17]) & 0xff) << 8) +
				((((int)buff[18]) & 0xff) << 16) +
				((((int)buff[19]) & 0xff) << 24));
	}
}
