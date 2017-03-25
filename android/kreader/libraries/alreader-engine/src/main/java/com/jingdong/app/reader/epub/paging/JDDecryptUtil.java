package com.jingdong.app.reader.epub.paging;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class JDDecryptUtil {

    public static String key = "0001f3V2qPZfClyfcvAHTXmpVC1LWlBaOFpvWktaa1pLWnBabFpPWmtaS1puekR6Ylo5em9aTFphYTlaa1pLWktaS1pRQTJ6ckFuYTlhb2F0QW96eEFOWmF6OGFVekR6V2FrYTh6alo4emxaVlp6Wk9aWVpsenF6cXppYVV6WXoxQVZ6NXowemp6aXp6elB6YXppejV6dHo3elV6THpQekN6d3o1enJ6anpVWnB6a1prejJ6NXpxekt6aVpwemtaa3pTejV6dHo3elV6TXprejF6M1psekl6anpVemR6UFprejN6NXpDemF6ZXo1elB6Q3pXejB6VnpGelp6dHp5enl6VXowelZ6anpVekh6a3pxeml6MHpJemZ6WnpvekN6Y3pVWmx6eno3elV6cnpQekN6NXowekl6M3paemh6UHpxeld6MHpyemF6WlpMelB6S3pTenJaN3o3eml6WnpQejF6aVpsenJ6UXppelB6Q3pGemZ6NVo3ejd6aVpMelBacHo1Wk56eXphelp6dnpQemF6dg==<HS>00448jcN+CqUs3XvNMKMwVooc668OCU+bPQv2bHHZED2uz0=";
    public static String deviceUUID = "ZCp2tdYiKmKfRMstbUmJDC1kQTRaaEFVWnZ6WmFrYUN6N2FyQUJhWEFWYUR6ZWFyQVZhUEFmQWthb2FiQTNBeA==";
    public static String random = "0001X2BNm/ZdEVyEK/BtbWWSIy1PQUpBTWEyYXRhZlpaYXc=";

    private static final int InputLength = 256;
    /**
     * 解密文件
     *
     * @param inputStream 文件路径
     * @return
     */
    public synchronized static int getDecryptFileSize(InputStream inputStream,int fileLength) {
        if (0 != DecryptHelper.initDecryptLibrary(key, deviceUUID, random)) {
            return -1;
        }
        int fileSize = 0;
        try {
            byte[] output = new byte[InputLength * 32];//8k
            byte[] buff = new byte[InputLength];//256b
            int rc = 0;
            int endLabel = 0;
            int outputLength = 0;
            int cLength = -1;
            while ((rc = inputStream.read(buff, 0, InputLength)) > 0) {
                if (fileLength - InputLength > 0) {
                    fileLength -= InputLength;
                    endLabel = 0;
                } else {
                    endLabel = 1;
                }

                outputLength = DecryptHelper.decrypt(buff, rc, output, output.length, endLabel);
                if (outputLength < 0) {
                    break;
                }

                outputLength -= cLength;
                cLength += outputLength;
                fileSize = cLength;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DecryptHelper.close();
        }
        return fileSize;
    }

    public synchronized static InputStream decryptFile(String filename) {
        if (0 != DecryptHelper.initDecryptLibrary(key, deviceUUID, random)) {
            return null;
        }

        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {

            File file = new File(filename);
            if (!file.exists()) {
                return null;
            }
            is = new FileInputStream(file);
            os = new ByteArrayOutputStream();


            byte[] output = new byte[InputLength * 32];//8k
            byte[] buff = new byte[InputLength];//256b
            long fileLength = file.length();
            int rc = 0;
            int endLabel = 0;
            int outputLength = 0;
            int clength = 0;
            while ((rc = is.read(buff, 0, InputLength)) > 0) {
                if (fileLength - InputLength > 0) {
                    fileLength -= InputLength;
                    endLabel = 0;
                } else {
                    endLabel = 1;
                }

                outputLength = DecryptHelper.decrypt(buff, rc, output, output.length, endLabel);
                if (outputLength < 0) {
                    break;
                }
                outputLength -= clength;
                os.write(output, 0, outputLength);
                clength += outputLength;
            }
            return new ByteArrayInputStream(os.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            }catch (Exception e){

            }
            DecryptHelper.close();
        }
        return null;
    }
}
