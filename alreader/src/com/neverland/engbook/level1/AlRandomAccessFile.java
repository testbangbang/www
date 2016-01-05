package com.neverland.engbook.level1;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.neverland.engbook.forpublic.TAL_RESULT;



public class AlRandomAccessFile {

	private	RandomAccessFile       fh;
 

	int		size;
	boolean		modeWrite;
		
	public AlRandomAccessFile() {
		fh = null;
		size = 0;
	}

	@Override
	public void finalize(){
		close();
	}

	public int open(String fileName, int needWrite){
		modeWrite = needWrite != 0;

		File f = new File(fileName);
		if (modeWrite || (f.exists() && f.canRead() && f.isFile())) {
			size = (int)f.length();	
			//last_modifed = f.lastModified();
			try {
				fh = new RandomAccessFile(fileName, modeWrite ? "rw" : "r");
			} catch (Exception e) {
				fh = null;
				e.printStackTrace();
			}
		} else {
			return TAL_RESULT.ERROR;		
		}

		if (fh == null)
			return TAL_RESULT.ERROR;
	
		try {
			if (modeWrite) {
				size = 0;
			} else {
				size = (int) fh.length();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	
		return TAL_RESULT.OK;
	}

	public void close() {
		if (fh == null)
			return;		
		
		try {
			fh.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fh = null;
	}

	public int seek(int pos) {
		if (fh == null)
			return -1;
		
		try {
			fh.seek(pos);
		} catch (IOException e) {
			pos = -1;
			e.printStackTrace();
		}
		
	 	return pos;
	}
		
	public int read(byte[] dst, int start, int cnt) {
		if (fh == null)
			return -1;

		try {
			cnt = fh.read(dst, start, cnt);
		} catch (IOException e) {
			cnt = -1;
			e.printStackTrace();
		}
		
		return cnt;
	}

	public int	write(byte[] src) {
		return write(src, 0, src.length);
	}
	
	public int	write(byte[] src, int start, int cnt) {
		if (fh == null || !modeWrite)
			return -1;

		try {
			fh.write(src, start, cnt);
		} catch (IOException e) {
			cnt = -1;
			e.printStackTrace();
		}
	
	 	return cnt;
	}

	public int get_size() {
		if (fh == null)
			return -1;
		
		if (modeWrite) {
			try {
				size = (int) fh.length();
			} catch (IOException e) {
				size = -1;
				e.printStackTrace();
			}
		}
		
		return size;
	}
	
}
