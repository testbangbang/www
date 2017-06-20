package com.neverland.engbook.level1;

import java.util.ArrayList;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_FILE_TYPE;
import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.ZipUtil;

public class AlFilesZIP extends AlFiles {
	
	private static final int ZIP_CHUNK_SIZE = 16384;
	
	/*ZIP_LCD zipLCD = new ZIP_LCD();
	ZIP_LZH zipLZH = new ZIP_LZH();
	ZIP_EXLZH zipExLZH = new ZIP_EXLZH();*/

	private int		zip_position;
	private int		zip_compression;
	private int		zip_csize;
	private int		zip_index;

	private Inflater 	inflater = null;

	private int		zip_total_out;
	private int		zip_in_buff_size;
	private int		zip_out_buff_size;
	private final byte[]	zip_in_buff = new byte [ZIP_CHUNK_SIZE];
	private final byte[] 	zip_out_buff = new byte [ZIP_CHUNK_SIZE];

	static public TAL_FILE_TYPE isZIPFile(String fName, AlFiles a, ArrayList<AlFileZipEntry> fList, String ext) {
		TAL_FILE_TYPE res = TAL_FILE_TYPE.TXT;
		Map<String,Integer> fileSizeMap = null;
		if ((ext == null || ext.equalsIgnoreCase(".JEB"))) {
			fileSizeMap = ZipUtil.unzipFile(a.fileName, null, JEBFilesZIP.key,
					JEBFilesZIP.deviceUUID, JEBFilesZIP.random);
		}

		int			fsize = a.getSize();
		int			arr_size = 0x1000f;
		int			ecd = 0, scd = -1, tmp;
		int			cnt_files = 0;
		
		byte[]		fname = new byte [AlFiles.LEVEL1_FILE_NAME_MAX_LENGTH];


		for (int dw = 0; dw < 16; dw++) {
			if (a.getByte(dw)     == 0x50 &&
				a.getByte(dw + 1) == 0x4b) {
				ecd = 1;
				break;
			}
		}
		if (ecd == 0)
			return res;

		ZIP_LCD zipLCD = new ZIP_LCD();
		ZIP_LZH zipLZH = new ZIP_LZH();
		ZIP_EXLZH zipExLZH = new ZIP_EXLZH();

		if (fsize < 16)
			return res;
		if (fsize < arr_size) 
			arr_size = fsize;

		ecd = fsize - arr_size;

		for (int dw = fsize - 16; dw > ecd; dw--) {
			if (a.getByte(dw)     == 0x50 &&
				a.getByte(dw + 1) == 0x4b &&
				a.getByte(dw + 2) == 0x05 &&
				a.getByte(dw + 3) == 0x06) {

				scd =  a.getUByte(dw + 16);
				scd |= a.getUByte(dw + 17) << 8;
				scd |= a.getUByte(dw + 18) << 16;
				scd |= a.getUByte(dw + 19) << 24;

				ecd = dw;
				break;
			}
		}

		if (scd == -1)
			return res;
		
		res = TAL_FILE_TYPE.ZIP;

		a.read_pos = scd;
		while (a.read_pos < ecd) {

			//ZIP_LCD.ReadLCD0(zipLCD, a);
			zipLCD.ReadLCD(a);
			if (zipLCD.sig != 0x02014b50 || zipLCD.namelength == 0)				
				return cnt_files > 0 ? res : TAL_FILE_TYPE.TXT;		
		
			if ((zipLCD.compressed == 0 || zipLCD.compressed == 8) && 
				 zipLCD.csize != 0 && 
				 zipLCD.usize != 0) {

			    tmp = zipLCD.namelength > EngBookMyType.AL_MAX_FILENAME_LENGTH - 1 ? 
			    		EngBookMyType.AL_MAX_FILENAME_LENGTH - 1 : zipLCD.namelength;
			    
				a.getBuffer(a.read_pos, fname, tmp);
				if (fname[0] != EngBookMyType.AL_ROOT_WRONGPATH && fname[0] != EngBookMyType.AL_ROOT_RIGHTPATH) {
					System.arraycopy(fname, 0, fname, 1, tmp);
					fname[0] = EngBookMyType.AL_ROOT_RIGHTPATH;
					tmp++;
				}
				fname[tmp] = 0x00;
				a.read_pos += zipLCD.namelength;

				for (int i = 0; i < tmp; i++)
					if (fname[i] == EngBookMyType.AL_ROOT_WRONGPATH)
						fname[i] = EngBookMyType.AL_ROOT_RIGHTPATH;			
				


				if (zipLCD.csize == 0xffffffff && zipLCD.usize == 0xffffffff) {
					if (zipLCD.extralength >= 40) {
						//ZIP_EXLZH.ReadEXLZH0(zipExLZH, a);
						zipExLZH.ReadEXLZH(a);
						
						if (zipExLZH.cs > 0 && zipExLZH.us > 0 && zipExLZH.cs <= zipExLZH.us) {
							zipLCD.csize = zipExLZH.cs;
							zipLCD.usize = zipExLZH.us;
						}
					} else {
						a.read_pos += zipLCD.extralength;
					}
				} else {
					a.read_pos += zipLCD.extralength;
				}
				
				cnt_files++;
				
				{
					int saved = a.read_pos;
					a.read_pos = (int) zipLCD.offset;
					//ZIP_LZH.ReadLZH0(zipLZH, a);
					zipLZH.ReadLZH(a);
					a.read_pos += 
							zipLZH.extralength +
							zipLZH.namelength;
					zipLCD.offset = a.read_pos;					
					a.read_pos = saved;					
				}				
							
				AlFileZipEntry of = new AlFileZipEntry();

				of.compress = zipLCD.compressed;
				of.cSize = (int) zipLCD.csize;
				of.uSize = (int) zipLCD.usize;
				of.flag = zipLCD.flag;
				of.position = (int) zipLCD.offset;
				of.time = 0;

				AlIntHolder t = new AlIntHolder(0);

				StringBuilder newName = new StringBuilder();

				int cp = ((zipLCD.flag & (1 << 11)) != 0) ? TAL_CODE_PAGES.CP65001 : TAL_CODE_PAGES.CP1252;
				while (fname[t.value] != 0 && tmp < EngBookMyType.AL_MAX_FILENAME_LENGTH)
					newName.append(AlUnicode.byte2Wide(cp, fname, t));

				of.name = newName.toString();


				/*if (ext != null && (ext.equalsIgnoreCase(".odt") || ext.equalsIgnoreCase(".sxw")) && zipLCD.fName.equalsIgnoreCase(FIRSTNAME_ODT))
					res = ArchiveType.ODT;*/
				if ((ext == null || ext.equalsIgnoreCase(".odt") || ext.equalsIgnoreCase(".sxw")) && of.name.equalsIgnoreCase(AlFiles.LEVEL1_ZIP_FIRSTNAME_ODT))
					res = TAL_FILE_TYPE.ODT;
				if ((ext == null || ext.equalsIgnoreCase(".docx")) && of.name.equalsIgnoreCase(AlFiles.LEVEL1_ZIP_FIRSTNAME_DOCX))
					res = TAL_FILE_TYPE.DOCX;
				if ((ext == null || ext.equalsIgnoreCase(".epub")) && of.name.equalsIgnoreCase(AlFiles.LEVEL1_ZIP_FIRSTNAME_EPUB))
					res = TAL_FILE_TYPE.EPUB;
				if ((ext == null || ext.equalsIgnoreCase(".fb3")) && of.name.equalsIgnoreCase(AlFiles.LEVEL1_ZIP_FIRSTNAME_FB3))
					res = TAL_FILE_TYPE.FB3;
				if ((ext == null || ext.equalsIgnoreCase(".JEB"))) {
						res = TAL_FILE_TYPE.JEB;
					Integer fileSize = 0;
					if(of.compress == 8) {
						fileSize = fileSizeMap.get(of.name);
						if (fileSize != null && fileSize > 0) {
							of.uSize = fileSize;
						}
					}
				}

				fList.add(of);
				
				a.read_pos += zipLCD.commlength;			
			} else {
				a.read_pos += zipLCD.namelength + zipLCD.extralength + zipLCD.commlength;
			}
		}

		return cnt_files > 0 ? res : TAL_FILE_TYPE.TXT;
	}

