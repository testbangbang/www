package com.neverland.engbook.level1;

import java.util.ArrayList;

import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;
import com.neverland.engbook.forpublic.TAL_RESULT;


public abstract class AlFiles {
	
	public static final int LEVEL1_FILE_BUF_SIZE =		32768;
	public static final int LEVEL1_FILE_BUF_MASK = 		((0xffffffff - LEVEL1_FILE_BUF_SIZE) + 1);
	public static final int LEVEL1_FILE_BUF_MASK_DATA =	(LEVEL1_FILE_BUF_SIZE - 1);
	public static final int LEVEL1_FILE_NAME_MAX_LENGTH	 = 256;
	
	int					read_pos;
	
	boolean				autoCodePage;
	String				fileName;
	int					size;
	protected int 		slot_active = 0; 	
	protected int[]		slot_start = {0, 0};
	protected int[]		slot_end = {0, 0};
	protected byte[] 	slot[] = {new byte[LEVEL1_FILE_BUF_SIZE], new byte[LEVEL1_FILE_BUF_SIZE]};
	
	static long			time_load;
	AlFiles				parent;
	String				ident;	
	
	ArrayList<AlFileZipEntry>	fileList = new ArrayList<AlFileZipEntry>(0);
	
	public	AlFiles() {
		parent = null;
		slot_active = 0;
		slot_start[0] = slot_start[1] = 0;
		slot_end[0] = slot_end[1] = 0;		
	}

	public int	initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
		parent = myParent;
		fileName = file;
		fileList.clear();
		if (fList != null)
			fileList = (ArrayList<AlFileZipEntry>) fList.clone();
		
		return TAL_RESULT.OK;
	}

	@Override
	public void finalize() {
		fileList.clear();
		
		parent = null;

		fileName = null;
		slot[0] = null;
		slot[1] = null;	
	}

	public long setLoadTime(boolean setStart) {
		if (setStart) {
			time_load = System.currentTimeMillis();
		} else {
			time_load = System.currentTimeMillis() - time_load;
		}	
		return time_load;
	}

	public int  getByteBuffer(int pos, byte[] dst, int len) {
		int point = pos;
		int res = 0x00;
		int ready, i;
		
		while ((res < len) && (point < size)) {
			if (slot_start[slot_active] <= point && slot_end[slot_active] > point) {
				ready = slot_end[slot_active] - point;
				if (ready > len - res)
					ready = len - res;
				
				System.arraycopy(slot[slot_active], point - slot_start[slot_active], 
						dst, res, ready);			
				
				res += ready;
				point += ready;
				continue;
			}
			
			slot_active = 1 - slot_active;
						
			if (slot_start[slot_active] <= point && slot_end[slot_active] > point) {
				ready = slot_end[slot_active] - point - 1;
				if (ready > len - res)
					ready = len - res;
				
				System.arraycopy(slot[slot_active], point - slot_start[slot_active], 
						dst, res, ready);			
				
				res += ready;
				point += ready;
				continue;
			}
						
			slot_start[slot_active] = point & LEVEL1_FILE_BUF_MASK;
			i = getBuffer(slot_start[slot_active], slot[slot_active], LEVEL1_FILE_BUF_SIZE);
			if (i >= 0) {
				slot_end[slot_active] = slot_start[slot_active] + i;
				continue;
			}			
		}
		
		return res;
	}


	byte getByte(int pos) {
		if (slot_start[slot_active] <= pos && slot_end[slot_active] > pos) {
			return slot[slot_active][pos & LEVEL1_FILE_BUF_MASK_DATA];
		}
				
		slot_active = 1 - slot_active;		
		
		if (slot_start[slot_active] <= pos && slot_end[slot_active] > pos) {
			return slot[slot_active][pos & LEVEL1_FILE_BUF_MASK_DATA];
		}
		
		slot_start[slot_active] = pos & LEVEL1_FILE_BUF_MASK;
		int i = getBuffer(slot_start[slot_active], slot[slot_active], LEVEL1_FILE_BUF_SIZE);
		if (i >= 0) {
			slot_end[slot_active] = slot_start[slot_active] + i;
			if (slot_start[slot_active] <= pos && slot_end[slot_active] > pos)
				return slot[slot_active][pos & LEVEL1_FILE_BUF_MASK_DATA];
		}
						
		return 0x00;
	}

	public String getFullPublicName() {
		String s = fileName;
		if (parent != null) {
			if (s.length() > 0) {
				s = parent.getFullPublicName() + EngBookMyType.AL_FILENAMES_SEPARATOR + s;
			} else {
				s = parent.getFullPublicName();
			}
		}
		return s;
	}

	@Override
	public String toString() {
		String s = "" + time_load;
		if (parent != null)
			s = parent.toString();
		return s + "\r\n" + fileName + " " + size + " " + getIdentStr();
	}

	public String	getIdentStr() {
		return ident;
	}

	public String getFullRealName() {
		String s = fileName;
		if (parent != null) {
			if (s.length() > 0) {
				s = parent.getFullRealName() + EngBookMyType.AL_FILENAMES_SEPARATOR + s;
			} else {
				s = parent.getFullRealName();
			}
		}
		return s;
	}

	public TAL_NOTIFY_RESULT createDebugFile(String pathForDebug) {
		int res = createDebugFile(pathForDebug, 0);
		return res < 0 ? TAL_NOTIFY_RESULT.ERROR : TAL_NOTIFY_RESULT.OK;
	}

	public int createDebugFile(String pathForDebug, int oc) {
		int outCounter = 0;

		if (parent != null) {
			outCounter = parent.createDebugFile(pathForDebug, outCounter);
			if (outCounter < 0)
				return outCounter;
		}
		
		AlRandomAccessFile df = new AlRandomAccessFile();
		
		String tmp = pathForDebug + "s_taldeb.l" + outCounter;

		if (df.open(tmp, 1) == TAL_RESULT.OK) {
			
			byte[] buff = new byte [LEVEL1_FILE_BUF_SIZE];
			int cnt_buff = 0;
						
			for (int i = 0; i < size; i++) {
				buff[cnt_buff++] = getByte(i);			
				if (cnt_buff == LEVEL1_FILE_BUF_SIZE || i == size - 1) {
					df.write(buff, 0, cnt_buff);
					cnt_buff = 0;
				}
			}
			
			df.close();
			buff = null;
			
		} else {
			outCounter = -100;
		}
		
		if (df != null)
			df = null;

		return ++outCounter;
	}
			

	int getUByte(int pos) {
		return ((int)getByte(pos)) & 0xff;
	}
		
	int getUByte() {
		return ((int)getByte(read_pos++)) & 0xff;
	}
		
	char getWord() {
		return (char)(getUByte() + (getUByte() << 8));
	}
		
	char getRevWord() {
		return (char)((getUByte() << 8) + getUByte());
	}
		
	long getDWord() {
		return (long)(getUByte() + 
					 (getUByte() << 8) +
					 (getUByte() << 16) +
					 (getUByte() << 24));
	}
		
	long getRevDWord() {
		return (long)((getUByte() << 24) + 
				 	  (getUByte() << 16) +
					  (getUByte() << 8) +
					   getUByte());
	}

	public int getSize() {
		return size;
	}	

	public static boolean isValidExt(String fname) {
		String test = fname.toLowerCase();
		
		if (test.endsWith("fb2")) {
			return true;
		} else
		if (test.endsWith("txt")) {
			return true;
		} else
		if (test.endsWith("doc")) {
			return true;
		}
			
		return false;
	}
	
	abstract int getBuffer(final int pos, byte[] dst, int cnt);

}
