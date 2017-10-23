package com.onyx.android.sun.utils;

import java.security.MessageDigest;

public class MD5Utils {

	public static String encode(String pwd) {
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");

			byte[] digest = digester.digest(pwd.getBytes());

			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int i = b & 0xff;
				String hex = Integer.toHexString(i);
				if (hex.length() == 1) {
					hex = 0 + hex;
				}
				sb.append(hex);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