	public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
		super.initState(file, myParent, fList);

		ident = "zip";

		if (file.length() > 0) {
			for (int i = 0; i < fileList.size(); i++) {
				if (fileList.get(i).name.contentEquals(file)) {
					fileName = file;
					size = fileList.get(i).uSize;
					zip_position = fileList.get(i).position;
					zip_compression = fileList.get(i).compress;
					zip_csize = fileList.get(i).cSize;
					zip_index = i;
					break;
				}
			}
		} 

		if (fileName.length() == 0) {
			for (int i = 0; i < fileList.size(); i++) {
				if (AlFiles.isValidExt(fileList.get(i).name)) {				
					fileName = fileList.get(i).name;
					size = fileList.get(i).uSize;
					zip_position = fileList.get(i).position;
					zip_compression = fileList.get(i).compress;
					zip_csize = fileList.get(i).cSize;
					zip_index = i;
					break;
				}
			}
		}

		if (fileName.length() == 0) {
			fileName = fileList.get(0).name;
			size = fileList.get(0).uSize;
			zip_position = fileList.get(0).position;
			zip_compression = fileList.get(0).compress;
			zip_csize = fileList.get(0).cSize;
			zip_index = 0;
		}

		if (file != null && file.contentEquals(LEVEL1_ZIP_FIRSTNAME_EPUB))
			fileName = null;
		/*if (file != null && file.contentEquals(LEVEL1_ZIP_FIRSTNAME_DOCX))
			fileName = null;*/
		//if (file == LEVEL1_ZIP_FIRSTNAME_EPUB)
		//	fileName.clear();

