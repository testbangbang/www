package com.neverland.engbook.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class TTFScan {
	private TTFScan() {
		
	}
	
	private static int getUInt16Buff(byte[] b, int offset) {
		int    res = 0;
		res = (b[offset++] & 0xff) << 8;
		res |= (b[offset] & 0xff);
		return res;
	}
	
	private static int getUInt32Buff(byte[] b, int offset) {
		int    res = 0;
		res  = (b[offset++] & 0xff) << 24;
		res |= (b[offset++] & 0xff) << 16;
		res |= (b[offset++] & 0xff) << 8;
		res |= (b[offset++] & 0xff);
		return res;
	}
	
	private static TTFInfo decodeName(String name) {
		TTFInfo ttfi = new TTFInfo();
		
		String n = name.toLowerCase();
		
		if (n.endsWith("_b.ttf")) {
				ttfi.Name = name.substring(0, name.length() - 6);
				ttfi.Type += 1;
		} else
			if (n.endsWith("-b.ttf")) {
				ttfi.Name = name.substring(0, name.length() - 6);
				ttfi.Type += 1;
			} else	
			if (n.endsWith("-bold.ttf")) {
				ttfi.Name = name.substring(0, name.length() - 9);
				ttfi.Type += 1;
			} else	
		if (n.endsWith("_i.ttf")) {
			ttfi.Name = name.substring(0, name.length() - 6);
			ttfi.Type += 2;
		} else
			if (n.endsWith("-i.ttf")) {
				ttfi.Name = name.substring(0, name.length() - 6);
				ttfi.Type += 2;
			} else
			if (n.endsWith("-italic.ttf")) {
				ttfi.Name = name.substring(0, name.length() - 11);
				ttfi.Type += 2;
			} else
		if (n.endsWith("_bi.ttf") || n.endsWith("_ib.ttf")) {
			ttfi.Name = name.substring(0, name.length() - 7);
			ttfi.Type += 3; 
		} else
			if (n.endsWith("-bi.ttf") || n.endsWith("-ib.ttf")) {
				ttfi.Name = name.substring(0, name.length() - 7);
				ttfi.Type += 3;
			} else
			if (n.endsWith("-bolditalic.ttf") || n.endsWith("-italicbold.ttf")) {
				ttfi.Name = name.substring(0, name.length() - 15);
				ttfi.Type += 3;			
			} else
		if (n.endsWith("-regular.ttf")) {
			ttfi.Name = name.substring(0, name.length() - 12);
		} else			
			if (n.endsWith("-normal.ttf")) {
				ttfi.Name = name.substring(0, name.length() - 11);
			} else
		{
			ttfi.Name = name.substring(0, name.length() - 4);
		}
		
		
		if (ttfi.Name != null && ttfi.Name.length() > 0)
			return ttfi;
		
		return null;
	}

	private static boolean decodeTableByPlatphormId(byte[] buff, TTFInfo ttfi, int PId) {
		int cnt_rec  = getUInt16Buff(buff, 2);
		int str_stor = getUInt16Buff(buff, 4);

		boolean flag1 = true, flag2 = true;
		int i, PlatformId, PlatfornEnc, LangId, NameId, StrLen, StrOff;
		for (i = 6; i < cnt_rec * 12; i += 12) {
			PlatformId = getUInt16Buff(buff, i);
			PlatfornEnc = getUInt16Buff(buff, i + 2);
			LangId = getUInt16Buff(buff, i + 4);
			NameId = getUInt16Buff(buff, i + 6);
			StrLen = getUInt16Buff(buff, i + 8);
			StrOff = getUInt16Buff(buff, i + 10);

			if (PId != -1 && PlatformId != PId)
				continue;

			switch (NameId) {
				case 1:
					if (ttfi.Name == null || (PlatformId == 3 && flag1)) {
						try {
							String s = new String( buff, str_stor + StrOff, StrLen,
									PlatformId == 3 ? "UTF-16BE" : "US-ASCII");;
							if (ttfi.Name != null && flag1 && ttfi.Name.equalsIgnoreCase(s)) {
								flag1 = false;
							} else {
								ttfi.Name = s;
							}
							Log.e("FONT TABLE name ", s);
						} catch (UnsupportedEncodingException e) {
							ttfi.Name = null;
						}
					}
					break;
				case 2:
					if (flag2) {
						try {
							String s = new String( buff, str_stor + StrOff, StrLen,
									PlatformId == 3 ? "UTF-16BE" : "US-ASCII");
							ttfi.Type = 0;
							s = s.toLowerCase();

							String[] arr = s.split(" ");
							for (String ss : arr) {

								if (ss.indexOf("bold") >= 0) {
									ttfi.Type |= 1;
									if (ss.indexOf("italic") >= 0 || ss.indexOf("oblique") >= 0)
										ttfi.Type |= 2;
								} else
								if (ss.indexOf("italic") >= 0 || ss.indexOf("oblique") >= 0) {
									ttfi.Type |= 2;
								} else
								if (ss.indexOf("normal") >= 0 || s.indexOf("regular") >= 0) {

								} else {
									if (ttfi.AddonName == null)
										ttfi.AddonName = new ArrayList<>();
									ttfi.AddonName.add(ss);
									ttfi.Type |= 4;
								}
							}

							flag2 = false;

							Log.e("FONT TABLE type ", s);
						} catch (UnsupportedEncodingException e) {}
					}
					break;
			}
			if (!flag1 && !flag2)
				break;
		}

		if (ttfi.Name != null) {
			Log.e("FONT TABLE result ", ttfi.Name + ',' + Integer.toString(ttfi.Type));
			return true;
		}

		return false;
	}

	private static TTFInfo decodeTable(byte[] buff) {
		if (getUInt16Buff(buff, 0) != 0x00)
			return null;

		TTFInfo ttfi = new TTFInfo();

		if (decodeTableByPlatphormId(buff, ttfi, 3))
			return ttfi;

		if (decodeTableByPlatphormId(buff, ttfi, 1))
			return ttfi;

		if (decodeTableByPlatphormId(buff, ttfi, -1))
			return ttfi;

		return null;
	}
	
	public static TTFInfo getTTFInfo(File f, boolean scanByName) {
		FileInputStream is = null;
		try {
			is = new FileInputStream(f);
			int flen = (int) f.length();

			byte[] buff0 = new byte[12];
			is.read(buff0);

			int version = getUInt32Buff(buff0, 0);
			if (version != 0x00010000 && version != 0x4f54544f)
				return null;

			int cnt_tbl = getUInt16Buff(buff0, 4) << 4;

			byte[] buff1 = new byte[cnt_tbl];
			if (12 + cnt_tbl >= flen)
				return null;
			is.read(buff1);

			int i, need_skip, len_name;
			for (i = 0; i < cnt_tbl; ) {
				version = getUInt32Buff(buff1, i);
				if (version == 0x6e616d65) {
					need_skip = getUInt32Buff(buff1, i + 8);
					len_name = getUInt32Buff(buff1, i + 12);

					if (need_skip + len_name >= flen)
						return null;

					need_skip -= 12 + cnt_tbl;
					if (is.skip(need_skip) != need_skip)
						return null;

					buff1 = new byte[len_name];
					if (is.read(buff1) != len_name)
						return null;
					if (scanByName)
						return decodeName(f.getName());
					return decodeTable(buff1);
				}
				i += 16;
			}

		} catch (IOException e) {
			Log.e("error font", f.getPath());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {

				}
			}
		}

		return null;
	}
}
