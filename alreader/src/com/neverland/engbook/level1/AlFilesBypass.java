package com.neverland.engbook.level1;

import java.util.ArrayList;

import com.neverland.engbook.forpublic.TAL_RESULT;


public class AlFilesBypass extends AlFiles {
	
	AlRandomAccessFile	raf = null;
	
	public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
		super.initState(file, myParent, fList);

		ident = "bypass";

		raf = new AlRandomAccessFile();

		if (raf.open(file, 0) == TAL_RESULT.OK) {
			size = raf.get_size();
			return TAL_RESULT.OK;
		}
		
		size = 0;
		return TAL_RESULT.ERROR;
	}

	@Override
	public void finalize() {	
		raf.close();
		super.finalize();
	}

	public int getBuffer(int pos, byte[] dst, int cnt) {
		if (raf.seek(pos) == pos) {
			int r = raf.read(dst, 0, cnt);
			if (r >= 0)
				return r;
		}
		for (int i = 0; i < cnt; i++)
			dst[i] = 0;
		return cnt;
	}
}
