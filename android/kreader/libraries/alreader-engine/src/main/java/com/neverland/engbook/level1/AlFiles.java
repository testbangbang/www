package com.neverland.engbook.level1;

import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.HashMap;

import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;


public abstract class AlFiles {

	public static final int LEVEL1_FILE_NOT_FOUND = -1;
	public static final int LEVEL1_FILE_BUF_SIZE =		65536;//32768;
	public static final int LEVEL1_FILE_BUF_MASK = 		((0xffffffff - LEVEL1_FILE_BUF_SIZE) + 1);
	public static final int LEVEL1_FILE_BUF_MASK_DATA =	(LEVEL1_FILE_BUF_SIZE - 1);
	public static final int LEVEL1_FILE_NAME_MAX_LENGTH	 = 256;
	public static final String LEVEL1_ZIP_FIRSTNAME_EPUB = "/META-INF/container.xml";
	public static final String LEVEL1_ZIP_FIRSTNAME_DOCX = "/word/document.xml";
	public static final String LEVEL1_ZIP_FIRSTNAME_ODT = "/content.xml";
	public static final int LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG = 0x80000000;
	public static final int LEVEL1_BOOKOPTIONS_NEED_MULTIFILE_FULL = 0x40000000;
	public static final int BOOKOPTIONS_FB2_SUBTITLE_2_TOC = 	0x00000001;
	public static final int BOOKOPTIONS_DISABLE_CSS	= 		0x00800000;
	public static final String LEVEL1_FB3_FILE_RELS = "/fb3/_rels/body.xml.rels";
	public static final String LEVEL1_FB3_FILE_FORCOVER = "/_rels/.rels";
	public static final String LEVEL1_ZIP_FIRSTNAME_FB3 = "/fb3/body.xml";
	public static final String LEVEL1_ZIP_DESCRIPTION_FB3 = "/fb3/description.xml";
	public static final String LEVEL1_FB3_FILE_CONTENTTYPES = "/[Content_Types].xml";


	int					read_pos;
	boolean				onlyScan;
	boolean				autoCodePage;
	public String		fileName;
	int					size;
	private int 		slot_active = 0;
	private final int[]		slot_start = {0, 0};
	private final int[]		slot_end = {0, 0};
	private final byte[][] 	slot = {new byte[LEVEL1_FILE_BUF_SIZE], new byte[LEVEL1_FILE_BUF_SIZE]};
	
	public static long			time_load1;
	public static long			time_load2;
	protected AlFiles			parent = null;
	String				ident;

	public AlFiles getParent() {
		return parent;
	}

	ArrayList<AlFileZipEntry>	fileList = new ArrayList<>(0);
	
	public	AlFiles() {
		parent = null;
		slot_active = 0;
		slot_start[0] = slot_start[1] = 0;
		slot_end[0] = slot_end[1] = 0;
		onlyScan = false;
	}

