/**
 *
 */
package com.onyx.kreader.tagus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.onyx.android.sdk.data.utils.HexStringUtil;

/**
 * @author jim
 * Created by jim on 17-4-20.
 *
 */
public class TagusZipPasswordUtil {

    private final static String KEY_ZIP_PREFIX = "2g_LO7x.5v";
    private final static String KEY_ZIP_SUFFIX = "6ksN;i-4*e";
    private final static String ALGORITHM = "MD5";

    public static String calculateDecryptionKey(String token, String user_id, String bookISBN) {
        String str1 = KEY_ZIP_PREFIX + bookISBN + user_id + KEY_ZIP_SUFFIX;
        String str2 = KEY_ZIP_PREFIX + token + KEY_ZIP_SUFFIX;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(ALGORITHM);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String md5Str1 = HexStringUtil.getHexString(digest.digest(str1.getBytes()));
        String md5Str2 = HexStringUtil.getHexString(digest.digest(str2.getBytes()));
        return md5Str1+md5Str2;
    }

    public static String getZipFilePassword(TagusDocumentCrypto crypto, String bookISBN) {
        String result = calculateDecryptionKey(crypto.getParamOne(), crypto.getParamTwo(), bookISBN);
        return result;
    }

}