		return TAL_RESULT.OK;	
	}

	@Override
	public void finalize() throws Throwable {
		inflater = null;
		super.finalize();
	}

	@Override
	public void	needUnpackData() {
		if (useUnpack)
			return;
		useUnpack = true;	
		
		try {
			unpack_buffer = new byte[size];
		} catch (Exception e) {
			e.printStackTrace();
			unpack_buffer = null;
		}
		if (unpack_buffer != null) {
			getBuffer(0, unpack_buffer, size);
		} else {
			useUnpack = false;
		}
	}
	
	protected int getBuffer(int pos, byte[] dst, int cnt) {

		int tmp;
		int res = 0;
		
		if (zip_compression == 8) {
			
			if (pos < zip_total_out) {
				if (inflater != null) {
					inflater.reset();				
				}
				zip_total_out = zip_out_buff_size = 0;				
			}

			if (inflater == null) {
				inflater = new Inflater(true);
				inflater.reset();
			}
			
			while (res < cnt && pos < size) {
				if (pos >= zip_total_out && pos < (zip_total_out + zip_out_buff_size)) {
					tmp = Math.min((zip_total_out + zip_out_buff_size) - pos, cnt - res);
					System.arraycopy(zip_out_buff, pos - zip_total_out, dst, res, tmp);
					res += tmp; 
					pos += tmp;
				} else {
					zip_total_out += zip_out_buff_size;

					if (inflater.needsInput()) {
						zip_in_buff_size = //parent.getBuffer(
								parent.getByteBuffer(
									inflater.getTotalIn() + zip_position, zip_in_buff, ZIP_CHUNK_SIZE);
						inflater.setInput(zip_in_buff, 0, zip_in_buff_size);
					}
					
					try {
						zip_out_buff_size = inflater.inflate(zip_out_buff, 0, ZIP_CHUNK_SIZE);
						
						if (zip_out_buff_size == 0 && inflater.finished()) {
							zip_out_buff_size = zip_out_buff.length;
							for (int err = 0; err < zip_out_buff_size; err++)
								zip_out_buff[err] = 0x00;
						}

					} catch (DataFormatException e) {
						zip_out_buff_size = zip_out_buff.length;
						for (int err = 0; err < zip_out_buff_size; err++)
							zip_out_buff[err] = 0x00;
						e.printStackTrace();
					}

				}
			}
		} else 
		if (zip_compression == 0) {
			res = parent.getBuffer(zip_position + pos, dst, Math.min(cnt, zip_csize - pos));
		} else {
			for (res = 0; res < Math.min(cnt, zip_csize - pos); res++)
				dst[res] = 0x00;
		}

		return res;
	}

	private byte[] in_external_buff = null;
	private byte[] out_external_buff = null;
	private Inflater external_infl = null;

	@Override
	public boolean fillBufFromExternalFile(int num, int pos, byte[] dst, int dst_pos, int cnt) {
		int res = 0;

		if (num >= 0 && num < fileList.size()) {

			if (fileName != null && fileList.get(num).name.contentEquals(fileName)) {
				res = getByteBuffer(pos, dst, dst_pos, cnt);
			} else {

				if (fileList.get(num).compress == 8) {
					if (external_infl == null)
						external_infl = new Inflater(true);
					external_infl.reset();

					int	total_out, in_buff_size, out_buff_size, tmp;

					if (in_external_buff == null)
						in_external_buff = new byte [ZIP_CHUNK_SIZE];
					if (out_external_buff == null)
						out_external_buff = new byte [ZIP_CHUNK_SIZE];

					int	position = fileList.get(num).position;

					external_infl.reset();
					total_out = out_buff_size = 0;

					while (res < cnt && pos < fileList.get(num).uSize) {
						if (pos >= total_out && pos < (total_out + out_buff_size)) {
							tmp = Math.min((total_out + out_buff_size) - pos, cnt - res);
							System.arraycopy(out_external_buff, pos - total_out, dst, res + dst_pos, tmp);
							res += tmp;
							pos += tmp;
						} else {
							total_out += out_buff_size;

							if (external_infl.needsInput()) {
								in_buff_size = //parent.getBuffer(
										parent.getByteBuffer(
												external_infl.getTotalIn() + position, in_external_buff, ZIP_CHUNK_SIZE);
								external_infl.setInput(in_external_buff, 0, in_buff_size);
							}

							try {
								out_buff_size = external_infl.inflate(out_external_buff, 0, ZIP_CHUNK_SIZE);

								if (out_buff_size == 0 && external_infl.finished()) {
									out_buff_size = out_external_buff.length;
									for (int err = 0; err < out_buff_size; err++)
										out_external_buff[err] = 0x00;
								}

							} catch (DataFormatException e) {
								out_buff_size = out_external_buff.length;
								for (int err = 0; err < out_buff_size; err++)
									out_external_buff[err] = 0x00;
								e.printStackTrace();
							}

						}
					}

					//external_infl.finished();
					//external_infl = null;

				} else if (fileList.get(num).compress == 0) {
					res = parent.getByteBuffer(fileList.get(num).position + pos, dst, dst_pos, cnt);
				}
			}
		}
		return res == cnt;
	}
}
