package com.neverland.engbook.level1;

import java.util.ArrayList;

import android.util.Log;

import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;


public class AlFilesBypass extends AlFiles {
	
	AlRandomAccessFile	raf = null;

	@Override
	public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
		super.initState(file, myParent, fList);

		ident = "bypass";

		raf = new AlRandomAccessFile();

		if (raf.open(file, 0) == TAL_RESULT.OK) {
			size = raf.getSize();
			return TAL_RESULT.OK;
		}
		
		size = 0;
		return TAL_RESULT.ERROR;
	}

	@Override
	public void finalize() throws Throwable {
		raf.close();
		super.finalize();
	}

	@Override
	public int getBuffer(int pos, byte[] dst, int cnt) {
		//Log.e("Bypass read", Integer.toString(pos));
		if (raf.seek(pos) == pos) {
			int r = raf.read(dst, 0, cnt);
			if (r >= 0)
				return r;
		}
		for (int i = 0; i < cnt; i++)
			dst[i] = 0;
		return cnt;
	}

	@Override
	public int getExternalFileNum(String fname) {
		if (fname == null)
			return LEVEL1_FILE_NOT_FOUND;

		for (int j = 0; j < 2; j++) {
			fname = j == 0 ? getAbsoluteName(fileName, fname) : AlUnicode.URLDecode(fname);

            if (fname != null) {
                for (int i = 0; i < fileList.size(); i++) {
                    if (fileList.get(i).name.contentEquals(fname)) {
                        return i;
                    }
                }

                int sz = AlRandomAccessFile.isFileExists(fname);
                if (sz > 0) {
                    AlFileZipEntry of = new AlFileZipEntry();
                    of.compress = 0;
                    of.cSize = sz;
                    of.uSize = sz;
                    of.flag = 0;
                    of.position = 0;
                    of.time = 0;
                    of.name = fname;
                    fileList.add(of);
                    return fileList.size() - 1;
                }
            }
		}

		return LEVEL1_FILE_NOT_FOUND;
	}

	@Override
	public boolean fillBufFromExternalFile(int num, int pos, byte[] dst, int dst_pos, int cnt) {
		int res = 0;
		if (num >= 0 && num < fileList.size()) {

			if (fileList.get(num).name.contentEquals(fileName)) {
				res = getByteBuffer(pos, dst, dst_pos, cnt);
			} else {
				AlRandomAccessFile raf = new AlRandomAccessFile();
				if (raf.open(fileList.get(num).name, 0) == TAL_RESULT.OK) {
					raf.seek(pos);
					res = raf.read(dst, dst_pos, cnt);
					raf.close();
				}
				raf = null;
			}

		}
		return res == cnt;
	}
}