	public void setOnlyScan() {
		onlyScan = true;
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
	public void finalize() throws Throwable {
		fileList.clear();
		
		parent = null;

		fileName = null;
		slot[0] = null;
		slot[1] = null;

		super.finalize();
	}

	public ArrayList<AlFileZipEntry> getFileList() {
		return fileList;
	}

	public long setLoadTime1(boolean setStart) {
		if (setStart) {
			time_load1 = System.currentTimeMillis();
		} else {
			time_load1 = System.currentTimeMillis() - time_load1;
		}	
		return time_load1;
	}

	public long setLoadTime2(boolean setStart) {
		if (setStart) {
			time_load2 = System.currentTimeMillis();
		} else {
			time_load2 = System.currentTimeMillis() - time_load2;
		}
		return time_load2;
	}
	
	boolean				useUnpack = false;
	byte[]				unpack_buffer = null;
	public void	needUnpackData() {
		if (parent != null)
			parent.needUnpackData(); 		
	}

    public int  getByteBuffer(int pos, byte[] dst, int dst_pos, int len) {
        int point = pos;
        int res = 0x00;
        int ready, i;

        if (useUnpack && unpack_buffer != null) {
            ready = len;
            if (pos + ready >= size)
                ready = size - pos;

            System.arraycopy(unpack_buffer, pos, dst, dst_pos, ready);
            return ready;
        }

        while ((res < len) && (point < size)) {
            if (slot_start[slot_active] <= point && slot_end[slot_active] > point) {
                ready = slot_end[slot_active] - point;
                if (ready > len - res)
                    ready = len - res;

                System.arraycopy(slot[slot_active], point - slot_start[slot_active],
                        dst, dst_pos + res, ready);

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
                        dst, dst_pos + res, ready);

                res += ready;
                point += ready;
                continue;
            }

            //Log.e("slot read", Integer.toString(point & LEVEL1_FILE_BUF_MASK));

            slot_start[slot_active] = point & LEVEL1_FILE_BUF_MASK;
            i = getBuffer(slot_start[slot_active], slot[slot_active], LEVEL1_FILE_BUF_SIZE);
            if (i >= 0) {
                slot_end[slot_active] = slot_start[slot_active] + i;
                continue;
            }
        }

        return res;
    }

