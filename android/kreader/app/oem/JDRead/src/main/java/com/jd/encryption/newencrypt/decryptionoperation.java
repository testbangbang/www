package com.jd.encryption.newencrypt;

/**
 * Created by guohangying on 2017/12/4.
 */
//com_jd_encryption_newencrypt_decryptionoperation_encrypting
public class decryptionoperation {
    static {
        System.loadLibrary("decryptionoperation");
    }

    public static native byte[] encrypting(String strContent, String strPath);

    public static native String decryption(byte[] strByte, String strPath);

    //加密函数
    //strContent,待加密数据
    //strPath，传输路径，详细功能咨询接口提供人
    //返回值,为加密后的数据
    public static byte[] atencrypting(String strContent, String strPath) {
        return encrypting(strContent, strPath);
    }

    //解密函数
    //strByte,已加密数据
    //strPath，传输路径，详细功能咨询接口提供人
    //返回值,解密后的数据
    public static String atdecryption(byte[] strByte, String strPath) {
        return decryption(strByte, strPath);
    }
}