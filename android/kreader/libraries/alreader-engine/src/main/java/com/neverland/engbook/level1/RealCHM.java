package com.neverland.engbook.level1;

import java.util.ArrayList;

public class RealCHM {

	static {
		System.loadLibrary("CHMlibrary");
	}

	private static native int		fchmOpen(String uri);

	private static native int 		fchmClose(int Handle);

	private static native byte[] 	fchmGetFileData(int Handle, String uri, int size);

	private static ArrayList<AlFileZipEntry> fileList = null;


	public void attachFList(ArrayList<AlFileZipEntry> fl) {
		fileList = fl;
	}

	public int openRealFile(String fn) {
		enumerateFiles(null, -1);
		return fchmOpen(fn);
	}

	public int closeRealFile(int Handle) {
		if (Handle != 0)
			return fchmClose(Handle);
		return 0;

	}

	public static void enumerateFiles(String name, int len) {
		if (len < 0)
			return;

		AlFileZipEntry a = new AlFileZipEntry();

		a.name = name;
        a.cSize = a.uSize = len;
		a.flag = a.position = a.compress = -1;

		if (name.toLowerCase().endsWith(".htm")) {
			a.flag = 1;
		} else
		if (name.toLowerCase().endsWith(".html")) {
			a.flag = 1;
		}

		fileList.add(a);
	}

	protected int getPointBuffer(int Handle, String fileName, int fileSize, int src_start, byte[] dst, int dst_start, int cnt) {
		byte[] data = fchmGetFileData(Handle, fileName, fileSize);
		System.arraycopy(data, src_start, dst, dst_start, cnt);
		return cnt;
	}
}


