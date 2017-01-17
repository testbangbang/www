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



	public static void ReadLZH0(ZIP_LZH zipLZH, AlFiles a) {
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

	private static final int STRUCTSIZE = 30;
	private final byte[] buff = new byte[STRUCTSIZE];

	public void ReadLZH(AlFiles a) {

		a.getByteBuffer(a.read_pos, buff, STRUCTSIZE);
		a.read_pos += STRUCTSIZE;

		sig = (long)((((int)buff[0]) & 0xff) +
				((((int)buff[1]) & 0xff) << 8) +
				((((int)buff[2]) & 0xff) << 16) +
				((((int)buff[3]) & 0xff) << 24));
		version = (char)( (((int)buff[4]) & 0xff) + ((((int)buff[5]) & 0xff) << 8));
		flag = (char)( (((int)buff[6]) & 0xff) + ((((int)buff[7]) & 0xff) << 8));
		compressed = (char)( (((int)buff[8]) & 0xff) + ((((int)buff[9]) & 0xff) << 8));
		moddatetime = (long)((((int)buff[10]) & 0xff) +
				((((int)buff[11]) & 0xff) << 8) +
				((((int)buff[12]) & 0xff) << 16) +
				((((int)buff[13]) & 0xff) << 24));
		crc32 = (long)((((int)buff[14]) & 0xff) +
				((((int)buff[15]) & 0xff) << 8) +
				((((int)buff[16]) & 0xff) << 16) +
				((((int)buff[17]) & 0xff) << 24));
		csize = (long)((((int)buff[18]) & 0xff) +
				((((int)buff[19]) & 0xff) << 8) +
				((((int)buff[20]) & 0xff) << 16) +
				((((int)buff[21]) & 0xff) << 24));
		usize = (long)((((int)buff[22]) & 0xff) +
				((((int)buff[23]) & 0xff) << 8) +
				((((int)buff[24]) & 0xff) << 16) +
				((((int)buff[25]) & 0xff) << 24));
		namelength = (char)( (((int)buff[26]) & 0xff) + ((((int)buff[27]) & 0xff) << 8));
		extralength = (char)( (((int)buff[28]) & 0xff) + ((((int)buff[29]) & 0xff) << 8));
	}
}