	public int  getByteBuffer(int pos, byte[] dst, int len) {
		int point = pos;
		int res = 0x00;
		int ready, i;
		
		if (useUnpack && unpack_buffer != null) {
			ready = len;
			if (pos + ready >= size)
				ready = size - pos;

			System.arraycopy(unpack_buffer, pos, dst, 0, ready);
			return ready;
		}
		
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
						
			//Log.e("slot read", Integer.toString(point & LEVEL1_FILE_BUF_MASK));
			
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
			if (s != null && s.length() > 0) {
				s = parent.getFullPublicName() + EngBookMyType.AL_FILENAMES_SEPARATOR + s;
			} else {
				s = parent.getFullPublicName();
			}
		}
		return s;
	}

	@Override
	public String toString() {
		String s = "" + ((int)(time_load1 + time_load2)) + '/' + time_load1 + '/' + time_load2;
		if (parent != null)
			s = parent.toString();
		return s + "\r\n" + (fileName != null ? fileName : ' ') + " " + size + " " + getIdentStr();
	}

	public String	getIdentStr() {
		return ident;
	}

	public int getCodePage() {
		return -1;
	}

	public String  getCRCForBook() {

		if (parent != null)
			return parent.getCRCForBook();

		return "1233333:1222";
	}

	public String getFullRealName() {
		String s = fileName;
		if (parent != null) {
			if (s != null && s.length() > 0) {
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

	private int createDebugFile(String pathForDebug, int oc) {
		int outCounter = 0;

		if (parent != null) {
			outCounter = parent.createDebugFile(pathForDebug, outCounter);
			if (outCounter < 0)
				return outCounter;
		}

		if (parent != null) {

			AlRandomAccessFile df = new AlRandomAccessFile();

			String tmp = pathForDebug + "s_taldeb.l" + outCounter;

			if (df.open(tmp, 1) == TAL_RESULT.OK) {

				byte[] buff = new byte[LEVEL1_FILE_BUF_SIZE];
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

			df = null;
		}

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
		if (test.endsWith("htm")) {
			return true;
		} else
		if (test.endsWith("html")) {
			return true;
		} else
		if (test.endsWith("xhtml")) {
			return true;
		} else
		if (test.endsWith("txt")) {
			return true;
		} else
		if (test.endsWith("doc")) {
			return true;
		} else
		if (test.endsWith("docx")) {
			return true;
		} else
		if (test.endsWith("epub")) {
			return true;
		} else
		if (test.endsWith("prc")) {
			return true;
		} else
		if (test.endsWith("pdb")) {
			return true;
		} else
		if (test.endsWith("azw")) {
			return true;
		} else
		if (test.endsWith("azw3")) {
			return true;
		} else
		if (test.endsWith("mobi")) {
			return true;
		} else
		if (test.endsWith("fb3")) {
			return true;
		} else
		if (test.endsWith("rtf")) {
			return true;
		} else
		if (test.endsWith("acbf")) {
			return true;
		} else
		if (test.endsWith("odt")) {
			return true;
		}
			
		return false;
	}

	public static String getAbsoluteName(String baseName, String fileName) {
        if (fileName == null)
            return null;

        if (fileName.length() > 0 && fileName.charAt(0) == EngBookMyType.AL_ROOT_RIGHTPATH)
            return fileName;

        if (fileName.indexOf(':') != -1)
            return fileName;

        StringBuilder fName = new StringBuilder(fileName.trim());

		if (fName.length() < 1)
			return fName.toString();

        for (int i = 0; i < fName.length(); i++) {
            if (fName.charAt(i) == EngBookMyType.AL_ROOT_WRONGPATH)
                fName.setCharAt(i, EngBookMyType.AL_ROOT_RIGHTPATH);
        }

		if (fName.charAt(0) == EngBookMyType.AL_ROOT_RIGHTPATH)
			return fName.toString();

		// need base
        StringBuilder bName = new StringBuilder(baseName.trim());

		if (bName.length() < 1)
			return fName.toString();

        for (int i = 0; i < bName.length(); i++) {
            if (bName.charAt(i) == EngBookMyType.AL_ROOT_WRONGPATH)
				bName.setCharAt(i, EngBookMyType.AL_ROOT_RIGHTPATH);
        }
		if (bName.charAt(0) != EngBookMyType.AL_ROOT_RIGHTPATH)
			return fName.toString();

		int	pos = bName.lastIndexOf(EngBookMyType.AL_ROOT_RIGHTPATH_STR);
		if (pos == -1)
			return fName.toString();

		StringBuilder res = new StringBuilder();
		res.append(bName.substring(0, pos));

		String[] tok = fName.toString().split(EngBookMyType.AL_ROOT_RIGHTPATH_STR);

		if (tok.length == 0) {
			res.append(EngBookMyType.AL_ROOT_RIGHTPATH);
			res.append(fileName);
		} else
			for (String aTok : tok) {
				if (aTok.length() == 0)
					continue;
				if (".".contentEquals(aTok))
					continue;
				if ("..".contentEquals(aTok)) {
					pos = res.lastIndexOf(EngBookMyType.AL_ROOT_RIGHTPATH_STR);
					if (pos == -1)
						return fName.toString();
					res.delete(pos, res.length());
				} else {
					res.append(EngBookMyType.AL_ROOT_RIGHTPATH);
					res.append(aTok);
				}
			}

		return res.toString();
	}

	protected final HashMap<String, Integer> mapFile = new HashMap<>();

	public int getExternalFileNum(String fname) {
		if (fname == null)
			return LEVEL1_FILE_NOT_FOUND;

		if (mapFile.size() == 0) {
			for (int i = 0; i < fileList.size(); i++) {
				mapFile.put(fileList.get(i).name, i);
			}
		}

        for (int j = 0; j < 2; j++) {
            fname = j == 0 ? getAbsoluteName(fileName, fname) : AlUnicode.URLDecode(fname);

            if (fname != null) {
				Integer i = mapFile.get(fname);
				if (i != null)
					return i;
                /*for (int i = 0; i < fileList.size(); i++) {
                    if (fileList.get(i).name.contentEquals(fname)) {
                        return i;
                    }
                }*/
			}
        }

		return LEVEL1_FILE_NOT_FOUND;
	}

	public String getExternalAbsoluteFileName(int num) {
		if (num >= 0 && num < fileList.size())
			return fileList.get(num).name;
		return null;
	}

	public int getExternalFileSize(int num) {
		if (num >= 0 && num < fileList.size())
			return fileList.get(num).uSize;
		return 0;
	}

	public boolean fillBufFromExternalFile(int num, int pos, byte[] dst, int dst_pos, int cnt) {
		return false;
	}
	
	abstract protected int getBuffer(final int pos, byte[] dst, int cnt);

}
