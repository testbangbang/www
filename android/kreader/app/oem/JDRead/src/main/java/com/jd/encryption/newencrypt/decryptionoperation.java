package com.jd.encryption.newencrypt;

/**
 * Created by guohangying on 2017/12/4.
 */
//com_jd_encryption_newencrypt_decryptionoperation_encrypting
public class decryptionoperation {
    static {
        System.loadLibrary("decryptionoperation");
    }
    public static native String encrypting(String strContent, String strPath,int iType);
    public static native String decryption(String strByte, String strPath,int iType);

    //加密函数
    //strContent,待加密数据
    //strPath，传输路径，详细功能咨询接口提供人
    //iType 0为Aes
    //返回值,为加密后的数据
    public static String atencrypting(String strContent, String strPath,int iType)
    {
        return encrypting(strContent,strPath,iType);
       // String t = new String(encrypting(strContent));
       // return t;
    }

    //解密函数
    //strByte,已加密数据
    //strPath，传输路径，详细功能咨询接口提供人
    //iType 0为Aes
    //返回值,解密后的数据
    public static String atdecryption(String strByte, String strPath,int iType)
    {
        return decryption(strByte,strPath,iType);
    }

}

/**调用方式
 *
 import com.jd.encryption.newencrypt.decryptionoperation;
 strbyte = decryptionoperation.atencrypting("ceshi","//path//file",0);
 decryptionoperation.atdecryption(strbyte,"//path//file",0);

 **/