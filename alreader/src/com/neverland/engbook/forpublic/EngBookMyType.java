package com.neverland.engbook.forpublic;

import java.io.File;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class EngBookMyType {

	public static final int AL_WORD_LEN =	384;
	
	public static final char AL_ROOT_WRONGPATH	= '\\';
	public static final char AL_ROOT_RIGHTPATH = '/';
	public static final int  AL_MAX_FILENAME_LENGTH = 255;
	public static final char AL_FILENAMES_SEPARATOR = '\u0001';
	public static final int  AL_MAX_PARAGRAPH_LEN = 16384;

	public static final int  AL_DEFAULT_PAGESIZE = 2048;
	
	
	//////////////////////////////////////////////
	
	public enum TAL_GOTOCOMMAND {
		NEXTPAGE(1),
		PREVPAGE(2),
		LASTPAGE(3),
		FIRSTPAGE(4),
		POSITION(5);
		
		private int numVal;
		TAL_GOTOCOMMAND(int numVal) {
	        this.numVal = numVal;
	    }
	} ;	

	public enum TAL_PAGE_INDEX { 
		PREV,
		CURR,
		NEXT,		
	} ;

	public enum TAL_SCREEN_DPI { 
		TAL_SCREEN_DPI_120(120),
		TAL_SCREEN_DPI_160(160),
		TAL_SCREEN_DPI_240(240),
		TAL_SCREEN_DPI_320(320),
		TAL_SCREEN_DPI_480(480),
		TAL_SCREEN_DPI_640(640);
		
		private int numVal;
		TAL_SCREEN_DPI(int numVal) {
	        this.numVal = numVal;
	    }
	} ;

	public enum TAL_SCREEN_PAGES_COUNT { 
		SIZE,
		SCREEN,
		AUTO,
	} ;

	public enum TAL_THREAD_TASK {
		OPENBOOK(10),
		//CLOSEBOOK(11),
		CREATEDEBUG(12),
		FIND(13),
		NEWCALCPAGES(14);
		
		private int numVal;
		TAL_THREAD_TASK(int numVal) {
	        this.numVal = numVal;
	    }
	} ;

	public enum TAL_NOTIFY_ID {
		NEEDREDRAW(1),
		STARTTHREAD(2),
		STOPTHREAD(3),
		OPENBOOK(10),
		CLOSEBOOK(11),
		CREATEDEBUG(12),
		FIND(13),
		NEWCALCPAGES(14);
		
		private int numVal;
		TAL_NOTIFY_ID(int numVal) {
	        this.numVal = numVal;
	    }
	} ;

	public enum TAL_NOTIFY_RESULT {
		OK,
		ERROR,		
	} ;

	public enum TAL_HYPH_LANG {
		NONE,
		ENGLISH,
		RUSSIAN,
		ENGRUS,		
	} ;

	public enum TAL_FILE_TYPE {
		TXT,
		ZIP,
		DOC,		
	} ;
	
}
